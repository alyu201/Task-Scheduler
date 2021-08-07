package Model;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MultipleRootsBroadAlgorithmTest {
    private Graph _graphA;

    @BeforeEach
    void setUp() {
        _graphA = new DefaultGraph("graphA");

        Node graphANodeA = _graphA.addNode("A");
        Node graphANodeB = _graphA.addNode("B");
        Node graphANodeC = _graphA.addNode("C");
        Node graphANodeD = _graphA.addNode("D");
        Node graphANodeE = _graphA.addNode("E");
        Node graphANodeF = _graphA.addNode("F");
        Node graphANodeG = _graphA.addNode("G");
        Node graphANodeH = _graphA.addNode("H");
        Node graphANodeI = _graphA.addNode("I");

        //Adding weights to nodes
        graphANodeA.setAttribute("Weight", 2);
        graphANodeB.setAttribute("Weight", 3);
        graphANodeC.setAttribute("Weight", 2);
        graphANodeD.setAttribute("Weight", 2);
        graphANodeE.setAttribute("Weight", 4);
        graphANodeF.setAttribute("Weight", 1);
        graphANodeG.setAttribute("Weight", 3);
        graphANodeH.setAttribute("Weight", 2);
        graphANodeI.setAttribute("Weight", 3);

        //Adding bottom levels to nodes
        graphANodeA.setAttribute("BottomLevel", 9);
        graphANodeB.setAttribute("BottomLevel", 9);
        graphANodeC.setAttribute("BottomLevel", 4);
        graphANodeD.setAttribute("BottomLevel", 4);
        graphANodeE.setAttribute("BottomLevel", 7);
        graphANodeF.setAttribute("BottomLevel", 4);
        graphANodeG.setAttribute("BottomLevel", 6);
        graphANodeH.setAttribute("BottomLevel", 2);
        graphANodeI.setAttribute("BottomLevel", 3);

        //Adding weighted edges to nodes
        Edge edgeAE = _graphA.addEdge("AE", graphANodeA, graphANodeE, true);
        edgeAE.setAttribute("Weight", 1);
        Edge edgeCH = _graphA.addEdge("CH", graphANodeC, graphANodeH, true);
        edgeCH.setAttribute("Weight", 1);
        Edge edgeAD = _graphA.addEdge("AD", graphANodeA, graphANodeD, true);
        edgeAD.setAttribute("Weight", 2);
        Edge edgeGI = _graphA.addEdge("GI", graphANodeG, graphANodeI, true);
        edgeGI.setAttribute("Weight", 2);
        Edge edgeDH = _graphA.addEdge("DH", graphANodeD, graphANodeH, true);
        edgeDH.setAttribute("Weight", 3);
        Edge edgeFI = _graphA.addEdge("FI", graphANodeF, graphANodeI, true);
        edgeFI.setAttribute("Weight", 3);
        Edge edgeAC = _graphA.addEdge("AC", graphANodeA, graphANodeC, true);
        edgeAC.setAttribute("Weight", 4);
        Edge edgeBF = _graphA.addEdge("BF", graphANodeB, graphANodeF, true);
        edgeBF.setAttribute("Weight", 5);
        Edge edgeBG = _graphA.addEdge("BG", graphANodeB, graphANodeG, true);
        edgeBG.setAttribute("Weight", 5);
        Edge edgeEI = _graphA.addEdge("EI", graphANodeE, graphANodeI, true);
        edgeEI.setAttribute("Weight", 10);

    }

    /**
     * These tests are meant to test for the A* algorithm so that an optimal solution is produced
     */
    @Test
    public void TestAlgorithmGraphATwoProcessorUnderestimate() {
        int numProcessors = 2;

        AStarScheduler scheduler = new AStarScheduler(_graphA, numProcessors);
        State finalState = scheduler.generateSchedule();

        assertEquals(12, finalState.getUnderestimate());
    }

}
