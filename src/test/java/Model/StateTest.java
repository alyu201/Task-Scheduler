package Model;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 StateTest class contains unit test cases for State class.
 @author alyu
 */

class StateTest {
    private static State _parent;
    private static State _child;
    private static State _child2;
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
        graph.addNode("D");
        Node nodeA = graph.getNode("A");
        Node nodeB = graph.getNode("B");
        Node nodeC = graph.getNode("C");
        Node nodeD = graph.getNode("D");
        nodeA.setAttribute("weight", 2);
        nodeB.setAttribute("weight", 3);
        nodeC.setAttribute("weight", 1);
        nodeD.setAttribute("weight", 2);

        State _empty = new State(2);
        _parent = new State(_empty,7,nodeA,1,0);
        _child = new State(_parent,6,nodeB,2,0);
        _child2 = new State(_child,5,nodeC,1,2);
        _final = new State(_child2,5,nodeD,1,3);
    }

    @Test
    void getDifferentParentChild() {
        assertNotEquals(_parent,_child);
        assertNotSame(_parent, _child);
    }

    @Test
    void getDifferentChildFinal() {
        assertNotEquals(_child, _final);
        assertNotSame(_child, _final);
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
        int nextStartTime2 = _parent.getNextStartTime(2);
        assertEquals(2,nextStartTime);
        assertEquals(0,nextStartTime2);

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
        assertEquals(5,nextStartTime1);
        assertEquals(3,nextStartTime2);
    }
}