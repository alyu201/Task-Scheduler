package Model;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for scheduling a number of tasks represented as a DAG (Directed
 * acyclic graph) into a number of processors using the Branch and Bound technique.
 * author: Sherman Chin and Kelvin Shen
 */
public class BranchAndBoundScheduler extends Scheduler{

    private State _completeState = null;
    private int _upperBound= Integer.MAX_VALUE;
    private Set<State> _closedList;
    private static int CLOSED_LIST_MAX_SIZE = (int) Math.pow(2, 22);

    public BranchAndBoundScheduler(Graph taskGraph, int numProcessors){
        super(taskGraph,numProcessors);
        _closedList = Collections.synchronizedSet(new LinkedHashSet<State>());
    }

    /**
     * Create an optimal schedule using Branch and Bound algorithm
     *
     * @return The state of the processors with schedules
     */
    public State generateSchedule() {
        TaskGraphUtil.removeDummyRootNode(_taskGraph);

        State emptyState = new State(_numProcessors);

        exploreState(emptyState);

        return _completeState;
    }

    /**
     * Expand on a state if it is not in closed list and the underestimate of the state is not higher
     * than the current upper bound.
     * @param currentState
     */
    public void exploreState(State currentState) {

        if (_closedList.contains(currentState) || currentState.getUnderestimate() > _upperBound) {
            return;
        }

        if (goalStateReached(currentState)) {
            _upperBound = currentState.getUnderestimate();
            _completeState = currentState;
        } else {
            List<Node> schedulableTasks = getNextTasks(currentState);
            fixTaskOrder(schedulableTasks, currentState);
            List<State> childStates = addChildStates(currentState, schedulableTasks);

            for (State i: childStates) {
                exploreState(i);
            }

        }

        synchronized (_closedList) {
            if (_closedList.size() > CLOSED_LIST_MAX_SIZE) {
                _closedList.remove(_closedList.iterator().next());
            }
            _closedList.add(currentState);
        }
    }


    public List<State> addChildStates(State state, List<Node> schedulableTasks){
        List<State> childStates = new LinkedList<State>();

        for (Node task: schedulableTasks){
            //The processor number in State starts indexing from 1
            Set<Integer> processors = state.procKeys();

            // startTimes maintains a list of start times for the task to start
            int[] startingTimes = new int[processors.size()];
            this.scheduleAfterPrerequisite(task, state, startingTimes);

            //This boolean determines if a task has already been scheduled in a processor, and prevents the task to be
            //scheduled in another processor with same outcome. E.g. Schedule A: {1={0=A}, 2={}} is the same as
            //Schedule B: {1={}, 2={A}}
            boolean taskScheduled = false;

            //adding communication time
            for(int i: processors) {
                if (taskScheduled) {
                    continue;
                }

                int nextStartTime = state.getNextStartTime(i);

                if (nextStartTime == 0) {
                    taskScheduled = true;
                }

                for (int j: processors) {
                    if (i != j) {
                        nextStartTime = Math.max(nextStartTime, startingTimes[j - 1]);
                    }
                }

                int maxUnderestimate = Math.max(nextStartTime + task.getAttribute("BottomLevel", Integer.class), state.getUnderestimate());

                State child = new State(state, maxUnderestimate, task, i, nextStartTime);

                childStates.add(child);
            }
        }
        return childStates;
    }

    /**
     * This method is a subcomponent of the addIndividualTask method.
     * It is responsible for generating two lists. One shows the
     * @param task
     * @param startingTimes
     */
    private void scheduleAfterPrerequisite(Node task, State state, int[] startingTimes){
        Set<Integer> processors = state.procKeys();

        //getting the prerequisite task details
        HashSet<Node> prerequisiteTasks = task.enteringEdges().map(e -> e.getNode0()).collect(Collectors.toCollection(HashSet:: new));

        for (int i: processors) {
            HashMap<Integer, Node> schedule = state.getSchedule(i);

            //if current schedule has prerequisite tasks, change the start time to after the finishing time of the prerequisites.
            for (int startTime: schedule.keySet()) {
                Node taskScheduled = schedule.get(startTime);

                //change start time to prerequisite task finishing time
                if (prerequisiteTasks.contains(taskScheduled)) {
                    int prereqTaskWeight = Double.valueOf(taskScheduled.getAttribute("Weight").toString()).intValue();
                    int prereqTaskCommunicationCost = Double.valueOf(taskScheduled.getEdgeToward(task).getAttribute("Weight").toString()).intValue();

                    int prereqStartTime = startTime + prereqTaskWeight + prereqTaskCommunicationCost;

                    if (prereqStartTime > startingTimes[i - 1]) {
                        startingTimes[i - 1] = prereqStartTime;
                    }
                }
            }
        }
    }

}
