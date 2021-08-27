package Model;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * This class is responsible for scheduling a number of tasks represented as a DAG (Directed
 * acyclic graph) into a number of processors using the Branch and Bound technique.
 * author: Sherman Chin and Kelvin Shen
 */
public class BranchAndBoundScheduler extends Scheduler {

    private State _completeState = null;
    private int _upperBound = Integer.MAX_VALUE;
    private Set<State> _closedList;
    public static int CLOSED_LIST_MAX_SIZE = (int) Math.pow(2, 22);

    public BranchAndBoundScheduler(Graph taskGraph, int numProcessors) {
        super(taskGraph, numProcessors);
        _closedList = Collections.synchronizedSet(new LinkedHashSet<State>());
    }

    /**
     * Create an optimal schedule using Branch and Bound algorithm
     *
     * @return The state of the processors with schedules
     */
    @Override
    public State generateSchedule() {
        TaskGraphUtil.removeDummyRootNode(_taskGraph);

        State emptyState = new State(_numProcessors);
        if (Main.NUMPROCESSORS == 1) {
            exploreState(emptyState);
        } else {
            //Continue expanding states until there are enough number of initial states to use BnB in each thread
            PriorityQueue<State> initialStatesForBnB = new PriorityQueue<State>(100, new StateComparator());
            initialStatesForBnB.add(emptyState);
            while (initialStatesForBnB.size() < Main.NUMPROCESSORS) {
                expandState(initialStatesForBnB);
            }
            ForkJoinPool pool = new ForkJoinPool(Main.NUMPROCESSORS);

            for (State state: initialStatesForBnB) {
                BranchAndBoundParallel task = new BranchAndBoundParallel(this, state);
                pool.invoke(task);
            }
        }
        return _completeState;
    }

    /**
     * Expand a state and add to closed list
     * @param states List of states available to be expanded
     */
    private void expandState (PriorityQueue<State> states) {
        State statesToExpand = states.poll();

        if (goalStateReached(statesToExpand)) {
            return;
        }
        List<Node> schedulableTasks = getNextTasks(statesToExpand);
        fixTaskOrder(schedulableTasks, statesToExpand);
        PriorityQueue<State> childStates = addChildStates(statesToExpand, schedulableTasks);
        states.addAll(childStates);

        _closedList.add(statesToExpand);
    }

    /**
     * Used as a recursive function to explore the search space
     * @param currentState The current state to expand and explore
     */
    public void exploreState(State currentState) {
        //todo update visualisation
        if (Main.PARALLELISATIONFLAG) {
            Visualiser.incrThreadCount();
        }
        if (_closedList.contains(currentState) || currentState.getUnderestimate() > _upperBound) {
            return;
        }

        if (goalStateReached(currentState)) {
            if (currentState.getUnderestimate() < _upperBound) {
                // Update GUI when another best upperbound is found
                if (Main.VISUALISATIONFLAG){
                    Visualiser.update(currentState);
                }
                _upperBound = currentState.getUnderestimate();
                _completeState = currentState;
            }
        } else {
            if (currentState.getUnderestimate() <= _upperBound) {
                List<Node> schedulableTasks = getNextTasks(currentState);
                PriorityQueue<State> childStates = addChildStates(currentState, schedulableTasks);
                for (State i : childStates) {
                    exploreState(i);
                }
            }
        }

        synchronized (_closedList) {
            if (_closedList.size() > BranchAndBoundScheduler.CLOSED_LIST_MAX_SIZE) {
                _closedList.remove(_closedList.iterator().next());
            }
            _closedList.add(currentState);
        }
    }

    /**
     * This method generates the child states of a given state and tasks to schedule.
     *
     * @param state The state to expand and generate child states from
     * @param schedulableTasks The list of task to be added
     * @return A list of child states
     */
    public PriorityQueue<State> addChildStates(State state, List<Node> schedulableTasks) {
        PriorityQueue<State> childStates = new PriorityQueue<>(100, new StateComparator());

        //The processor number in State starts indexing from 1
        for (Node task : schedulableTasks) {

            Set<Integer> processors = state.procKeys();

            // startTimes maintains a list of start times for the task to start
            int[] startingTimes = new int[processors.size()];
            this.scheduleAfterPrerequisite(task, state, startingTimes);

            //This boolean determines if a task has already been scheduled in a processor, and prevents the task to be
            //scheduled in another processor with same outcome. E.g. Schedule A: {1={0=A}, 2={}} is the same as
            //Schedule B: {1={}, 2={A}}
            boolean taskScheduled = false;

            //adding communication time
            for (int i : processors) {
                if (taskScheduled) {
                    continue;
                }

                int nextStartTime = state.getNextStartTime(i);

                if (nextStartTime == 0) {
                    taskScheduled = true;
                }

                for (int j : processors) {
                    if (i != j) {
                        nextStartTime = Math.max(nextStartTime, startingTimes[j - 1]);
                    }
                }

                // get underestimation from load balance
                int idleTime = computeIdleTime(state, nextStartTime, i);
                int loadBalance = computeLoadBalance(idleTime);
                int maxUnderestimate = Math.max(nextStartTime + task.getAttribute("BottomLevel", Integer.class), state.getUnderestimate());
                maxUnderestimate = Math.max(maxUnderestimate, loadBalance);

                // create new child state then add it to queue
                State child = new State(state, maxUnderestimate, task, i, nextStartTime);
                child.setTotalIdleTime(idleTime);
                childStates.add(child);
            }
        }
        return childStates;
    }

    /**
     * This method computes the load balance of a state.
     */
    private int computeLoadBalance(int idleTime) {
        return (_totalNodeWeight + idleTime) / _numProcessors;
    }

    /**
     * This mehtod computes the totalIdleTime
     *
     * @param parentState
     * @param nextStartTime
     * @param processNum
     * @return idleTime
     */
    private int computeIdleTime(State parentState, int nextStartTime, int processNum) {
        int parentIdleTime = parentState.getTotalIdleTime();

        // calculate the idle time to the new task
        HashMap<Integer, Node> schedule = parentState.getAllSchedules().get(processNum - 1);
        List<Integer> startTimes = new ArrayList<>(schedule.keySet());
        int newIdleTime = 0;
        if (!startTimes.isEmpty()) {
            Integer lastStartTime = startTimes.get(startTimes.size() - 1);
            Integer nodeWeight = Double.valueOf(schedule.get(lastStartTime).getAttribute("Weight").toString()).intValue();
            newIdleTime = nextStartTime - (lastStartTime + nodeWeight);
        } else {
            newIdleTime = nextStartTime;
        }

        return parentIdleTime + newIdleTime;

    }


    /**
     * This method is a subcomponent of the addIndividualTask method.
     *
     * @param task
     * @param startingTimes
     */
    private void scheduleAfterPrerequisite(Node task, State state, int[] startingTimes) {
        Set<Integer> processors = state.procKeys();

        //getting the prerequisite task details
        HashSet<Node> prerequisiteTasks = task.enteringEdges().map(e -> e.getNode0()).collect(Collectors.toCollection(HashSet::new));

        for (int i : processors) {
            HashMap<Integer, Node> schedule = state.getSchedule(i);

            //if current schedule has prerequisite tasks, change the start time to after the finishing time of the prerequisites.
            for (int startTime : schedule.keySet()) {
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
