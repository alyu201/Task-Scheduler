package Model;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class MultipleRootsDetachedAlgorithmTest {
    private Graph _graphA;

    @BeforeEach
    void setUp() {
        _graphA = new DefaultGraph("graphA");
        _graphA.addNode("A");
        _graphA.addNode("B");
        _graphA.addNode("C");
        _graphA.addNode("D");
        _graphA.addNode("E");
        _graphA.addNode("F");
        _graphA.addNode("G");


        Node graphANodeA = _graphA.getNode("A");
        Node graphANodeB = _graphA.getNode("B");
        Node graphANodeC = _graphA.getNode("C");
        Node graphANodeD = _graphA.getNode("D");
        Node graphANodeE = _graphA.getNode("E");
        Node graphANodeF = _graphA.getNode("F");
        Node graphANodeG = _graphA.getNode("G");

        //Adding weights to nodes
        graphANodeA.setAttribute("Weight", 2);
        graphANodeB.setAttribute("Weight", 2);
        graphANodeC.setAttribute("Weight", 3);
        graphANodeD.setAttribute("Weight", 1);
        graphANodeE.setAttribute("Weight", 3);
        graphANodeF.setAttribute("Weight", 5);
        graphANodeG.setAttribute("Weight", 6);

        //Adding bottom levels to nodes
        graphANodeA.setAttribute("BottomLevel", 8);
        graphANodeB.setAttribute("BottomLevel", 8);
        graphANodeC.setAttribute("BottomLevel", 3);
        graphANodeD.setAttribute("BottomLevel", 6);
        graphANodeE.setAttribute("BottomLevel", 3);
        graphANodeF.setAttribute("BottomLevel", 5);
        graphANodeG.setAttribute("BottomLevel", 6);

        //Adding weighted edges to nodes
        Edge edgeAD = _graphA.addEdge("AD", graphANodeA, graphANodeD, true);
        edgeAD.setAttribute("Weight", 1);
        Edge edgeBG = _graphA.addEdge("BG", graphANodeB, graphANodeG, true);
        edgeBG.setAttribute("Weight", 1);
        Edge edgeDE = _graphA.addEdge("DE", graphANodeD, graphANodeE, true);
        edgeDE.setAttribute("Weight", 1);
        Edge edgeAE = _graphA.addEdge("AE", graphANodeA, graphANodeE, true);
        edgeAE.setAttribute("Weight", 2);
        Edge edgeBC = _graphA.addEdge("BC", graphANodeB, graphANodeC, true);
        edgeBC.setAttribute("Weight", 3);
        Edge edgeDF = _graphA.addEdge("DF", graphANodeD, graphANodeF, true);
        edgeDF.setAttribute("Weight", 5);

    }

    /**
     * These tests are meant to test for the A* algorithm so that an optimal solution is produced
     */
    @Test
    public void TestAlgorithmGraphAFourProcessorUnderestimate() {
        int numProcessors = 4;

        AStarScheduler scheduler = new AStarScheduler(_graphA, numProcessors);
        State finalState = scheduler.generateSchedule();

        assertEquals(8, finalState.getUnderestimate());
    }

}
