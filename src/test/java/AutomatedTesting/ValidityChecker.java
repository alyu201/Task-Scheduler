package AutomatedTesting;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;

import java.util.*;

/**
 * This class is a validity checker. It checks whether a state (HashMap<Integer, HashMap<Integer, Node>>)
 * is representing a valid schedule or not.
 * @author Megan Lim
 */
class ValidityChecker {

    private HashMap<Integer, HashMap<Integer, Node>> _stateToTest;

    public ValidityChecker(HashMap<Integer, HashMap<Integer, Node>> stateToTest) {
        _stateToTest = stateToTest;
    }

    /**
     * This is a helper method to find the requested node in the state, and return its details.
     * Its details are:
     *      * The processor number it is scheduled in
     *      * Start time
     * @param nodeToFind the node that we want to find the details of
     * @return (List<Integer>) an arraylist containing the node's details.
     *          0th index contains proc num. 1st index contains start time.
     */
    private List<Integer> getNodeDetails(Node nodeToFind) {

        List<Integer> nodeDetails = new ArrayList<Integer>();

        // Go through each processor, and try to find this node
        Set<Integer> allProcessors = _stateToTest.keySet();
        for (Integer processorNum : allProcessors) {

            HashMap<Integer, Node> thisProcAllTasks = _stateToTest.get(processorNum);

            // Going through all the tasks that are scheduled in this processor.
            Set<Integer> allStartTimes = thisProcAllTasks.keySet();
            for (Integer startTime : allStartTimes) {
                //Get the node
                Node node = thisProcAllTasks.get(startTime);

                // If we found the node that we were looking for,
                // return the processor number that it is scheduled in.
                if (nodeToFind == node) {
                    nodeDetails.add(processorNum);
                    nodeDetails.add(startTime);

                    return nodeDetails;
                }
            }
        }

        // If unable to find node, return processorNum = 0, startTime = 0
        Integer proc0 = new Integer(0);
        nodeDetails.add(proc0);
        nodeDetails.add(proc0);

        return nodeDetails;
    }

    /**
     * This method checks for validity in terms of when a task was scheduled in the state.
     * @return (boolean) whether the state represents a valid schedule or not
     */
    public boolean checkValidity() {

        // This is to keep track of whether the whole schedule is valid or not
        boolean validSchedule = true;

        // Going through each processor
        Set<Integer> allProcessors = _stateToTest.keySet();
        for (Integer processorNum : allProcessors) {

            // Getting all the tasks in this processor
            HashMap<Integer, Node> thisProcAllTasks = _stateToTest.get(processorNum);

            // Going through each node scheduled in this processor
            Set<Integer> allStartTimes = thisProcAllTasks.keySet();
            for(Integer startTime : allStartTimes) {
                //Get the node
                Node node = thisProcAllTasks.get(startTime);

                int numOfParents = node.getInDegree();

                if (numOfParents > 0) {

                    // This keeps track of the earliest time that this node/task is able to start
                    int earliestStartTime = 0;

                    // Go through all its parent(s)
                    for (int i = 0; i < numOfParents; i++) {
                        Edge parentEdge = node.getEnteringEdge(i);
                        Node parent = parentEdge.getSourceNode();

                        // Finding out things about the parent node
                        List<Integer> parentDetails = getNodeDetails(parent);
                        int parentProcessorNum = parentDetails.get(0).intValue();
                        int parentStartTime = parentDetails.get(1).intValue();
                        int parentWeight = (int) parent.getAttribute("Weight");
                        int communicationTime = (int) parentEdge.getAttribute("Weight");

                        // If the parent is on a different processor
                        if (processorNum.intValue() != parentProcessorNum) {
                            int childStartTime4ThisParent = parentStartTime + parentWeight + communicationTime;

                            // If we have found a later time that the child should start
                            if (earliestStartTime < childStartTime4ThisParent) {
                                earliestStartTime = childStartTime4ThisParent;
                            }

                        } else {

                            int childStartTime4ThisParent = parentStartTime + parentWeight;

                            // If we have found a later time that the child should start
                            if (earliestStartTime < childStartTime4ThisParent) {
                                earliestStartTime = childStartTime4ThisParent;
                            }
                        }
                    }

                    // If this task was scheduled way too early
                    if (!(earliestStartTime <= startTime.intValue())) {
                        validSchedule = false;
                        System.out.println("NOT VALID! Problem is: Node " + node + " should have started on " + earliestStartTime + " or later, But it actually started at " + startTime.intValue());
                    }
                }

            }
        }

        return validSchedule;
    }

    public static void main(String[] args) {

        ValidityChecker validityChecker = new ValidityChecker(creatingGoodState());
        boolean valid = validityChecker.checkValidity();
        System.out.println("FINAL state 1 validSchedule: " + valid);

        ValidityChecker validityChecker2 = new ValidityChecker(creatingGoodState2());
        boolean valid2 = validityChecker2.checkValidity();
        System.out.println("FINAL state 2 validSchedule: " + valid2);

        ValidityChecker validityChecker3 = new ValidityChecker(creatingBadState());
        boolean valid3 = validityChecker3.checkValidity();
        System.out.println("FINAL state 3 validSchedule: " + valid3);

        ValidityChecker validityChecker4 = new ValidityChecker(creatingBadState2());
        boolean valid4 = validityChecker4.checkValidity();
        System.out.println("FINAL state 4 validSchedule: " + valid4);

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

    /**
     * This creates another good state to test with.
     *  This time, all the tasks will be on processor #2 only.
     * @return good state
     */
    private static HashMap<Integer, HashMap<Integer, Node>> creatingGoodState2() {
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
        nodeC.setAttribute("Weight", 1);
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
        nodeC.setAttribute("Weight", 9);
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
        nodeB.setAttribute("Weight", 20);
        nodeC.setAttribute("Weight", 8);
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
