package AutomatedTesting;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import java.util.HashMap;
import java.util.Set;

/**
 * This class serves as a helper to calculate the finishing time of a schedule.
 * @author Megan Lim
 */
public class FinishingTimeHelper {

    private HashMap<Integer, HashMap<Integer, Node>> _stateToTest = new HashMap<>();

    public FinishingTimeHelper(HashMap<Integer, HashMap<Integer, Node>> stateToTest) {
        _stateToTest = stateToTest;
    }

    /**
     * This helper method will calculate the latest finishing time of the given state.
     * @return (int) the latest finishing time
     */
    public int returnLatestFinishingTime() {

        System.out.println("State to calculate latest finishing time for:" + _stateToTest);

        int latestFinTime = 0;

        // Go through each processor
        Set<Integer> allProcessors = _stateToTest.keySet();
        for (Integer processorNum : allProcessors) {

            // Get all the tasks that this processor has
            HashMap<Integer, Node> allTasks = _stateToTest.get(processorNum);

            int latestFinTimeThisProc = 0;

            // Go through all the tasks that this processor has
            Set<Integer> allTasksStartTimes = allTasks.keySet();
            for (Integer startTime : allTasksStartTimes) {
                // Calculate what time does this node finish
                Node node = allTasks.get(startTime);
                int nodeWeight = (int) node.getAttribute("Weight");
                int thisNodeFinTime = startTime + nodeWeight;

                // Update (if needed) the latestFinTimeThisProc
                if (latestFinTimeThisProc < thisNodeFinTime) {
                    latestFinTimeThisProc = thisNodeFinTime;
                }
            }

            // Update (if needed) the latestFinTime of the overall state
            if (latestFinTime < latestFinTimeThisProc) {
                latestFinTime = latestFinTimeThisProc;
            }
        }

        return latestFinTime;
    }

    public static void main(String[] args) {

        FinishingTimeHelper finishingTimeHelper1 = new FinishingTimeHelper(creatingGoodState());
        int latestFinTime1 = finishingTimeHelper1.returnLatestFinishingTime();
        System.out.println("FINAL latestFinTime1: " + latestFinTime1);

        FinishingTimeHelper finishingTimeHelper2 = new FinishingTimeHelper(creatingGoodState2());
        int latestFinTime2 = finishingTimeHelper2.returnLatestFinishingTime();
        System.out.println("FINAL latestFinTime2: " + latestFinTime2);

        FinishingTimeHelper finishingTimeHelper3 = new FinishingTimeHelper(creatingBadState());
        int latestFinTime3 = finishingTimeHelper3.returnLatestFinishingTime();
        System.out.println("FINAL latestFinTime3: " + latestFinTime3);

        FinishingTimeHelper finishingTimeHelper4 = new FinishingTimeHelper(creatingBadState2());
        int latestFinTime4 = finishingTimeHelper4.returnLatestFinishingTime();
        System.out.println("FINAL latestFinTime4: " + latestFinTime4);
    }

    /**
     * This creates another good state to test with.
     * There are tasks in both processors #1 and #2.
     * @return good state
     */
    private static HashMap<Integer, HashMap<Integer, Node>> creatingGoodState() {
        /***** Setting up the a state to test this helper for now *****/
        // Graph stuff
        Graph _graph = new DefaultGraph("graph");
        _graph.addNode("A");
        _graph.addNode("B");
        _graph.addNode("C");
        _graph.addNode("D");
        Node nodeA = _graph.getNode("A");
        Node nodeB = _graph.getNode("B");
        Node nodeC = _graph.getNode("C");
        Node nodeD = _graph.getNode("D");
        nodeA.setAttribute("Weight", 2);
        nodeB.setAttribute("Weight", 2);
        nodeC.setAttribute("Weight", 3);
        nodeD.setAttribute("Weight", 2);

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

    /**
     * This creates another good state to test with.
     *  This time, all the tasks will be on processor #2 only.
     * @return good state
     */
    private static HashMap<Integer, HashMap<Integer, Node>> creatingGoodState2() {
        /***** Setting up the a state to test this helper for now *****/
        // Graph stuff
        Graph _graph = new DefaultGraph("graph");
        _graph.addNode("A");
        _graph.addNode("B");
        _graph.addNode("C");
        _graph.addNode("D");
        Node nodeA = _graph.getNode("A");
        Node nodeB = _graph.getNode("B");
        Node nodeC = _graph.getNode("C");
        Node nodeD = _graph.getNode("D");
        nodeA.setAttribute("Weight", 2);
        nodeB.setAttribute("Weight", 2);
        nodeC.setAttribute("Weight", 1);
        nodeD.setAttribute("Weight", 2);

        // Times
        Integer time0 = new Integer(0);
        Integer time2 = new Integer(2);
        Integer time5 = new Integer(5);
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
        proc2Schedule.put(time5, nodeC);
        proc2Schedule.put(time6, nodeD);
        stateToReturn.put(proc1, proc1Schedule);
        stateToReturn.put(proc2, proc2Schedule);

        return stateToReturn;
    }

    /**
     * This creates a bad state to test with. It is bad because:
     *      * Task C (Starts at: 3) (Weight: 9)
     *      * Task D (Starts at: 6) (Weight: 2)
     *  In other words, there is an overlap in the tasks.
     * @return bad state
     */
    private static HashMap<Integer, HashMap<Integer, Node>> creatingBadState() {
        /***** Setting up the a state to test this helper for now *****/
        // Graph stuff
        Graph _graph = new DefaultGraph("graph");
        _graph.addNode("A");
        _graph.addNode("B");
        _graph.addNode("C");
        _graph.addNode("D");
        Node nodeA = _graph.getNode("A");
        Node nodeB = _graph.getNode("B");
        Node nodeC = _graph.getNode("C");
        Node nodeD = _graph.getNode("D");
        nodeA.setAttribute("Weight", 2);
        nodeB.setAttribute("Weight", 2);
        nodeC.setAttribute("Weight", 9);
        nodeD.setAttribute("Weight", 2);

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

    /**
     * This creates a bad state to test with. It is bad because Processor #1 has:
     *      * Task C (Starts at: 3) (Weight: 8)
     *      * Task D (Starts at: 6) (Weight: 2)
     *  In other words, there is an overlap in the tasks.
     *  But Processor #2's last task is:
     *      * Task B (Starts at: 2) (Weight: 20)
     *    which will create a bigger finishing time than the overlapped tasks.
     * @return bad state
     */
    private static HashMap<Integer, HashMap<Integer, Node>> creatingBadState2() {
        /***** Setting up the a state to test this helper for now *****/
        // Graph stuff
        Graph _graph = new DefaultGraph("graph");
        _graph.addNode("A");
        _graph.addNode("B");
        _graph.addNode("C");
        _graph.addNode("D");
        Node nodeA = _graph.getNode("A");
        Node nodeB = _graph.getNode("B");
        Node nodeC = _graph.getNode("C");
        Node nodeD = _graph.getNode("D");
        nodeA.setAttribute("Weight", 2);
        nodeB.setAttribute("Weight", 20);
        nodeC.setAttribute("Weight", 8);
        nodeD.setAttribute("Weight", 2);

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
