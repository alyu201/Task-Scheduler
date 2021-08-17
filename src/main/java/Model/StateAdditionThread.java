package Model;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.Node;

import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is responsible for creating a new state from it's parent schedule then add the new state to the open list.
 * This class implements the Runnable interface, as adding new states to open list will be done in parallel.
 * @author Kelvin Shen
 */
public class StateAdditionThread implements Runnable{
    private PriorityQueue<State> _openList;
    private State _currentParentState;
    private Node _currentTask;

    public StateAdditionThread(State parentState, Node task, PriorityQueue<State> priorityQueue){
        _currentParentState = parentState;
        _currentTask = task;
        _openList = priorityQueue;
    }

    /**
     * This method is responsible for creating a new state from the given task to be scheduled and it's parent state.
     * This method is call in the Run method, and will be done on a child thread.
     */
    public void addIndividualTask(){

        //TODO: The processor number in State starts indexing from 1
        Set<Integer> processors = _currentParentState.procKeys();

        // prerequistieNodePos maintains a list of prerequisites, each is the last prerequisite on a schedule
        Node[] prerequisiteNodePos = new Node[processors.size()];

        // startTimes maintains a list of start times for the task to start
        int[] startingTimes = new int[processors.size()];
        this.scheduleAfterPrerequisite(_currentTask,prerequisiteNodePos,startingTimes);

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
                if (i != j && prerequisiteNodePos[j - 1] != null) {
                    int communicationCost = Double.valueOf(prerequisiteNodePos[j - 1].getEdgeToward(_currentTask).getAttribute("Weight").toString()).intValue();
                    nextStartTime = Math.max(nextStartTime, startingTimes[j - 1] + communicationCost);
                }
            }

            int maxUnderestimate = Math.max(nextStartTime + _currentTask.getAttribute("BottomLevel", Integer.class), _currentParentState.getUnderestimate());

            State child = new State(_currentParentState, maxUnderestimate, _currentTask, i, nextStartTime);
            _openList.add(child);
        }
    }

    /**
     * This method is a subcomponent of the addIndividualTask method.
     * It is responsible for generating two lists. One shows the
     * @param task
     * @param prerequisiteNodePos
     * @param startingTimes
     */
    private void scheduleAfterPrerequisite(Node task, Node[] prerequisiteNodePos, int[] startingTimes){
        Set<Integer> processors = _currentParentState.procKeys();

        //getting the prerequisite task details
        List<Node> prerequisiteTasks = task.enteringEdges().map(Edge::getNode0).collect(Collectors.toList());
        List<String> prerequisiteTasksId = prerequisiteTasks.stream().map(Element::getId).collect(Collectors.toList());

        for (int i: processors) {
            HashMap<Integer, Node> schedule = _currentParentState.getSchedule(i);

            //if current schedule has prerequisite tasks, change the start time to after the finishing time of the prerequisites.
            for (int startTime: schedule.keySet()) {
                //change start time to prerequisite task finishing time
                if (prerequisiteTasksId.contains(schedule.get(startTime).getId())) {

                    //Processor indexing starts from 1
                    int currentStartTime = startingTimes[i - 1];
                    int afterPrerequisiteStartTime = startTime + Double.valueOf(schedule.get(startTime).getAttribute("Weight").toString()).intValue();

                    if (afterPrerequisiteStartTime > currentStartTime) {
                        startingTimes[i - 1] = afterPrerequisiteStartTime;
                        prerequisiteNodePos[i - 1] = schedule.get(startTime);
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        addIndividualTask();
    }
}
