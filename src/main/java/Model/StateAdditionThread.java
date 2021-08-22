package Model;

import org.graphstream.graph.Node;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

/**
 * This class is responsible for creating a new state from it's parent schedule then add the new state to the open list.
 * This class implements the Runnable interface, as adding new states to open list will be done in parallel.
 * @author Kelvin Shen
 */
public class StateAdditionThread implements Callable{
    private PriorityBlockingQueue<State> _openList;
    private Set<State> _closedList;
    private State _currentParentState;
    private Node _currentTask;

    public StateAdditionThread(State parentState, Node task, PriorityBlockingQueue<State> priorityQueue, Set<State> closedList){
        _currentParentState = parentState;
        _currentTask = task;
        _openList = priorityQueue;
        _closedList = closedList;
    }

    /**
     * This method is responsible for creating a new state from the given task to be scheduled and it's parent state.
     * This method is call in the Run method, and will be done on a child thread.
     */
    public synchronized void addIndividualTask(){

        //The processor number in State starts indexing from 1
        Set<Integer> processors = _currentParentState.procKeys();

        // startTimes maintains a list of start times for the task to start
        int[] startingTimes = new int[processors.size()];
        this.scheduleAfterPrerequisite(_currentTask, startingTimes);

        //This boolean determines if a task has already been scheduled in a processor, and prevents the task to be
        //scheduled in another processor with same outcome. E.g. Schedule A: {1={0=A}, 2={}} is the same as
        //Schedule B: {1={}, 2={A}}
        boolean taskScheduled = false;

        //adding communication time
        for(int i: processors) {
            if (taskScheduled) {
                continue;
            }

            int nextStartTime = _currentParentState.getNextStartTime(i);

            if (nextStartTime == 0) {
                taskScheduled = true;
            }

            for (int j: processors) {
                if (i != j) {
                    nextStartTime = Math.max(nextStartTime, startingTimes[j - 1]);
                }
            }

            int maxUnderestimate = Math.max(nextStartTime + _currentTask.getAttribute("BottomLevel", Integer.class), _currentParentState.getUnderestimate());

            State child = new State(_currentParentState, maxUnderestimate, _currentTask, i, nextStartTime);

            if (!_closedList.contains(child)) {
                synchronized (_openList) {
                    _openList.add(child);
                }
            }

        }
    }

    /**
     * This method is a subcomponent of the addIndividualTask method.
     * It is responsible for generating two lists. One shows the
     * @param task
     * @param startingTimes
     */
    private void scheduleAfterPrerequisite(Node task, int[] startingTimes){
        Set<Integer> processors = _currentParentState.procKeys();

        //getting the prerequisite task details
        HashSet<Node> prerequisiteTasks = _currentTask.enteringEdges().map(e -> e.getNode0()).collect(Collectors.toCollection(HashSet:: new));

        for (int i: processors) {
            HashMap<Integer, Node> schedule = _currentParentState.getSchedule(i);

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

    @Override
    public Object call() {
        addIndividualTask();
        return 1;
    }
}
