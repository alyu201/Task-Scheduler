package TestingHelper;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import java.util.HashMap;
import java.util.Set;

public class FinishingTimeHelper {

    private HashMap<Integer, HashMap<Integer, Node>> _stateToTest = new HashMap<>();

    public FinishingTimeHelper(HashMap<Integer, HashMap<Integer, Node>> stateToTest) {
        _stateToTest = stateToTest;
    }

    public int returnLatestFinishingTime() {

        System.out.println("******************************************************");
        System.out.println(_stateToTest);

        int latestFinTime = 0;

        // Go through each processor
        Set<Integer> allProcessors = _stateToTest.keySet();
        for (Integer processorNum : allProcessors) {
            System.out.println("Now going through processor " + processorNum);

            // Get all the tasks that this processor has
            HashMap<Integer, Node> allTasks = _stateToTest.get(processorNum);
            System.out.println("\t All tasks this processor has: " + allTasks);

            int latestFinTimeThisProc = 0;

            // Go through all the tasks that this processor has
            Set<Integer> allTasksStartTimes = allTasks.keySet();
            for (Integer startTime : allTasksStartTimes) {
                // Say the task name and what time it starts
                Node node = allTasks.get(startTime);
                int nodeWeight = (int) node.getAttribute("Weight");
                int thisNodeFinTime = startTime + nodeWeight;
                System.out.println("\t\t Task " + node + ":");
                System.out.println("\t\t\t Starts at: " + startTime);
                System.out.println("\t\t\t Weight: " + nodeWeight);
                System.out.println("\t\t\t thisNodeFinTime: " + thisNodeFinTime);

                if (latestFinTimeThisProc < thisNodeFinTime) {
                    latestFinTimeThisProc = thisNodeFinTime;
                }
            }

            System.out.println("\t This processor " + processorNum + " finishes at time: " + latestFinTimeThisProc);

            if (latestFinTime < latestFinTimeThisProc) {
                latestFinTime = latestFinTimeThisProc;
                System.out.println("\t UPDATE latestFinTime!!");
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
