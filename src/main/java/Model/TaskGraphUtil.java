package Model;

import org.graphstream.graph.ElementNotFoundException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is a utility class for the algorithm to help find information related to the task graph
 * author: Sherman Chin
 */
public class TaskGraphUtil {

    /**
     * This method removes the dummy root node that may have been added to the task graph
     * @param graph The task graph
     */
    public static void removeDummyRootNode (Graph graph) {
        try {
            graph.removeNode("dummyRoot");
        }catch (ElementNotFoundException e) {
            return;
        }
    }

    /**
     * This method gives all the tasks within a graph
     * @param graph The graph to be evaluated
     * @return A list of Node (tasks)
     */
    public static List<Node> allTasksInGraph(Graph graph) {
        List<Node> allTasks = graph.nodes().collect(Collectors.toList());

        return allTasks;
    }


    /**
     * This method determines if all the tasks in the graph has been scheduled
     * @param graph The task graph
     * @param scheduledTasks The list of tasks that had been scheduled
     * @return True means all tasks has been scheduled, false otherwise.
     */
    public static boolean allTaskScheduled(Graph graph, List<Node> scheduledTasks) {

        //TODO: Remove root to reduce iterations
        List<Node> allTasks = allTasksInGraph(graph);
        HashSet<Node> scheduledTasksSet = scheduledTasks.stream().collect(Collectors.toCollection(HashSet:: new));

        allTasks.removeAll(scheduledTasksSet);
        if (allTasks.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method gives the next tasks that can be scheduled given a list of already-scheduled tasks
     * @param graph The task graph
     * @param currentTasks The tasks that had already been scheduled
     * @return A list of tasks that can be scheduled
     */
    public static List<Node> getNextSchedulableTasks(Graph graph, List<Node> currentTasks) {
        List<Node> allTasks = allTasksInGraph(graph);

        HashMap<Node, Integer> taskCounter = new HashMap<Node, Integer>();

        for (Node n: allTasks) {
            taskCounter.put(n, n.getInDegree());
        }

        //Used for O(1) removal of scheduled tasks from the allTasks list
        HashSet<Node> currentTaskSet = new HashSet<Node>();

        for (Node n: currentTasks) {
            currentTaskSet.add(n);
            n.leavingEdges().forEach(edge -> taskCounter.put(edge.getNode1(), taskCounter.get(edge.getNode1()) - 1));
        }

        allTasks.removeAll(currentTaskSet);
        allTasks.removeIf(n -> taskCounter.get(n) > 0 || currentTaskSet.contains(n));

        return allTasks;
    }

}
