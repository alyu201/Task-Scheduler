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
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

/**
 * This class is a singleton class that is used for processing input dot file and output dot file.
 * @author Kelvin Shen and Megan Lim
 *
 */

public class GraphProcessing {
    private Logger _logger = Logger.getLogger(GraphProcessing.class.getName());


    // static variable single_instance of type Singleton
    private static GraphProcessing _single_instance = null;
    private Graph _graph;


    /**
     *private constructor restricted to this class itself
      */
    private GraphProcessing() {
        _graph = new DefaultGraph("graph");
    }


    /**
     * static method to create instance of GraphProcessing class
     */
    public static GraphProcessing Graphprocessing() {
        // To ensure only one instance is created
        if (_single_instance == null) {
            _single_instance = new GraphProcessing();
        }
        return _single_instance;
    }

    /**
     * This method returns a graph when invoked.
     */
    public Graph getGraph(){
        return _graph;
    }


    /**
     * This method takes a path to a dot file and uses the GraphStream library to convert the file content to a graph.
     * A dummy root will also be added to this graph in case there are multiple roots.
     *
     * @throws IOException
     * @author Kelvin Shen and Megan Lim
     */
    public void inputProcessing(String filePath) throws IOException{
        // Set the graph style
        String graphStyle = "node {"
                + "size: 25px;"
                + "fill-color: dimgray;"
                + "text-size: 15;"
                + "text-alignment: at-right;"
                + "text-background-mode: rounded-box;"
                + "text-offset: 8, -15;"
                + "text-padding: 5;"
                + "text-color: dimgray;"
                + "z-index: 1;"
                + "}"

                + "node.default {"
                + "fill-color: dimgray;"
                + "}"
                + "node.proc1 {"
                + "fill-color: #ffb3ba;"
                + "}"
                + "node.proc2 {"
                + "fill-color: #ffd288;"
                + "}"
                + "node.proc3 {"
                + "fill-color: #ffecbb;"
                + "}"
                + "node.proc4 {"
                + "fill-color: #cbedc9;"
                + "}"
                + "node.proc5 {"
                + "fill-color: #ccf2fe;"
                + "}"
                + "node.proc6 {"
                + "fill-color: #c4d4ff;"
                + "}"
                + "node.proc7 {"
                + "fill-color: #e6d3fe;"
                + "}"
                + "node.proc8 {"
                + "fill-color: #f5d5e6;"
                + "}"

                + "graph {"
                + "padding: 60;"
                + "}"

                + "edge {"
                + "fill-color: dimgray;"
                + "text-size: 15;"
                + "text-alignment: center;"
                + "text-background-mode: rounded-box;"
                + "text-offset: 0, 2;"
                + "text-padding: 5;"
                + "text-color: dimgray;"
                + "z-index: 0;"
                + "}";
        _graph.setAttribute("ui.stylesheet", graphStyle);

        //Keep a list of the original root nodes of the graph
        ArrayList<Node> listOfOriginalRoots = new ArrayList<Node>();

        try {
            FileSource fileSource = FileSourceFactory.sourceFor(filePath);

            //Add a sink to listen to all the graph events that come from the input dot file
            fileSource.addSink(this._graph);
            fileSource.readAll(filePath);

            //This for loop is to find the root node(s) ONLY
            for (Node node : _graph) {
                //If we found the original root nodes, add it to the listOfOriginalRoots
                if (node.getInDegree() == 0) {
                    listOfOriginalRoots.add(node);
                }
                node.setAttribute("ui.label", "Node: " + node.getId() + "\n" + node.getAttribute("Weight"));
            }

            //Adding a dummy node to the graph and setting its weight to 0.
            //Note: This dummyRootNode will NOT be in the listOfOriginalRoots.
            Node dummyRootNode = _graph.addNode("dummyRoot");
            dummyRootNode.setAttribute("Weight", 0);


            //Adding edges of weight 0 from the dummyRootNode to each of the listOfOriginalRoots
            for (Node originalRoot : listOfOriginalRoots) {
                String edgeName = "(" + dummyRootNode.toString() + ";" + originalRoot.toString() + ")";
                Edge edge = _graph.addEdge(edgeName, dummyRootNode, originalRoot, true);
                edge.setAttribute("Weight", 0);
                edge.setAttribute("ui.label", edge.getAttribute("Weight"));
            }

            //Calculating all the bottomLevels of all nodes by starting at the dummyRootNode
            //Note that the dummyRoot's bottom level is
            // already calculated and set as an attribute in this method too
            int dummyRootBL = calBottomLevels(dummyRootNode);

            fileSource.removeSink(_graph);
            System.out.println(_graph);


        } catch (IOException e) {
            _logger.info("Ensure your dot file is in the same directory as the jar file!");
            System.exit(0);
        }
    }

