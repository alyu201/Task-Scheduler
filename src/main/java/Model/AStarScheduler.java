package Model;

// TODO: Should this class or visualisation classes implement Thread for concurrency

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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

    /**
     * This variable keeps track of the dummy root so that it can only be scheduled once
     */
    private boolean _dummyRootScheduled;


    //TODO: Update with the actual classes
    public AStarScheduler (Graph taskGraph, int numProcessors) {
        _numProcessors = numProcessors;
        _openList = new PriorityQueue<State>(200, new StateComparator());
        _taskGraph = taskGraph;
        _dummyRootScheduled = false;
        _executorService = Executors.newFixedThreadPool(Main.NUMPROCESSORS);

    }

    /**
     * Create an optimal schedule using A* algorithm
     * @return The state of the processors with schedules
     */
    public State generateSchedule() throws ExecutionException, InterruptedException {

        State emptyState = new ModelState(_numProcessors);
        _openList.add(emptyState);

        while (!_openList.isEmpty()) {
            State state = _openList.poll();

            if (goalStateReached(state)) {
                _executorService.shutdown();
                return state;
            }

            List<Node> schedulableTasks = getSchedulableTasks(state);
            addChildStates(state, schedulableTasks);
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
        HashSet<String> requiredTaskIds = _taskGraph.nodes().filter(n -> !(n.getId().equals("dummyRoot"))).map(n -> n.getId()).collect(Collectors.toCollection(HashSet:: new));
        HashSet<String> completedTaskIds = new HashSet<String>();
        Set<Integer> processors = state.procKeys();

        for (int i: processors) {
            HashMap<Integer, Node> schedule = state.getSchedule(i);

            for (Node n: schedule.values()) {
                String taskId = n.getId();
                completedTaskIds.add(taskId);
            }
        }

        return requiredTaskIds.equals(completedTaskIds);
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
            StateAdditionThread stateAdditionThread = new StateAdditionThread(parentState, task, _openList);
            futures.add(_executorService.submit(stateAdditionThread));
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
    private List<Node> getSchedulableTasks(State state) {
        HashMap<Node, Integer> allTasks = new HashMap<Node, Integer>();

        //Stores a mapping for all the tasks and and their number of prerequisite tasks
        _taskGraph.nodes().forEach(task -> allTasks.put(task, task.getInDegree()));

        Node dummyRootNode = _taskGraph.getNode("dummyRoot");

        if (_dummyRootScheduled) {
            allTasks.remove(dummyRootNode);
            dummyRootNode.leavingEdges().forEach(edge -> allTasks.put(edge.getNode1(), allTasks.get(edge.getNode1()) - 1));
        }

        Set<Integer> processors = state.procKeys();

        //TODO: Might need to change data structure
        List<Node> scheduledTasks = new ArrayList<Node>();

        //Go through all scheduled tasks and reduce the count for prerequisite tasks of their children.
        for (int i: processors) {
            HashMap<Integer, Node> processorTasks = state.getSchedule(i);
            for (int j: processorTasks.keySet()) {
                Node task = processorTasks.get(j);

                task.leavingEdges().forEach(edge -> allTasks.put(edge.getNode1(), allTasks.get(edge.getNode1()) - 1));
                scheduledTasks.add(task);
            }
        }

        //Remove tasks that have already been scheduled and tasks that still have prerequisite tasks > 0
        allTasks.entrySet().removeIf(e -> (scheduledTasks.contains(e.getKey()) || e.getValue() > 0));

        List<Node> schedulableTasks = new ArrayList<Node>(allTasks.keySet());

        if (schedulableTasks.contains(_taskGraph.getNode("dummyRoot"))) {
            _dummyRootScheduled = true;
        }

        return schedulableTasks;
    }

}
