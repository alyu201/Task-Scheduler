package Model;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scheduler {
    protected Graph _taskGraph;
    protected int _numProcessors;

    public Scheduler(Graph taskGraph, int numProcessors){
        _numProcessors = numProcessors;
        _taskGraph = taskGraph;
    }

    public List<Node> getNextTasks(State state){
        List<HashMap<Integer, Node>> schedules = state.getAllSchedules();

        List<Node> scheduledTasks = new ArrayList<Node>();

        for (HashMap<Integer, Node> i : schedules) {
            scheduledTasks.addAll(i.values());
        }
        List<Node> schedulableTasks = TaskGraphUtil.getNextSchedulableTasks(_taskGraph, scheduledTasks);

        return schedulableTasks;
    }

    public boolean goalStateReached(State state){
        List<HashMap<Integer, Node>> schedules = state.getAllSchedules();

        List<Node> scheduledTasks = new ArrayList<Node>();

        for (HashMap<Integer, Node> i : schedules) {
            scheduledTasks.addAll(i.values());
        }

        return TaskGraphUtil.allTaskScheduled(_taskGraph, scheduledTasks);
    }

}
