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

            printGraph();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileSource.removeSink(graph);
        }
    }

    /**
     * This method will write the graph that is currently in the system to a dot file.
     * This graph will have the dummyRoot removed.
     */
    public void outputProcessing(String filePath) throws IOException {

        //This is needed to remove the dummyRoot and its edges from the graph
        Node dummyNodeToRemove = graph.removeNode("dummyRoot");

        try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.dot")))){
            out.write("digraph {");
            out.newLine();
//            for(Edge e:d.getEdges()){
//                out.write(e.v+" -> "+e.w);
//                out.newLine();
//            }
            out.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method prints out the graph.
     * It is for testing purposes only, will be removed in a later commit.
     * @author Megan
     */
    private void printGraph() {
        //This is to print out the graph
        for (Node node : graph) {
            System.out.println(node.toString() + node.getAttribute("Weight") + "    " + node.enteringEdges());

            for(Edge edge:node)
            {
                System.out.println(edge + "\t Weight: " + edge.getAttribute("Weight"));
            }
        }
    }

}
