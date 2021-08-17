package Model;

// TODO: Should this class or visualisation classes implement Thread for concurrency

import javafx.concurrent.Task;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for scheduling a number of tasks represented as a DAG (Directed
 * acyclic graph) into a number of processors.
 * author: Sherman Chin
 */
public class AStarScheduler {
    private Graph _taskGraph;
    private int _numProcessors;
    private PriorityQueue<State> _openList;

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
    }

    /**
     * Create an optimal schedule using A* algorithm
     * @return The state of the processors with schedules
     */
    public State generateSchedule() {

        State emptyState = new State(_numProcessors);
        _openList.add(emptyState);

        TaskGraphUtil.removeDummyRootNode(_taskGraph);

        while (!_openList.isEmpty()) {
            State state = _openList.poll();
            if (goalStateReached(state)) {
                return state;
            }

            List<Node> schedulableTasks = getNextTasks(state);
            addChildStates(state, schedulableTasks);
        }
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
     * Create a set of child state from a parent state
     * @param parentState The parent state
     * @param tasks The list of tasks that are to be scheduled in tathe child states.
     */
    private void addChildStates (State parentState, List<Node> tasks) {
        State child;

        Set<Integer> processors = parentState.procKeys();

        for (Node task: tasks) {
            //getting the prerequisite task details
            List<Node> prerequisiteTasks = task.enteringEdges().map(e -> e.getNode0()).collect(Collectors.toList());
            List<String> prerequisiteTasksId = prerequisiteTasks.stream().map(n -> n.getId()).collect(Collectors.toList());

            //TODO: The processor number in State starts indexing from 1
            int[] startingTimes = new int[_numProcessors];

            for (int i: processors) {
                HashMap<Integer, Node> schedule = parentState.getSchedule(i);

                //if current schedule has prerequisite tasks, change the start time to after the finishing time of the prerequisites.
                for (int startTime: schedule.keySet()) {
                    Node taskScheduled = schedule.get(startTime);

                    //change start time to prerequisite task finishing time
                    if (prerequisiteTasksId.contains(taskScheduled.getId())) {

                        //Processor indexing starts from 1
                        int prereqTaskWeight = Double.valueOf(taskScheduled.getAttribute("Weight").toString()).intValue();
                        int prereqTaskCommunicationCost = Double.valueOf(taskScheduled.getEdgeToward(task).getAttribute("Weight").toString()).intValue();

                        int prereqStartTime = startTime + prereqTaskWeight + prereqTaskCommunicationCost;

                        if (prereqStartTime > startingTimes[i - 1]) {
                            startingTimes[i - 1] = prereqStartTime;
                        }
                    }
                }
            }

            //This boolean determines if a task has already been scheduled in a processor, and prevents the task to be
            //scheduled in another processor with same outcome. E.g. Schedule A: {1={0=A}, 2={}} is the same as
            //Schedule B: {1={}, 2={A}}
            boolean taskScheduled = false;

            //adding communication time
            for(int i: processors) {
                if (taskScheduled) {
                    continue;
                }

                int nextStartTime = parentState.getNextStartTime(i);

                if (nextStartTime == 0) {
                    taskScheduled = true;
                }

                for (int j: processors) {
                    if (i != j) {
                        nextStartTime = Math.max(nextStartTime, startingTimes[j - 1]);
                    }
                }

                int maxUnderestimate = Math.max(nextStartTime + task.getAttribute("BottomLevel", Integer.class), parentState.getUnderestimate());

                child = new State(parentState, maxUnderestimate, task, i, nextStartTime);
                _openList.add(child);
            }
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
