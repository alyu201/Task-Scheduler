package Model;

// TODO: Should this class or visualisation classes implement Thread for concurrency

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for scheduling a number of tasks represented as a DAG (Directed
 * acyclic graph) into a number of processors.
 * author: schi314
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

        while (!_openList.isEmpty()) {
            State state = _openList.poll();

            if (goalStateReached(state)) {
                return state;
            }

            List<Node> schedulableTasks = getSchedulableTasks(state);
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

        boolean allTasksCompleted = requiredTaskIds.equals(completedTaskIds);
        return allTasksCompleted;
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
            Node[] prerequisiteNodePos = new Node[_numProcessors];
            int[] startingTimes = new int[_numProcessors];
            for (int i: processors) {
                HashMap<Integer, Node> schedule = parentState.getSchedule(i);

                //if current schedule has prerequisite tasks, change the start time to after the finishing time of the prerequisites.
                for (int startTime: schedule.keySet()) {
                    //change start time to prerequisite task finishing time
                    if (prerequisiteTasksId.contains(schedule.get(startTime).getId())) {

                        //Processor indexing starts from 1
                        int currentStartTime = startingTimes[i - 1];
                        int prereqStartTime = startTime + Double.valueOf(schedule.get(startTime).getAttribute("Weight").toString()).intValue();

                        if (prereqStartTime > currentStartTime) {
                            startingTimes[i - 1] = prereqStartTime;
                            prerequisiteNodePos[i - 1] = schedule.get(startTime);
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
                    if (i != j && prerequisiteNodePos[j - 1] != null) {
                        int communicationCost = Double.valueOf(prerequisiteNodePos[j - 1].getEdgeToward(task).getAttribute("Weight").toString()).intValue();
                        nextStartTime = Math.max(nextStartTime, startingTimes[j - 1] + communicationCost);
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
