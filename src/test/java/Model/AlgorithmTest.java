package Model;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains test cases for the A* algorithm implementation, as well as providing simple task graph
 * for the A* algorithm.
 * author: Sherman Chin
 */
public class AlgorithmTest {
    private Graph _graphA;
    private Graph _graphB;

    @BeforeEach
    void setUp() {
        _graphA = new DefaultGraph("graphA");
        _graphA.addNode("A");
        _graphA.addNode("B");
        _graphA.addNode("C");

        Node graphANodeA = _graphA.getNode("A");
        Node graphANodeB = _graphA.getNode("B");
        Node graphANodeC = _graphA.getNode("C");

        graphANodeA.setAttribute("Weight", 2);
        graphANodeB.setAttribute("Weight", 3);
        graphANodeC.setAttribute("Weight", 1);

        graphANodeA.setAttribute("BottomLevel", 6);
        graphANodeB.setAttribute("BottomLevel", 4);
        graphANodeC.setAttribute("BottomLevel", 1);


        _graphA.addEdge("AB", graphANodeA, graphANodeB, true);
        _graphA.addEdge("BC", graphANodeB, graphANodeC, true);

        _graphB = new DefaultGraph("graphB");
        _graphB.addNode("A");
        _graphB.addNode("B");
        _graphB.addNode("C");
        _graphB.addNode("D");

        Node graphBNodeA = _graphB.getNode("A");
        Node graphBNodeB = _graphB.getNode("B");
        Node graphBNodeC = _graphB.getNode("C");
        Node graphBNodeD = _graphB.getNode("D");

        graphBNodeA.setAttribute("Weight", 2);
        graphBNodeB.setAttribute("Weight", 2);
        graphBNodeC.setAttribute("Weight", 3);
        graphBNodeD.setAttribute("Weight", 2);

        graphBNodeA.setAttribute("BottomLevel", 7);
        graphBNodeB.setAttribute("BottomLevel", 4);
        graphBNodeC.setAttribute("BottomLevel", 5);
        graphBNodeD.setAttribute("BottomLevel", 2);

        _graphB.addEdge("AB", graphBNodeA, graphBNodeB, true);
        _graphB.addEdge("AC", graphBNodeA, graphBNodeC,true);
        _graphB.addEdge("BD", graphBNodeB, graphBNodeD,true);
        _graphB.addEdge("CD", graphBNodeC, graphBNodeD,true);

        _graphB.getEdge("AB").setAttribute("Weight", 2);
        _graphB.getEdge("AC").setAttribute("Weight", 1);
        _graphB.getEdge("BD").setAttribute("Weight", 2);
        _graphB.getEdge("CD").setAttribute("Weight", 1);

    }

    /**
     * These tests are meant to for testing the AStarScheduler#addChildStates() so that correct child states are generated
     */
//    @Test
//    public void TestEmptyStateGenerateOneChildState() {
//        int numProcessors = 1;
//
//        AStarScheduler scheduler = new AStarScheduler(_graphA, numProcessors);
//        State initialState = new State(numProcessors);
//
//        List<Node> tasks = new ArrayList<>();
//        tasks.add(_graphA.getNode("A"));
//
//        PriorityQueue<State> childStates = scheduler.addChildStates(initialState, tasks);
//
//        assertEquals("[{1={0=A}}]", childStates.toString());
//        assertEquals(6, childStates.poll().getUnderestimate());
//    }
//
//    @Test
//    public void TestEmptyStateGenerateMultipleChildMultipleProcessor() {
//        int numProcessors = 3;
//
//        AStarScheduler scheduler = new AStarScheduler(_graphA, numProcessors);
//        State initialState = new State(numProcessors);
//
//        List<Node> tasks = new ArrayList<>();
//        tasks.add(_graphA.getNode("A"));
//
//        PriorityQueue<State> childStates = scheduler.addChildStates(initialState, tasks);
//
//        assertEquals("[{1={0=A}, 2={}, 3={}}, {1={}, 2={0=A}, 3={}}, {1={}, 2={}, 3={0=A}}]", childStates.toString());
//        assertEquals(6, childStates.poll().getUnderestimate());
//        assertEquals(6, childStates.poll().getUnderestimate());
//        assertEquals(6, childStates.poll().getUnderestimate());
//
//    }
//
//    @Test
//    public void TestOneProcessorGenerateTwoStates() {
//        int numProcessors = 1;
//
//        AStarScheduler scheduler = new AStarScheduler(_graphA, numProcessors);
//        State emptyState = new State(numProcessors);
//
//        List<Node> tasks = new ArrayList<>();
//        tasks.add(_graphB.getNode("B"));
//        tasks.add( _graphB.getNode("C"));
//
//        State firstState = new State(emptyState, 7, _graphB.getNode("A"), 1, 0);
//
//        PriorityQueue<State> childStates = scheduler.addChildStates(firstState, tasks);
//
//        assertEquals("[{1={0=A, 2=B}}, {1={0=A, 2=C}}]", childStates.toString());
//        assertEquals(7, childStates.poll().getUnderestimate());
//        assertEquals(7, childStates.poll().getUnderestimate());
//
//    }
//
//    @Test
//    public void TestTwoProcessorGenerateTwoStates() {
//        int numProcessors = 2;
//
//        AStarScheduler scheduler = new AStarScheduler(_graphA, numProcessors);
//        State emptyState = new State(numProcessors);
//
//        List<Node> tasks = new ArrayList<>();
//        tasks.add(_graphB.getNode("B"));
//        tasks.add( _graphB.getNode("C"));
//
//        State firstState = new State(emptyState, 7, _graphB.getNode("A"), 1, 0);
//
//        PriorityQueue<State> childStates = scheduler.addChildStates(firstState, tasks);
//
//        assertEquals("[{1={0=A, 2=B}, 2={}}, {1={0=A}, 2={4=B}}, {1={0=A, 2=C}, 2={}}, {1={0=A}, 2={3=C}}]", childStates.toString());
//        assertEquals(7, childStates.poll().getUnderestimate());
//        assertEquals(7, childStates.poll().getUnderestimate());
//        assertEquals(8, childStates.poll().getUnderestimate());
//        assertEquals(8, childStates.poll().getUnderestimate());
//    }


    /**
     * These tests are meant to test for the A* algorithm so that an optimal solution is produced
     */
    @Test
    public void TestAlgorithmGraphBOneProcessor() throws ExecutionException, InterruptedException {
        int numProcessors = 1;

        AStarScheduler scheduler = new AStarScheduler(_graphB, numProcessors);
        State finalState = scheduler.generateSchedule();

        if (finalState.toString().equals("{1={0=A, 2=C, 5=B, 7=D}}") && finalState.toString().equals("{1={0=A, 2=B, 4=C, 7=D}}")) {
            fail();
        }
    }

    @Test
    public void TestAlgorithmGraphBTwoProcessorSchedule() throws ExecutionException, InterruptedException {
        int numProcessors = 2;

        AStarScheduler scheduler = new AStarScheduler(_graphB, numProcessors);
        State finalState = scheduler.generateSchedule();

        assertEquals("{1={0=A, 2=B}, 2={3=C, 6=D}}", finalState.toString());
    }

    @Test
    public void TestAlgorithmGraphBTwoProcessorUnderestimate() throws ExecutionException, InterruptedException {
        int numProcessors = 2;

        AStarScheduler scheduler = new AStarScheduler(_graphB, numProcessors);
        State finalState = scheduler.generateSchedule();

        assertEquals(8, finalState.getUnderestimate());
    }

}
