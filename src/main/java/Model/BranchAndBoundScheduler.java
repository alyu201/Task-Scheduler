package Model;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for scheduling a number of tasks represented as a DAG (Directed
 * acyclic graph) into a number of processors using the Branch and Bound technique.
 * author: Sherman Chin and Kelvin Shen
 */
public class BranchAndBoundScheduler extends Scheduler{

    private ArrayList<LinkedStateList> _stateStack = new ArrayList<LinkedStateList>();
    private int _upperBound= Integer.MAX_VALUE;

    public BranchAndBoundScheduler(Graph taskGraph, int numProcessors){
        super(taskGraph,numProcessors);
    }

    /**
     * Create an optimal schedule using Branch and Bound algorithm
     *
     * @return The state of the processors with schedules
     */
    public State generateSchedule(){
        State completeState = null;
        State emptyState = new State(_numProcessors);
        _stateStack.add(new LinkedStateList(emptyState,new LinkedList<State>()));
        LinkedStateList topLinkedStateList = _stateStack.get(0);
        List<Node> schedulableTasks = getNextTasks(emptyState);
        addChildStates(emptyState, topLinkedStateList, schedulableTasks);

        while (!_stateStack.isEmpty()){
            topLinkedStateList = _stateStack.get(_stateStack.size()-1);
            State state = topLinkedStateList.getLowest();
            System.out.println(state.getAllSchedules());

            // remove from stack if no more state to explore
            if (!topLinkedStateList.stillAvailable()){
                _stateStack.remove(topLinkedStateList);
            }

            // if state underestimate is less than the upper bound
            if (state.getUnderestimate() < _upperBound){

                // check goal state
                if (goalStateReached(state)) {

                    if (state.getUnderestimate()<_upperBound){
                        _upperBound = state.getUnderestimate();
                        completeState = state;
                    }
                }

                // check if is a leaf
                if (!getNextTasks(state).isEmpty()){
                    _stateStack.add(new LinkedStateList(state,new LinkedList<State>()));
                    topLinkedStateList = _stateStack.get(_stateStack.size()-1);
                    schedulableTasks = getNextTasks(state);
                    addChildStates(state, topLinkedStateList, schedulableTasks);
                }
            }
        }
        return completeState;
    }


    public void addChildStates(State state, LinkedStateList linkedStateList, List<Node> schedulableTasks){
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

                linkedStateList.add(child);

            }
        }

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
