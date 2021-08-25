package AutomatedTesting;

import Model.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class AutomatedTester {

    @Test
    public void testingScheduler() {

        // Getting the desired dotfile and its details
        DotFileTestCase dotFileTestCase = DotFileTestCase.NODES7_PROC2_TWOTREE;
        String filePathTestCase = dotFileTestCase.getFilePath();
        int numProcTestCaseUse = dotFileTestCase.getNumOfProcUsed();
        int optimalSolTestCase = dotFileTestCase.getOptimalSol();

        // Generating the graph
        GraphProcessing graphProcessing = GraphProcessing.Graphprocessing();
        try {
            graphProcessing.inputProcessing(filePathTestCase);
        } catch (IOException e) {
            System.out.println("Make sure your dot file is in the same directory level as the 'src' folder!");
            e.printStackTrace();
        }
        Graph graph = graphProcessing.getGraph();

        Node nodeA = graph.getNode("a");
        Node nodeB = graph.getNode("b");
        Node nodeC = graph.getNode("c");
        Node nodeD = graph.getNode("d");
        System.out.println("This graph has " + graph.getNodeCount() + " nodes:");
        for (int nodeIndex = 0; nodeIndex < graph.getNodeCount(); nodeIndex++) {
            Node node = graph.getNode(nodeIndex);
            System.out.println("\t Node: " + node + " (Weight: " + node.getAttribute("Weight") + ")");
        }

        // Temporary number of threads for now
        int numOfThreads = 6;

        // Taking care of whether we are using A* or BnB to generate an outputState
        State outputState;
        if (graph.getNodeCount() > 11 || (graph.getNodeCount() == 11 && numOfThreads > 5)) {
            System.out.println("Running Branch and Bound ......");
            BranchAndBoundScheduler scheduler = new BranchAndBoundScheduler(graph, numProcTestCaseUse);
            outputState = scheduler.generateSchedule();
        } else {
            System.out.println("Running A-Star ......");
            AStarScheduler scheduler = new AStarScheduler(graph, numProcTestCaseUse);
            outputState = scheduler.generateSchedule();
        }
        System.out.println("outputState: " + outputState);

        // Getting the HashMap representation
        HashMap<Integer, HashMap<Integer, Node>> stateHashMap = outputState.getState();
        System.out.println("stateHashMap: " + stateHashMap);

        // Checking on the finishing time
        FinishingTimeHelper finishingTimeHelper = new FinishingTimeHelper(stateHashMap);
        int generatedFinTime = finishingTimeHelper.returnLatestFinishingTime();
        assertEquals(generatedFinTime, optimalSolTestCase);

        // Checking whether valid schedule or not
        ValidityChecker validityChecker = new ValidityChecker(stateHashMap);
        boolean validOrNot = validityChecker.checkValidity();
        assertTrue(validOrNot);

    }

    /**
     * This creates another good state to test with.
     * There are tasks in both processors #1 and #2.
     * @return good state
     */
    private static Graph creatingGoodGraph() {
        /***** Setting up the a graph to test this helper for now *****/
        // Graph stuff
        Graph graph = new DefaultGraph("graph");
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        Node nodeA = graph.getNode("A");
        Node nodeB = graph.getNode("B");
        Node nodeC = graph.getNode("C");
        Node nodeD = graph.getNode("D");
        nodeA.setAttribute("Weight", 2);
        nodeB.setAttribute("Weight", 2);
        nodeC.setAttribute("Weight", 3);
        nodeD.setAttribute("Weight", 2);

        // Edges
        Edge edgeAB = graph.addEdge("AB", nodeA, nodeB, true);
        edgeAB.setAttribute("Weight", 2);
        Edge edgeAC = graph.addEdge("AC", nodeA, nodeC, true);
        edgeAC.setAttribute("Weight", 1);
        Edge edgeBD = graph.addEdge("BD", nodeB, nodeD, true);
        edgeBD.setAttribute("Weight", 2);
        Edge edgeCD = graph.addEdge("CD", nodeC, nodeD, true);
        edgeCD.setAttribute("Weight", 1);


        return graph;
    }

    /**
     * This creates another good state to test with.
     * There are tasks in both processors #1 and #2.
     * @return good state
     */
    private static HashMap<Integer, HashMap<Integer, Node>> creatingGoodState() {
        /***** Setting up the a state to test this helper for now *****/
        // Graph stuff
        Graph graph = new DefaultGraph("graph");
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        Node nodeA = graph.getNode("A");
        Node nodeB = graph.getNode("B");
        Node nodeC = graph.getNode("C");
        Node nodeD = graph.getNode("D");
        nodeA.setAttribute("Weight", 2);
        nodeB.setAttribute("Weight", 2);
        nodeC.setAttribute("Weight", 3);
        nodeD.setAttribute("Weight", 2);

        // Edges
        Edge edgeAB = graph.addEdge("AB", nodeA, nodeB, true);
        edgeAB.setAttribute("Weight", 2);
        Edge edgeAC = graph.addEdge("AC", nodeA, nodeC, true);
        edgeAC.setAttribute("Weight", 1);
        Edge edgeBD = graph.addEdge("BD", nodeB, nodeD, true);
        edgeBD.setAttribute("Weight", 2);
        Edge edgeCD = graph.addEdge("CD", nodeC, nodeD, true);
        edgeCD.setAttribute("Weight", 1);

        // Times
        Integer time0 = new Integer(0);
        Integer time2 = new Integer(2);
        Integer time3 = new Integer(3);
        Integer time6 = new Integer(6);

        // Processors
        Integer proc1 = new Integer(1);
        Integer proc2 = new Integer(2);

        // Creating state
        HashMap<Integer, HashMap<Integer, Node>> stateToReturn = new HashMap<>();
        HashMap<Integer, Node> proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> proc2Schedule = new HashMap<>();
        proc2Schedule.put(time0, nodeA);
        proc2Schedule.put(time2, nodeB);
        proc1Schedule.put(time3, nodeC);
        proc1Schedule.put(time6, nodeD);
        stateToReturn.put(proc1, proc1Schedule);
        stateToReturn.put(proc2, proc2Schedule);

        return stateToReturn;
    }
}
