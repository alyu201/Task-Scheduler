package Controller;

import Model.Visualiser;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.GraphRenderer;

import java.io.IOException;

public class PrimaryController {

    @FXML
    private AnchorPane graphPane;

    @FXML
    public void initialize() {
        // Simple graph generated as example
        Graph graph = new SingleGraph("Graph");
        graph.addNode("1"); graph.addNode("2"); graph.addNode("3"); graph.addNode("4"); graph.addNode("5");
        graph.addNode("6"); graph.addNode("7"); graph.addNode("8"); graph.addNode("9"); graph.addNode("10");
        graph.addNode("11");
//        graph.addEdge("12","1","2", true);
//        graph.addEdge("13","1","3", true);
//
//        graph.addEdge("24","2","4", true);
//        graph.addEdge("25","2","5", true);
//        graph.addEdge("26","2","6", true);
//        graph.addEdge("27","2","7", true);
//
//        graph.addEdge("38","3","8", true);
//        graph.addEdge("39","3","9", true);
//        graph.addEdge("310","3","10", true);
//        graph.addEdge("311","3","11", true);

        // GraphStream visualiser added to AnchorPane of primaryStage
        GraphRenderer renderer = new FxGraphRenderer();
        FxViewer v = new FxViewer(graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        v.enableAutoLayout();
        FxViewPanel panel = (FxViewPanel)v.addDefaultView(false, renderer);
        Scene scene2 = new Scene(panel, 800, 600);
        graphPane.getChildren().add(scene2.getRoot());
    }

    @FXML
    private void switchToSecondary() throws IOException {
        Visualiser.setRoot("/View/secondary");
    }
}
