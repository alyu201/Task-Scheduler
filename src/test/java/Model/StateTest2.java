package Model;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 StateTest class contains unit test cases for State class.
 @author alyu
 */

class StateTest2 {
    private static State _s1;
    private static State _s2;
    private static State _s3;
    private static State _s4;
    private static State _s5;

    /**
     * Creates a simple graph and 5 states.
     */
    @BeforeEach
    void setUp() {
        // Simple graph generated using GraphStream
        Graph graph = new DefaultGraph("graph");
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        graph.addNode("E");
        Node nodeA = graph.getNode("A");
        Node nodeB = graph.getNode("B");
        Node nodeC = graph.getNode("C");
        Node nodeD = graph.getNode("D");
        Node nodeE = graph.getNode("E");
        nodeA.setAttribute("weight", 3);
        nodeB.setAttribute("weight", 4);
        nodeC.setAttribute("weight", 5);
        nodeD.setAttribute("weight", 6);
        nodeE.setAttribute("weight", 1);

        State _empty = new State(3);
        _s1 = new State(_empty,12,nodeA,1,0);
        _s2 = new State(_s1,12,nodeB,1,3);
        _s3 = new State(_s2,12,nodeC,1,7);
        _s4 = new State(_s3,11,nodeD,2,5);
        _s5 = new State(_s4,9,nodeE,3,8);
    }

    /**
     * States are different to each other
     */
    @Test
    void getDifferentS1AndS2() {
        assertNotEquals(_s1,_s2);
        assertNotSame(_s1,_s2);
    }

    @Test
    void getDifferentS2AndS3() {
        assertNotEquals(_s2,_s3);
        assertNotSame(_s2,_s3);
    }

    @Test
    void getDifferentS3AndS4() {
        assertNotEquals(_s3,_s4);
        assertNotSame(_s3,_s4);
    }

    @Test
    void getDifferentS4AndS5() {
        assertNotEquals(_s4,_s5);
        assertNotSame(_s4,_s5);
    }



    /**
     * Testing underestimates
     */
    @Test
    void getS1Underestimate() {
        int actUnderestimate = _s1.getUnderestimate();
        assertEquals(12,actUnderestimate);
    }

    @Test
    void getS2Underestimate() {
        int actUnderestimate = _s2.getUnderestimate();
        assertEquals(12,actUnderestimate);
    }

    @Test
    void getS3Underestimate() {
        int actUnderestimate = _s3.getUnderestimate();
        assertEquals(12,actUnderestimate);
    }

    @Test
    void getS4Underestimate() {
        int actUnderestimate = _s4.getUnderestimate();
        assertEquals(11,actUnderestimate);
    }

    @Test
    void getS5Underestimate() {
        int actUnderestimate = _s5.getUnderestimate();
        assertEquals(9,actUnderestimate);
    }


    /**
     * Testing nextStartTime of each state for each processor
     */
    @Test
    void getS1NextStartTime() {
        int nextStartTime = _s1.getNextStartTime(1);
        int nextStartTime2 = _s1.getNextStartTime(2);
        int nextStartTime3 = _s1.getNextStartTime(3);
        assertEquals(3,nextStartTime);
        assertEquals(3,nextStartTime2);
        assertEquals(3,nextStartTime3);
    }

    @Test
    void getS2NextStartTime() {
        int nextStartTime = _s2.getNextStartTime(1);
        int nextStartTime2 = _s2.getNextStartTime(2);
        int nextStartTime3 = _s2.getNextStartTime(3);
        assertEquals(7,nextStartTime);
        assertEquals(3,nextStartTime2);
        assertEquals(3,nextStartTime3);
    }

    @Test
    void getS3NextStartTime() {
        int nextStartTime = _s3.getNextStartTime(1);
        int nextStartTime2 = _s3.getNextStartTime(2);
        int nextStartTime3 = _s3.getNextStartTime(3);
        assertEquals(12,nextStartTime);
        assertEquals(3,nextStartTime2);
        assertEquals(3,nextStartTime3);
    }

    @Test
    void getS4NextStartTime() {
        int nextStartTime = _s4.getNextStartTime(1);
        int nextStartTime2 = _s4.getNextStartTime(2);
        int nextStartTime3 = _s4.getNextStartTime(3);
        assertEquals(12,nextStartTime);
        assertEquals(11,nextStartTime2);
        assertEquals(3,nextStartTime3);
    }




    @Test
    void testOutputFunction() throws IOException {
        GraphProcessing graph = GraphProcessing.Graphprocessing();
        graph.inputProcessing("input.dot");
        graph.outputProcessing("example",_s5);
        System.out.println(_s5);
    }
}