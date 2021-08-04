package Model;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
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

        graphANodeA.setAttribute("weight", 2);
        graphANodeB.setAttribute("weight", 3);
        graphANodeC.setAttribute("weight", 1);

        graphANodeA.setAttribute("bottomLevel", 6);
        graphANodeB.setAttribute("bottomLevel", 4);
        graphANodeC.setAttribute("bottomLevel", 1);


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

        graphBNodeA.setAttribute("weight", 2);
        graphBNodeB.setAttribute("weight", 2);
        graphBNodeC.setAttribute("weight", 3);
        graphBNodeD.setAttribute("weight", 2);

        graphBNodeA.setAttribute("bottomLevel", 7);
        graphBNodeB.setAttribute("bottomLevel", 4);
        graphBNodeC.setAttribute("bottomLevel", 5);
        graphBNodeD.setAttribute("bottomLevel", 2);

        _graphB.addEdge("AB", graphBNodeA, graphBNodeB, true);
        _graphB.addEdge("AC", graphBNodeA, graphBNodeC,true);
        _graphB.addEdge("BD", graphBNodeB, graphBNodeD,true);
        _graphB.addEdge("CD", graphBNodeC, graphBNodeD,true);

    }

    @Test
    public void TestGraphAOneProcessor() {
        AStarScheduler scheduler = new AStarScheduler(_graphA, 1);

        State schedule = scheduler.generateSchedule();

        assertEquals("", schedule);
    }

}
