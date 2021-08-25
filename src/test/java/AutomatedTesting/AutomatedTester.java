package AutomatedTesting;

import Model.*;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * This class is for Automated Testing. It's main method will ask the user to:
 *         * Enter the number of threads that they want to use (1 <= numOfThreads <= 8)
 *         * Which dot file they want to use from the list
 * Then the class will test the algorithm schedulers by:
 *         * Generating a schedule with AStarScheduler or BranchAndBoundScheduler (to decide which algorithm to use is
 *                 based on what dot file was chosen)
 *         * Checking whether: Generated finishing time = Graph's optimal finishing time
 *         * Checking whether the generated schedule is valid or not
 * @author Megan Lim
 */
public class AutomatedTester {

    private int _numOfThreads = 1;
    private DotFileTestCase _dotFileTestCase;

    public AutomatedTester(int numOfThreads, DotFileTestCase dotFileTestCase) {
        _numOfThreads = numOfThreads;
        _dotFileTestCase = dotFileTestCase;
    }

    /**
     * This method will ask the user to type in how many threads they want to use.
     * @return (int) the number of threads that the user requested.
     * @throws IOException
     */
    private static int gettingNumOfThreads() throws IOException {
        // Getting number of threads
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter number of threads (1 <= x <= 8): ");
        String s = br.readLine();
        int numOfThreads = Integer.parseInt(s);

        return numOfThreads;
    }

    /**
     * This method will ask the user to chose among some options to select which dot file they want to use to test.
     * @return (DotFileTestCase) the selected input dot file enum that the user wanted
     * @throws IOException
     */
    private static DotFileTestCase getDotFileTestCase() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n1) NODES4_PROC2_FROM_SLIDES");
        System.out.println("2) NODES7_PROC2_TWOTREE \t\t\t 3) NODES7_PROC4_TWOTREE");
        System.out.println("4) NODES8_PROC2_RANDOM \t\t\t 5) NODES8_PROC4_RANDOM");
        System.out.println("6) NODES9_PROC2_SERIESPARALLEL \t\t\t 7) NODES9_PROC4_SERIESPARALLEL");
        System.out.println("8) NODES10_PROC2_RANDOM \t\t\t 9) NODES10_PROC4_RANDOM");
        System.out.println("10) NODES11_PROC2_OUTTREE \t\t\t 11) NODES11_PROC4_OUTTREE");
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
            case 3:
                dotFileToReturn = DotFileTestCase.NODES7_PROC4_TWOTREE;
                break;
            case 4:
                dotFileToReturn = DotFileTestCase.NODES8_PROC2_RANDOM;
                break;
            case 5:
                dotFileToReturn = DotFileTestCase.NODES8_PROC4_RANDOM;
                break;
            case 6:
                dotFileToReturn = DotFileTestCase.NODES9_PROC2_SERIESPARALLEL;
                break;
            case 7:
                dotFileToReturn = DotFileTestCase.NODES9_PROC4_SERIESPARALLEL;
                break;
            case 8:
                dotFileToReturn = DotFileTestCase.NODES10_PROC2_RANDOM;
                break;
            case 9:
                dotFileToReturn = DotFileTestCase.NODES10_PROC4_RANDOM;
                break;
            case 10:
                dotFileToReturn = DotFileTestCase.NODES11_PROC2_OUTTREE;
                break;
            case 11:
                dotFileToReturn = DotFileTestCase.NODES11_PROC4_OUTTREE;
                break;
            default:
                break;
        }

        return dotFileToReturn;
    }

    /**
     * This method will test the algorithm schedulers by:
     *      * Generating a schedule with AStarScheduler or BranchAndBoundScheduler (to decide which algorithm to use is
     *                based on what dot file was chosen)
     *      * Checking whether: Generated finishing time = Graph's optimal finishing time
     *      * Checking whether the generated schedule is valid or not
     */
    public void testingScheduler() {

        // Getting the desired input dot file's details
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
        System.out.println("This graph has " + graph.getNodeCount() + " nodes (including dummy node because algorithm hasn't taken it away yet)");
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
        // Getting the number of threads that the user wants to use
        int numOfThreads = gettingNumOfThreads();

        // Getting the desired inptu dot file enum that the user wants to use
        DotFileTestCase dotFileTestCase = getDotFileTestCase();

        System.out.println("\nNumber of threads to use: " + numOfThreads);
        System.out.println("DotFileTestCase: " + dotFileTestCase.name());

        // Test out the chosen input dot file on the scheduler with the given number of threads; and print results
        AutomatedTester automatedTester = new AutomatedTester(numOfThreads, dotFileTestCase);
        automatedTester.testingScheduler();

    }
}
