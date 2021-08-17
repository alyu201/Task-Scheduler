package Controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.graphstream.graph.Edge;
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
import java.util.Iterator;
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

    public void initialize(Graph graph) {
        String graphStyle = "node {"
                + "size: 20px;"
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
                + "text-size: 15;"
                + "text-alignment: center;"
                + "text-background-mode: rounded-box;"
                + "text-offset: 0, 2;"
                + "text-padding: 5;"
                + "text-color: dimgray;"
                + "}";

        _graph = Model.GraphProcessing.Graphprocessing().getGraph();
        _graph.removeNode("dummyRoot");

        // Add node id as label
        for (Node node : _graph) {
            node.setAttribute("ui.label", node.getId());
        }

        Iterator edgesIterator = _graph.edges().iterator();
        while (edgesIterator.hasNext()) {
            Edge edge = (Edge) edgesIterator.next();
            edge.setAttribute("ui.label", edge.getAttribute("Weight"));
        }

        // Setup graph display pane
        GraphRenderer renderer = new FxGraphRenderer();
        FxViewer v = new FxViewer(_graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        v.enableAutoLayout();
        FxViewPanel panel = (FxViewPanel)v.addDefaultView(false, renderer);
        graphPane.getChildren().add(panel);

        _graph.setAttribute("ui.stylesheet", graphStyle);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
