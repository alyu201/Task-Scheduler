package Model;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 StateTest class contains unit test cases for State class.
 @author alyu
 */

class StateTest {

    private static State _parent;
    private static State _child;
    private static State _final;

    /**
     * Creates a simple graph and two states.
     */
    @BeforeEach
    void setUp() {
        // Simple graph generated using GraphStream
        Graph graph = new DefaultGraph("graph");
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        Node nodeA = graph.getNode("A");
        Node nodeB = graph.getNode("B");
        Node nodeC = graph.getNode("C");
        nodeA.setAttribute("weight", 2);
        nodeB.setAttribute("weight", 3);
        nodeC.setAttribute("weight", 1);

        _parent = new State(null,7,nodeA,1,0);
        _child = new State(_parent,6,nodeB,2,0);
        _final = new State(_child,5,nodeC,1,2);
    }

    @Test
    void getParentClone() {
        HashMap<Integer,HashMap<Integer,Node>> clone = State.clone(_parent);
        assertTrue(_parent.getState() != clone);
    }

    @Test
    void getChildClone() {
        HashMap<Integer,HashMap<Integer,Node>> clone = State.clone(_child);
        assertTrue(_child.getState() != clone);
    }

    @Test
    void getFinalClone() {
        HashMap<Integer,HashMap<Integer,Node>> clone = State.clone(_final);
        assertTrue(_final.getState() != clone);
    }

    @Test
    void getDifferentParentChild() {
        assertNotEquals(_parent,_child);
        assertTrue(_parent != _child);
    }

    @Test
    void getDifferentChildFinal() {
        assertNotEquals(_child, _final);
        assertTrue(_child != _final);
    }

    @Test
    void getParentUnderestimate() {
        int actUnderestimate = _parent.getUnderestimate();
        assertEquals(7,actUnderestimate);
    }

    @Test
    void getChildUnderestimate() {
        int actUnderestimate = _child.getUnderestimate();
        assertEquals(6,actUnderestimate);
    }

    @Test
    void getFinalUnderestimate() {
        int actUnderestimate = _final.getUnderestimate();
        assertEquals(5,actUnderestimate);
    }

    @Test
    void getParentNextStartTime() {
        int nextStartTime = _parent.getNextStartTime(1);
        assertEquals(2,nextStartTime);
    }

    @Test
    void getChildNextStartTime() {
        int nextStartTime1 = _child.getNextStartTime(1);
        int nextStartTime2 = _child.getNextStartTime(2);
        assertEquals(2,nextStartTime1);
        assertEquals(3,nextStartTime2);
    }

    @Test
    void getFinalNextStartTime() {
        int nextStartTime1 = _final.getNextStartTime(1);
        int nextStartTime2 = _final.getNextStartTime(2);
        assertEquals(3,nextStartTime1);
        assertEquals(3,nextStartTime2);
    }
}