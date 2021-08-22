package Model;

// TODO: Should this class or visualisation classes implement Thread for concurrency

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for scheduling a number of tasks represented as a DAG (Directed
 * acyclic graph) into a number of processors.
 * author: Sherman Chin and Kelvin Shen
 */
public class AStarScheduler extends Scheduler{
    private PriorityBlockingQueue<State> _openList;
    private ExecutorService _executorService;
    private Set<State> _closedList;


    //TODO: Update with the actual classes
    public AStarScheduler (Graph taskGraph, int numProcessors) {
        super(taskGraph, numProcessors);
        _openList = new PriorityBlockingQueue<State>(200, new StateComparator());
        ConcurrentHashMap<State, Integer> map = new ConcurrentHashMap<State, Integer>();
        _closedList = map.newKeySet();
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
            if (i % freq == 0 && Main.VISUALISATIONFLAG) { Visualiser.update(state); }
            i++;

            if (goalStateReached(state)) {
                _executorService.shutdown();
                // Call Visualiser to update GUI
                if (Main.VISUALISATIONFLAG) {
                    Visualiser.update(state);
                }
                return state;
            }

            List<Node> schedulableTasks = getNextTasks(state);

            fixTaskOrder(schedulableTasks, state);

            addChildStates(state, schedulableTasks);
            _closedList.add(state);
        }
        _executorService.shutdown();
        return null;
    }

    /**
     * Create a set of child state from a parent state.
     * This method uses the ExecutorService to add the child state in parallel.
     * @param parentState The parent state
     * @param tasks The list of tasks that are to be scheduled in tathe child states.
     */
    private void addChildStates (State parentState, List<Node> tasks) throws ExecutionException, InterruptedException {
        List<Callable<Object>> taskList = new ArrayList<>() ;


        //for each task, add it to the openlist on a different thread
        for (Node task: tasks) {
            StateAdditionThread stateAdditionThread = new StateAdditionThread(parentState, task, _openList, _closedList);
            taskList.add(stateAdditionThread);
        }

        List<Future<Object>> futures = _executorService.invokeAll(taskList);

        //Wait for all the child thread to return
        for (Future future:futures){
                future.get();
        }
    }

}