    /**
     * This method will write the graph that is currently in the system to a dot file.
     * This graph will have the dummyRoot and its edges removed.
     * @author Kelvin Shen
     *
     */
    public void outputProcessing(String filePath, State state) throws IOException {

        String outputFilename = filePath.concat(".dot");
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilename)))) {
            out.write("digraph \"" + filePath + "\" " + "{");
            out.newLine();

            HashMap<Integer, HashMap<Integer, Node>> schedule = state.getState();
            Set<Integer> key = state.procKeys();
            //writing nodes one by one to the file
            for (Node node : _graph) {
                if (node.getId().equals("dummyRoot")) {
                    break;
                }

                //getting the start time and process scheduled on
                String[] temp = nodeDetail(schedule, key, node);
                String startingTime = temp[0];
                String process = temp[1];

                String nodeWeight = node.getAttribute("Weight").toString();
                out.write(node.toString() + " [" + "Weight=" + nodeWeight + ", Start=" + startingTime + ", Processor=" + process + "];");
                out.newLine();
                
                //writing out going edges one by one to the file
                node.leavingEdges().forEach(edge -> {
                    String edgeUnformatted = edge.toString();
                    int subStart = edgeUnformatted.indexOf("[") + 1;
                    int subEnd = edgeUnformatted.length() - 1;
                    String edgeFormatted = edgeUnformatted.substring(subStart, subEnd);
                    String edgeWeight = edge.getAttribute("Weight").toString();
                    try {
                        out.write(edgeFormatted + " [" + "Weight=" + edgeWeight + "];");
                        out.newLine();
                    } catch (IOException e) {
                        _logger.info("Unsuccessful output the schedule.");
                    }
                });
            }
            out.write("}");
        } catch (IOException e) {
            _logger.info("Unsuccessful output the schedule.");
        }catch(NullPointerException e1){
            _logger.info("The program did not came up with an schedule!");
        }

    }

    /**
     * This method is a helper function that gets the starting time and processor of a task scheduled
     * @author kelvin
     */
    public String[] nodeDetail(HashMap<Integer, HashMap<Integer, Node>> schedule, Set<Integer> key, Node node) {
        //getting the start time and process scheduled on
        String[] output = new String[2];
        for (Integer processor : key) {
            HashMap<Integer, Node> partialSchedule = schedule.get(processor);
            if (partialSchedule.containsValue(node)) {
                for (Integer nodeKey : partialSchedule.keySet()) {
                    Node tempNode = partialSchedule.get(nodeKey);
                    if (tempNode.equals(node)) {
                        output[0] = nodeKey.toString();
                        output[1] = processor.toString();
                        break;
                    }
                }
            }
        }
        return output;
    }

    /**
     * This method will first be given the dummyRootNode and recursion will be used to
     * calculate the bottom level of all the nodes of the graph.
     * Returns the bottom level of the dummyRootNode - even though by then the
     * dummyRootNode's bottom level would have already been set as an attribute.
     * @author Megan Lim
     *
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
}
