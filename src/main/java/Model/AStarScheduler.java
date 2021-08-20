package Model;

// TODO: Should this class or visualisation classes implement Thread for concurrency

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.concurrent.*;

/**
 * This class is responsible for scheduling a number of tasks represented as a DAG (Directed
 * acyclic graph) into a number of processors.
 * author: Sherman Chin and Kelvin Shen
 */
public class AStarScheduler {
    private Graph _taskGraph;
    private int _numProcessors;
    private PriorityQueue<State> _openList;
    private ExecutorService _executorService;
    private Set<State> _closedList;

    /**
     * This variable keeps track of the dummy root so that it can only be scheduled once
     */
    private boolean _dummyRootScheduled;


    //TODO: Update with the actual classes
    public AStarScheduler (Graph taskGraph, int numProcessors) {
        _numProcessors = numProcessors;
        _openList = new PriorityQueue<State>(200, new StateComparator());
        _closedList = new HashSet<State>();
        _taskGraph = taskGraph;
        _dummyRootScheduled = false;
        _executorService = Executors.newFixedThreadPool(Main.NUMPROCESSORS);

    }

    /**
     * Create an optimal schedule using A* algorithm
     * @return The state of the processors with schedules
     */
    public State generateSchedule() throws ExecutionException, InterruptedException {

        State emptyState = new State(_numProcessors);
        _openList.add(emptyState);
        int i = 1;
        int freq = (int) (_taskGraph.nodes().count()) * _numProcessors;

        TaskGraphUtil.removeDummyRootNode(_taskGraph);

        while (!_openList.isEmpty()) {
            State state = _openList.poll();

            // Update GUI at a frequency of 1/(numOfTasks*numProc) whenever a state is popped off openList
            if (i % freq == 0) { Visualiser.update(state); }
            i++;

            if (goalStateReached(state)) {
                _executorService.shutdown();
                // Call Visualiser to update GUI
                Visualiser.update(state);
                return state;
            }

            List<Node> schedulableTasks = getNextTasks(state);
            addChildStates(state, schedulableTasks);
            _closedList.add(state);

            // Update GUI at a frequency of 1/(numOfTasks*numProc) whenever a state is popped off openList
            if (i % freq == 0) {
                Visualiser.update(state);
            }
            i++;

            Visualiser.resetThreadCount();
        }
        _executorService.shutdown();
        return null;
    }

    /**
     * This method determines if a state has reached the goal
     * @param state The state to be checked
     * @return True if is goal state, false otherwise
     */
    private boolean goalStateReached(State state) {
        List<HashMap<Integer, Node>> schedules = state.getAllSchedules();

        List<Node> scheduledTasks = new ArrayList<Node>();

        for (HashMap<Integer, Node> i: schedules) {
            scheduledTasks.addAll(i.values());
        }

        return TaskGraphUtil.allTaskScheduled(_taskGraph, scheduledTasks);
    }

    /**
     * Create a set of child state from a parent state.
     * This method uses the ExecutorService to add the child state in parallel.
     * @param parentState The parent state
     * @param tasks The list of tasks that are to be scheduled in tathe child states.
     */
    private void addChildStates (State parentState, List<Node> tasks) throws ExecutionException, InterruptedException {
        Set<Future> futures = new HashSet<>();

        //for each task, add it to the openlist on a different thread
        for (Node task: tasks) {
            StateAdditionThread stateAdditionThread = new StateAdditionThread(parentState, task, _openList, _closedList);
            futures.add(_executorService.submit(stateAdditionThread));
            Visualiser.incrThreadCount();
        }

        //Wait for all the child thread to return
        for (Future future:futures){
            future.get();
        }
    }

    /**
     * Generate a list of schedulable tasks (nodes) for a state
     * @param state The state to be evaluated
     * @return A list of schedulable tasks (nodes)
     */
    private List<Node> getNextTasks(State state) {
        List<HashMap<Integer, Node>> schedules = state.getAllSchedules();

        List<Node> scheduledTasks = new ArrayList<Node>();

        for (HashMap<Integer, Node> i: schedules) {
            scheduledTasks.addAll(i.values());
        }

        List<Node> schedulableTasks = TaskGraphUtil.getNextSchedulableTasks(_taskGraph, scheduledTasks);

        return schedulableTasks;
    }

}
