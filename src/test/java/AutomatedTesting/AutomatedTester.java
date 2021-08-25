package AutomatedTesting;

import Model.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AutomatedTester {

    private int _numOfThreads = 1;
    private DotFileTestCase _dotFileTestCase = DotFileTestCase.NODES4_PROC2_FROM_SLIDES;

    public AutomatedTester(int numOfThreads, DotFileTestCase dotFileTestCase) {
        _numOfThreads = numOfThreads;
        _dotFileTestCase = dotFileTestCase;
    }

    private static int gettingNumOfThreads() throws IOException {
        // Getting number of threads
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter number of threads (1 <= x <= 8): ");
        String s = br.readLine();
        int numOfThreads = Integer.parseInt(s);

        return numOfThreads;
    }

    private static DotFileTestCase getDotFileTestCase() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n1) NODES4_PROC2_FROM_SLIDES");
        System.out.println("2) NODES7_PROC2_TWOTREE");
        System.out.println("Choose which dot file you want to test with (e.g. 3): ");
        String s = br.readLine();
        int choice = Integer.parseInt(s);
        System.out.println("Chosen: " + choice);

        // By default, return DotFileTestCase.NODES4_PROC2_FROM_SLIDES
        DotFileTestCase dotFileToReturn = DotFileTestCase.NODES4_PROC2_FROM_SLIDES;

        switch (choice)
        {
            case 1:
                dotFileToReturn = DotFileTestCase.NODES4_PROC2_FROM_SLIDES;
                break;
            case 2:
                dotFileToReturn = DotFileTestCase.NODES7_PROC2_TWOTREE;
                break;
            default:
                break;
        }

        return dotFileToReturn;
    }

    public void testingScheduler() {

        // Getting the desired dotfile and its details
        String filePathTestCase = _dotFileTestCase.getFilePath();
        int numProcTestCaseUse = _dotFileTestCase.getNumOfProcUsed();
        int optimalSolTestCase = _dotFileTestCase.getOptimalSol();


        // Generating the graph obj
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


        // Taking care of whether we are using A* or BnB to generate an outputState
        State outputState;
        if (graph.getNodeCount() > 11 || (graph.getNodeCount() == 11 && _numOfThreads > 5)) {
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

        if (generatedFinTime == optimalSolTestCase) {
            System.out.println("IT IS OPTIMAL (" + generatedFinTime + ")");
        } else {
            System.out.println("NOT OPTIMAL: (generatedFinTime: " + generatedFinTime + ") (optimalSolTestCase: " + optimalSolTestCase + ")");
        }


        // Checking whether valid schedule or not
        ValidityChecker validityChecker = new ValidityChecker(stateHashMap);
        boolean validOrNot = validityChecker.checkValidity();

        if (validOrNot) {
            System.out.println("VALID");
        } else {
            System.out.println("NOT VALID");
        }

    }

    public static void main(String[] args) throws IOException {
        int numOfThreads = gettingNumOfThreads();

        DotFileTestCase dotFileTestCase = getDotFileTestCase();

        System.out.println("\nNumber of threads to use: " + numOfThreads);
        System.out.println("DotFileTestCase: " + dotFileTestCase.name());

        AutomatedTester automatedTester = new AutomatedTester(numOfThreads, dotFileTestCase);
        automatedTester.testingScheduler();

    }
}
