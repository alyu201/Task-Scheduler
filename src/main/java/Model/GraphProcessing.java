package Model;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
This class is used for processing input dot file and output dot file.
 @author kelvi and Megan
 */
public class GraphProcessing {

    private Graph graph = new DefaultGraph("graph");

    /**
     * This method takes a path to a dot file and uses the GraphStream library to convert the file content to a graph.
     * A dummy root will also be added to this graph in case there are multiple roots.
     * @throws IOException
     * @author Kelvin and Megan
     */
    public void inputProcessing(String filePath) throws IOException {
      
        FileSource fileSource = FileSourceFactory.sourceFor(filePath);

        //Keep a list of the original root nodes of the graph
        ArrayList<Node> listOfOriginalRoots = new ArrayList<Node>();

        //Add a sink to listen to all the graph events that come from the input dot file
        fileSource.addSink(this.graph);

        try {
            fileSource.readAll(filePath);

            //This for loop is to find the root node(s) ONLY
            for (Node node : graph) {
                //If we found the original root nodes, add it to the listOfOriginalRoots
                if (node.getInDegree() == 0) {
                    listOfOriginalRoots.add(node);
                }
            }

            //Adding a dummy node to the graph and setting its weight to 0.
            //Note: This dummyRootNode will NOT be in the listOfOriginalRoots.
            Node dummyRootNode = graph.addNode("dummyRoot");
            dummyRootNode.setAttribute("Weight", 0);


            //Adding edges of weight 0 from the dummyRootNode to each of the listOfOriginalRoots
            for(Node originalRoot : listOfOriginalRoots) {
                String edgeName = "(" + dummyRootNode.toString() + ";" + originalRoot.toString() + ")";
                Edge edge = graph.addEdge(edgeName, dummyRootNode, originalRoot, true);
                edge.setAttribute("Weight", 0);
            }

            //Calculating all the bottomLevels of all nodes by starting at the dummyRootNode
            //Note that the dummyRoot's bottom level is
            // already calculated and set as an attribute in this method too
            int dummyRootBL = calBottomLevels(dummyRootNode);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileSource.removeSink(graph);
        }
    }

    /**
     * This method will write the graph that is currently in the system to a dot file.
     * This graph will have the dummyRoot and its edges removed.
     * @author: Kelvin
     */
    public void outputProcessing(String filePath) throws IOException {

        String outputFilename = filePath.concat(".dot");
        try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilename)))){
            out.write("digraph \""+filePath+"\" "+"{");

            out.newLine();
            //writing nodes one by one to the file
            for (Node node : graph) {
                String nodeWeight = node.getAttribute("Weight").toString();
                out.write(node.toString()+" ["+"Weight="+nodeWeight+"];");
                out.newLine();

                //writing out going edges one by one to the file
                node.leavingEdges().forEach(edge -> {
                    String edgeUnformatted = edge.toString();
                    int subStart=edgeUnformatted.indexOf("[")+1;
                    int subEnd=edgeUnformatted.length()-1;
                    String edgeFormatted = edgeUnformatted.substring(subStart,subEnd);
                    String edgeWeight = edge.getAttribute("Weight").toString();
                    try {
                        out.write(edgeFormatted+" ["+"Weight="+edgeWeight+"];");
                        out.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            out.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method will first be given the dummyRootNode and recursion will be used to
     * calculate the bottom level of all the nodes of the graph.
     * Returns the bottom level of the dummyRootNode - even though by then the
     * dummyRootNode's bottom level would have already been set as an attribute.
     * @author Megan
     */
    private int calBottomLevels(Node node) {

        int currentNodeWeight = (Double.valueOf(node.getAttribute("Weight").toString())).intValue();
        int numberOfChildren = node.getOutDegree();

        // If this is a leaf, save and return its weight
        if (numberOfChildren == 0) {
            node.setAttribute("BottomLevel", currentNodeWeight);
            return currentNodeWeight;
        }

        int largestChildBLSoFar = 0;

        //For each of its children, calculate their bottom level recursively.
        //Keep track of the largest bottom level among all the children's bottom levels.
        for (int i = 0; i < numberOfChildren; i++) {
            Edge edge = node.getLeavingEdge(i);
            Node currentChild = edge.getTargetNode();
            int currentChildBL = calBottomLevels(currentChild);

            if (largestChildBLSoFar < currentChildBL) {
                largestChildBLSoFar = currentChildBL;
            }
        }

        // Save and return the largestChild's bottom level PLUS the currentNodeWeight
        int bottomLevel = largestChildBLSoFar + currentNodeWeight;
        node.setAttribute("BottomLevel", bottomLevel);
        return bottomLevel;
    }

    public Graph getGraph() {
        return graph;
    }

}
