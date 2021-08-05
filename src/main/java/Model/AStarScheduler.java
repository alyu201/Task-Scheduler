package Model;

// TODO: Should this class or visualisation classes implement Thread for concurrency

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for scheduling a number of tasks represented as a DAG (Directed
 * acyclic graph) into a number of processors.
 */
public class AStarScheduler {
    private Graph _taskGraph;
    private int _numProcessors;
    private PriorityQueue<State> _openList;


    //TODO: Update with the actual classes
    public AStarScheduler (Graph taskGraph, int numProcessors) {
        _numProcessors = numProcessors;
        _openList = new PriorityQueue<State>(100, new StateComparator());
        _taskGraph = taskGraph;
    }

    /**
     * Create an optimal schedule using A* algorithm
     * @return The state of the processors with schedules
     */
    public State generateSchedule() {

        //TODO: If there is memory problem, can make just one "StateUtility" class having the numProcessors stored and spits out State objects

        //TODO: The child task for the empty state is probably wrong
        Node rootNode = _taskGraph.getNode("A");
        State emptyState = new State(null, 0, rootNode, 1, 0);
        _openList.add(emptyState);

        while (!_openList.isEmpty()) {
            State state = _openList.poll();

            if (getTaskCountInState(state) == _taskGraph.getNodeCount()) {
                return state;
            }

            List<Node> schedulableTasks = getSchedulableTasks(state);
            addChildStates(state, schedulableTasks);
        }
        return null;
    }

    /**
     * Provides the number of tasks has been allocated in a state.
     * @param state The state to be evaluated
     * @return The number of tasks allocated
     */
    private int getTaskCountInState (State state) {
        HashMap<Integer, HashMap<Integer, Node>> processorSchedules= state.getState();

        int numTaskAllocated = 0;

        for(int i: processorSchedules.keySet()) {
            numTaskAllocated += processorSchedules.get(i).size();
        }
        return numTaskAllocated;
    }

    /**
     * Create a set of child state from a parent state
     * @param parentState The parent state
     * @param tasks The list of tasks that are to be scheduled in the child states.
     */
    public void addChildStates (State parentState, List<Node> tasks) {
        State child;
        HashMap<Integer, HashMap<Integer, Node>> processorSchedules = parentState.getState();

        for (Node task: tasks) {
            List<Node> prerequisiteTasks = task.enteringEdges().map(e -> e.getNode0()).collect(Collectors.toList());

            Node[] prerequisiteNodePos = new Node[_numProcessors];
            int[] startingTimes = new int[_numProcessors];

            for (int i: processorSchedules.keySet()) {
                HashMap<Integer, Node> schedule = processorSchedules.get(i);

                for (int startTime: schedule.keySet()) {
                    if (prerequisiteTasks.contains(schedule.get(startTime))) {
                        int currentStartTime = startingTimes[i];
                        int prereqStartTime = startTime + schedule.get(startTime).getAttribute("Weight", Integer.class);

                        if (prereqStartTime > currentStartTime) {
                            startingTimes[i] = prereqStartTime;
                            prerequisiteNodePos[i] = schedule.get(startTime);
                        }
                    }
                }

            }

            //TODO: Find all the prerequisite tasks and figure out the the max start time, after that, add communication cost if needed when scheduling on a processor
            for(int i = 0; i < _numProcessors; i++) {
                int nextStartTime = parentState.getNextStartTime(i);
                for (int j = 0; j < _numProcessors; j++) {
                    if (i != j && prerequisiteNodePos[j] != null) {
                        int communicationCost = prerequisiteNodePos[j].getEdgeToward(task).getAttribute("Weight", Integer.class);
                        nextStartTime = Math.max(nextStartTime, startingTimes[j] + communicationCost);
                    }
                }

                int maxUnderestimate = Math.max(nextStartTime + task.getAttribute("BottomLevel", Integer.class), parentState.getUnderestimate());

                child = new State(parentState, maxUnderestimate, task, i, nextStartTime);
                _openList.add(child);
            }
        }
//        return _openList;
    }

    /**
     * Generate a list of schedulable tasks (nodes) for a state
     * @param state The state to be evaluated
     * @return A list of schedulable tasks (nodes)
     */
    private List<Node> getSchedulableTasks(State state) {
        HashMap<Node, Integer> allTasks = new HashMap<Node, Integer>();

        //Stores a mapping for all the tasks and and their number of prerequisite tasks
        _taskGraph.nodes().forEach(task -> allTasks.put(task, task.getInDegree()));

        HashMap<Integer, HashMap<Integer, Node>> schedule = state.getState();

        //TODO: Might need to change data structure
        List<Node> scheduledTasks = new ArrayList<Node>();

        //Go through all scheduled tasks and reduce the count for prerequisite tasks of their children.
        for (int i: schedule.keySet()) {
            HashMap<Integer, Node> processorTasks = schedule.get(i);
            for (int j: processorTasks.keySet()) {
                Node task = processorTasks.get(j);

                task.leavingEdges().forEach(edge -> allTasks.put(edge.getNode1(), allTasks.get(edge.getNode1()) - 1));

                scheduledTasks.add(task);
            }
        }

        //Remove tasks that have already been scheduled and tasks that still have prerequisite tasks > 0
        allTasks.entrySet().removeIf(e -> (scheduledTasks.contains(e.getKey()) && e.getValue() > 0));

        return new ArrayList<Node>(allTasks.keySet());
    }

}
