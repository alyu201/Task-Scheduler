package Model;

// TODO: Should this class or visualisation classes implement Thread for concurrency

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * This class is responsible for scheduling a number of tasks represented as a DAG (Directed
 * acyclic graph) into a number of processors.
 */
public class AStarScheduler {
    private Graph _taskGraph;
    private int _numProcessors;
    private PriorityQueue<State> _openList;


    //TODO: Update with the actual classes
    public AStarScheduler (Graph _taskGraph, int numProcessors) {
        _numProcessors = numProcessors;
        _openList = new PriorityQueue<State>(100, new StateComparator());
    }

    /**
     * Create an optimal schedule using A* algorithm
     * @return The state of the processors with schedules
     */
    public State generateSchedule() {

        //TODO: If there is memory problem, can make just one "StateUtility" class having the numProcessors stored and spits out State objects

        State emptyState = new State(null, 0, );
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
    private void addChildStates (State parentState, List<Node> tasks) {
        State child;

        for (Node task: tasks) {
            for(int i = 1; i <= _numProcessors; i++) {
                //TODO: Might not need a startTime parameter for State constructor because the parentState is passed in already
                int startTime = parentState.getNextStartTime(i);
                //int maxUnderestimate = Math.max(startTime + task.bottomLevel, parentState.getUnderestimate())

                child = new State(parentState, /*maxUnderestimate*/, task, i, startTime);
                _openList.add(child);
            }
        }
    }

    /**
     * Generate a list of schedulable tasks (nodes) for a state
     * @param state The state to be evaluated
     * @return A list of schedulable tasks (nodes)
     */
    private List<Node> getSchedulableTasks(State state) {

        return null;
    }

}
