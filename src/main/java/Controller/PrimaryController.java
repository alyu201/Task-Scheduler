package Controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.GraphRenderer;

import java.io.IOException;

/**
 * This class handles the displaying and controlling functions of the
 * visual representation of the algorithm scene.
 * It loads the appropriate the FXML file,
 * and contains the required action handlers.
 * @author Amy Lyu
 *
 */

public class PrimaryController {

    @FXML
    private AnchorPane graphPane;
    private Graph _graph;
    private int _nextIdx = 12;

    @FXML
    public void initialize() {
        // Simple stub graph generated initialised
        Graph graph = new DefaultGraph("Graph");
        _graph = graph;

        _graph.setStrict(false);

        // GraphStream visualiser added to AnchorPane of primaryStage
        GraphRenderer renderer = new FxGraphRenderer();
        FxViewer v = new FxViewer(_graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        v.enableAutoLayout();
        FxViewPanel panel = (FxViewPanel)v.addDefaultView(false, renderer);
        graphPane.getChildren().add(panel);

        _graph.addNode("1"); _graph.addNode("2"); _graph.addNode("3"); _graph.addNode("4"); _graph.addNode("5");
        _graph.addNode("6"); _graph.addNode("7"); _graph.addNode("8"); _graph.addNode("9"); _graph.addNode("10");
        _graph.addNode("11");
        _graph.addEdge("12","1","2", true);
        _graph.addEdge("13","1","3", true);

        _graph.addEdge("24","2","4", true);
        _graph.addEdge("25","2","5", true);
        _graph.addEdge("26","2","6", true);
        _graph.addEdge("27","2","7", true);

        _graph.addEdge("38","3","8", true);
        _graph.addEdge("39","3","9", true);
        _graph.addEdge("310","3","10", true);
        _graph.addEdge("311","3","11", true);
    }

    public void initialize(Graph graph) {

    }

    @FXML
    private void addNodes() throws IOException {
//        Visualiser.setRoot("/View/secondary");
        _graph.addNode(""+_nextIdx); _graph.addNode(""+(_nextIdx+1));
        _graph.addEdge((_nextIdx-1)+"-"+_nextIdx,""+(_nextIdx-1),""+_nextIdx, true);
        _graph.addEdge((_nextIdx-1)+"-"+(_nextIdx+1),""+(_nextIdx-1),""+(_nextIdx+1), true);
        _nextIdx++;
    }
}
