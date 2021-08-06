package Model;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;


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

    @Test
    public void TestEmptyStateGenerateOneChildState() {
        int numProcessors = 1;

        AStarScheduler scheduler = new AStarScheduler(_graphA, numProcessors);
        State initialState = new State(numProcessors);

        List<Node> tasks = new ArrayList<>();
        tasks.add(_graphA.getNode("A"));

        assertEquals("[{1={0=A}}]", scheduler.addChildStates(initialState, tasks).toString());
    }

    @Test
    public void TestEmptyStateGenerateMultipleChildMultipleProcessor() {
        int numProcessors = 3;

        AStarScheduler scheduler = new AStarScheduler(_graphA, numProcessors);
        State initialState = new State(numProcessors);

        List<Node> tasks = new ArrayList<>();
        tasks.add(_graphA.getNode("A"));

        assertEquals("[{1={0=A}, 2={}, 3={}}, {1={}, 2={0=A}, 3={}}, {1={}, 2={}, 3={0=A}}]", scheduler.addChildStates(initialState, tasks).toString());
    }

    @Test
    public void TestOneProcessorGenerateTwoStates() {
        int numProcessors = 1;

        AStarScheduler scheduler = new AStarScheduler(_graphA, numProcessors);
        State emptyState = new State(numProcessors);

        List<Node> tasks = new ArrayList<>();
        tasks.add(_graphA.getNode("B"));
        tasks.add( _graphB.getNode("C"));

        State firstState = new State(emptyState, 7, _graphA.getNode("A"), 1, 0);

        assertEquals("[{1={0=A, 2=B}}, {1={0=A, 2=C}}]", scheduler.addChildStates(firstState, tasks).toString());
    }

    @Test
    public void TestTwoProcessorGenerateTwoStates() {
        int numProcessors = 2;

        AStarScheduler scheduler = new AStarScheduler(_graphA, numProcessors);
        State emptyState = new State(numProcessors);

        List<Node> tasks = new ArrayList<>();
        tasks.add(_graphB.getNode("B"));
        tasks.add( _graphB.getNode("C"));

        State firstState = new State(emptyState, 7, _graphB.getNode("A"), 1, 0);

        PriorityQueue<State> childStates = scheduler.addChildStates(firstState, tasks);
        assertEquals("[{1={0=A, 2=B}, 2={}}, {1={0=A}, 2={4=B}}, {1={0=A, 2=C}, 2={}}, {1={0=A}, 2={3=C}}]", childStates.toString());
    }

    @Test
    public void TestAlgorithmGraphBOneProcessor() {
        int numProcessors = 1;

        AStarScheduler scheduler = new AStarScheduler(_graphB, numProcessors);
        State finalState = scheduler.generateSchedule();

        assertEquals("", finalState);
    }

}
