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
public class AStarScheduler {
    private Graph _taskGraph;
    private int _numProcessors;
    private PriorityBlockingQueue<State> _openList;
    private ExecutorService _executorService;
    private Set<State> _closedList;

    /**
     * This variable keeps track of the dummy root so that it can only be scheduled once
     */
    private boolean _dummyRootScheduled;


    //TODO: Update with the actual classes
    public AStarScheduler (Graph taskGraph, int numProcessors) {
        _numProcessors = numProcessors;
        _openList = new PriorityBlockingQueue<State>(200, new StateComparator());
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

    /**
     * This method attempts to fix the order of the free tasks in a state in order to reduce the branching
     * factor to the number of processors) where possible. The tasks need to meet the following condition:
     *      - The number of child task and number of parent task of the free tasks must be less than 1 respectively
     *      - If a free task has a parent task, then all parent task of the free tasks must be in the same processor
     *      - If a free task has a child task, then it has the same child task as any other free tasks.
     * @param tasks Free tasks
     * @param currentState The current state to be expanded
     */
    private void fixTaskOrder (List<Node> tasks, State currentState) {

        Map<Node, Integer> outEdgeCostOfTasks = new HashMap<Node, Integer>();

        if (!hasSingleChild(tasks, outEdgeCostOfTasks)) {
            return;
        }

        Map<Node, Integer> taskDataReadyTimes = new HashMap<Node, Integer>();

        if (!hasSingleParentInOneProc(tasks, currentState, taskDataReadyTimes)) {
            return;
        }

        SortedSet<Map.Entry<Node, Integer>> fixedTaskOrder = sortTasks(taskDataReadyTimes, outEdgeCostOfTasks);

        tasks.clear();
        tasks.add(fixedTaskOrder.first().getKey());
    }

    /**
     * This method determines if all free tasks have at most one child task and the child task will be the same for all
     * free tasks
     * @param tasks The tasks to be evaulated
     * @param outEdgeCostOfTasks Mapping of free tasks and their edge costs from it to the child task. Edge cost is 0 if
     *                           free task does not have a child task
     * @return True if all free tasks has a single child task (if exist) that are the same. Else false.
     */
    private boolean hasSingleChild (List<Node> tasks, Map<Node, Integer> outEdgeCostOfTasks) {
        Node singleChildTask = null;

        for (Node task: tasks) {
            List<Node> childTasks = task.leavingEdges().map(e -> e.getNode1()).collect(Collectors.toList());
            Node loneChildTask;

            int numChildTasks = childTasks.size();
            if (numChildTasks > 1) {
                return false;
            } else if (numChildTasks == 0) {
                outEdgeCostOfTasks.put(task, 0);
                continue;
            } else {
                loneChildTask = childTasks.get(0);
            }

            if (singleChildTask == null) {
                singleChildTask = loneChildTask;

            } else if (loneChildTask != singleChildTask) {
                return false;
            }

            outEdgeCostOfTasks.put(task, Double.valueOf(task.getEdgeToward(loneChildTask).getAttribute("Weight").toString()).intValue());
        }
        return true;
    }

    /**
     * This method determines if the free tasks have at most one parent and all parents of free tasks are in the same processor
     * in the given state.
     * @param tasks The tasks to be evaluated
     * @param currentState The current state of scheduled tasks
     * @param taskDataReadyTimes Mapping of the free task and the finishing time of its parent task + the edge cost
     *                           from parent task to the free task
     * @return True if all parent task (if exist) of free tasks are allocated to the same processor.
     */
    private boolean hasSingleParentInOneProc (List<Node> tasks, State currentState, Map<Node, Integer> taskDataReadyTimes) {
        List<HashMap<Integer, Node>> schedules = currentState.getAllSchedules();

        int singleParentTaskProcessor = -1;

        for (Node task: tasks) {
            List<Node> parentTasks = task.enteringEdges().map(e->e.getNode0()).collect(Collectors.toList());

            int currentParentTaskProcessor = -1;

            int numParentTasks = parentTasks.size();

            if (numParentTasks > 1) {
                return false;
            } else if (numParentTasks == 0) {
                taskDataReadyTimes.put(task, 0);
                continue;
            } else {
                Node parentTask = parentTasks.get(0);

                for (int i = 0; i < schedules.size(); i++) {
                    HashMap<Integer, Node> schedule = schedules.get(i);
                    for (Integer startTime: schedule.keySet()) {
                        if (schedule.get(startTime).equals(parentTask)) {
                            currentParentTaskProcessor = i;

                            int parentTaskWeight = Double.valueOf(parentTask.getAttribute("Weight").toString()).intValue();
                            int edgeWeight = Double.valueOf(parentTask.getEdgeToward(task).getAttribute("Weight").toString()).intValue();

                            int currentDataReadyTime = startTime + parentTaskWeight + edgeWeight;
                            taskDataReadyTimes.put(task, currentDataReadyTime);
                        }
                    }
                }
            }

            if (singleParentTaskProcessor == -1) {
                singleParentTaskProcessor = currentParentTaskProcessor;

            } else if (currentParentTaskProcessor == -1){
                continue;

            } else if (currentParentTaskProcessor != singleParentTaskProcessor) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method sorts a given Map of free tasks to its data ready time and a Mapping of free tasks to its outer
     * edge cost (edge cost to child task) into a single sorted sets of Mapping
     * @param dataReadyMap Mappings of free task to its data free time
     * @param edgeCostMap Mappings of free task to its edge cost to child task
     * @param <K> An object that should be a Node class (representing a task)
     * @param <V> An object that is an Integer (representing the data ready time or edge cost to child task)
     * @return A SortedSet that is ordered based on the data ready time of free task. If two free tasks have the same data
     * ready time, then order them based on the outer edge cost (edge cost to child task)
     */
    private static <K, V extends  Comparable<? super V>> SortedSet<Map.Entry<K, V>> sortTasks (Map<K, V> dataReadyMap, Map<Node, Integer> edgeCostMap) {
        SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int result = o1.getValue().compareTo(o2.getValue());
                if (result == 0) {
                    result = edgeCostMap.get(o1.getKey()).compareTo(edgeCostMap.get(o2.getKey()));
                }

                return result != 0 ? result : 1;
            }
        });

        sortedEntries.addAll(dataReadyMap.entrySet());
        return sortedEntries;
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
