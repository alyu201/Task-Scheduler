package Model;


import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * This class is responsible for scheduling a number of tasks represented as a DAG (Directed
 * acyclic graph) into a number of processors.
 * author: Sherman Chin and Kelvin Shen
 */
public class AStarScheduler extends Scheduler {
    private PriorityBlockingQueue<State> _openList;
    private ExecutorService _executorService;
    private Set<State> _closedList;
    private Logger _logger = Logger.getLogger(AStarScheduler.class.getName());


    public AStarScheduler(Graph taskGraph, int numProcessors) {
        super(taskGraph, numProcessors);
        _openList = new PriorityBlockingQueue<State>(200, new StateComparator());
        ConcurrentHashMap<State, Integer> map = new ConcurrentHashMap<State, Integer>();
        _closedList = map.newKeySet();
        _executorService = Executors.newFixedThreadPool(Main.NUMPROCESSORS);
    }

    /**
     * Create an optimal schedule using A* algorithm
     *
     * @return The state of the processors with schedules
     */
    @Override
    public State generateSchedule() {

        State emptyState = new State(_numProcessors);
        _openList.add(emptyState);

        TaskGraphUtil.removeDummyRootNode(_taskGraph);

        while (!_openList.isEmpty()) {
            State state = _openList.poll();

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

            try {
                addChildStates(state, schedulableTasks);
            } catch (ExecutionException | InterruptedException e) {
                _logger.info("Threading error in adding child states");
            }
            _closedList.add(state);

            // Update GUI whenever a state is popped off openList
            if (Main.VISUALISATIONFLAG) {
                Visualiser.update(state);
            }

            // Reset the count for the number of processors/threads
            Visualiser.resetThreadCount();
        }
        _executorService.shutdown();
        return null;
    }

    /**
     * Create a set of child state from a parent state.
     * This method uses the ExecutorService to add the child state in parallel.
     *
     * @param parentState The parent state
     * @param tasks       The list of tasks that are to be scheduled in tathe child states.
     */
    private void addChildStates(State parentState, List<Node> tasks) throws ExecutionException, InterruptedException {
        List<Callable<Object>> taskList = new ArrayList<>();


        //for each task, add it to the openlist on a different thread
        for (Node task : tasks) {
            StateAdditionThread stateAdditionThread = new StateAdditionThread(parentState, task, _openList, _closedList);
            taskList.add(stateAdditionThread);
            // only count number of processors/threads if parallelisation is required
            if (Main.PARALLELISATIONFLAG) {
                Visualiser.incrThreadCount();
            }
        }

        List<Future<Object>> futures = _executorService.invokeAll(taskList);

        //Wait for all the child thread to return
        for (Future future : futures) {
            future.get();
        }
    }

}
