package Model;

// TODO: Should this class or visualisation classes implement Thread for concurrency

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.HashMap;
import java.util.PriorityQueue;

/***
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

    public State generateSchedule() {

        //TODO: If there is memory problem, can make just one "StateUtility" class having the numProcessors stored and spits out State objects

        State emptyState = new State(null, 0, );
        _openList.add(emptyState);

        while (!_openList.isEmpty()) {
            State state = _openList.poll();

            if (getTaskCountInState(state) == _taskGraph.getNodeCount()) {
                return state;
            }

            addChildStates(state, getSchedulableTasks());
        }
        return null;
    }

    private int getTaskCountInState (State state) {
        HashMap<Integer, HashMap<Integer, Node>> processorSchedules= state.getState();

        int numTaskAllocated = 0;

        for(int i: processorSchedules.keySet()) {
            for (int j: processorSchedules.get(i).keySet()) {
                numTaskAllocated++;
            }
        }
        return numTaskAllocated;
    }

    private void addChildStates (State parentState, List<Node> tasks) {
        State child;

        for (Node task: tasks) {
            for(int i = 1; i <= _numProcessors; i++) {
                //TODO: Might not need a startTime parameter for State constructor because the parentState is passed in already
                int startTime = parentState.getNextStartTime(i);

                child = new State(parentState, , task, i, startTime);
                _openList.add(child);
            }
        }
    }

    private List<Node> getSchedulableTasks() {


        return null;
    }

}
