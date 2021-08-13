package Controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.GraphRenderer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * This class handles the displaying and controlling functions of the
 * visual representation of the algorithm scene.
 * It loads the appropriate the FXML file,
 * and contains the required action handlers.
 * @author Amy Lyu
 *
 */

public class MainController implements PropertyChangeListener {

    @FXML
    private AnchorPane graphPane;
    private Graph _graph = new DefaultGraph("Graph");
    private int _nextIdx = 12;

    public void initialize(Graph graph) {
        // TODO: zoom and pan options
        // TODO: implement PropertyChangeListener
        // TODO: try to get url to stylesheet file working
        String graphStyle = "node {"
                + "size: 30px;"
                + "fill-color: dimgray;"
                + "text-size: 15;"
                + "text-alignment: under;"
                + "text-background-mode: rounded-box;"
                + "text-offset: 0, 2;"
                + "text-padding: 5;"
                + "text-color: dimgray;"
                + "}"

                + "graph {"
                + "}"

                + "edge {"
                + "fill-color: dimgray;"
                + "}";

        // Retrieve the nodes and edges of the input graph
        HashSet<String> nodes = graph.nodes().filter(n -> !(n.getId().equals("dummyRoot"))).map(n -> n.getId()).collect(Collectors.toCollection(HashSet:: new));
        HashSet<String> edges = graph.edges().filter(n -> !(n.getId().contains("dummyRoot"))).map(n -> n.getId()).collect(Collectors.toCollection(HashSet:: new));
        System.out.println(nodes);
        System.out.println(edges);

        // Add nodes to new graph for display
        for (String id : nodes) {
            _graph.addNode(id);
        }

        // Add node id as label
        for (Node node : _graph) {
            node.setAttribute("ui.label", node.getId());
        }

        // Setup graph display pane
        GraphRenderer renderer = new FxGraphRenderer();
        FxViewer v = new FxViewer(_graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        v.enableAutoLayout();
        FxViewPanel panel = (FxViewPanel)v.addDefaultView(false, renderer);
        graphPane.getChildren().add(panel);

        _graph.setAttribute("ui.stylesheet", graphStyle);
//        String resource = MainController.class.getResource("/Styles/MainSceneStylesheet.css").getPath();
//        System.out.println(resource);
//        _graph.setAttribute("ui.stylesheet", "url('file:///"+resource+"')");

        // TODO: consider performance when tasks grow
        // Add edges to graph for display (must be done after display pane setup
        for (String id : edges) {
            String[] nodeIds = id.substring(1, id.length()-1).split(";");
            _graph.addEdge(id, nodeIds[0], nodeIds[1]);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
