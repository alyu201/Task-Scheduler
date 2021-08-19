package Controller;

import Model.Main;
import Model.State;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.GraphRenderer;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the displaying and controlling functions of the
 * visual representation of the algorithm scene.
 * It loads the appropriate the FXML file,
 * and contains the required action handlers.
 * @author Amy Lyu
 *
 */

public class MainController {

    @FXML
    private AnchorPane graphPane;
    @FXML
    private AnchorPane ganttChartPane;
    @FXML
    private LineChart<Number, Number> threadChart;
    @FXML
    private Label inputNameLabel;
    @FXML
    private Label processorNumLabel;
    @FXML
    private Label elapsedTimeLabel;

    private Graph _graph;
    private XYChart.Series<Number,Number> _threadSeries = new XYChart.Series<>();
    private int _counter = 0;

    public void initialize() {
        inputNameLabel.setText(Main.INPUTNAME);
        processorNumLabel.setText(String.valueOf(Main.INPUTPROCNUM));

        String graphStyle = "node {"
                + "size: 25px;"
                + "fill-color: dimgray;"
                + "text-size: 15;"
                + "text-alignment: at-right;"
                + "text-background-mode: rounded-box;"
                + "text-offset: 8, -15;"
                + "text-padding: 5;"
                + "text-color: dimgray;"
                + "}"

                + "node.default {"
                + "fill-color: dimgray;"
                + "}"
                + "node.proc1 {"
                + "fill-color: #ffb3ba;"
                + "}"
                + "node.proc2 {"
                + "fill-color: #ffd288;"
                + "}"
                + "node.proc3 {"
                + "fill-color: #ffecbb;"
                + "}"
                + "node.proc4 {"
                + "fill-color: #cbedc9;"
                + "}"
                + "node.proc5 {"
                + "fill-color: #ccf2fe;"
                + "}"
                + "node.proc6 {"
                + "fill-color: #c4d4ff;"
                + "}"
                + "node.proc7 {"
                + "fill-color: #e6d3fe;"
                + "}"
                + "node.proc8 {"
                + "fill-color: #f5d5e6;"
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

        // Retrieve graph instance
        _graph = Model.GraphProcessing.Graphprocessing().getGraph();
        // TODO: remove dummy root node in display
        // Remove any dummy nodes
//        if (_graph.getNode("dummyRoot") != null) {
//            _graph.removeNode("dummyRoot");
//        }

        // Add node id as label
        Iterator nodesIterator = _graph.nodes().iterator();
        while (nodesIterator.hasNext()) {
            Node node = (Node) nodesIterator.next();
            node.setAttribute("ui.label", "Node: " + node.getId() + "\n" + node.getAttribute("Weight"));
        }

        // Add edge weight as label
        Iterator edgesIterator = _graph.edges().iterator();
        while (edgesIterator.hasNext()) {
            Edge edge = (Edge) edgesIterator.next();
            edge.setAttribute("ui.label", edge.getAttribute("Weight"));
        }
        _graph.setAttribute("ui.stylesheet", graphStyle);

        // Setup graph display pane
        GraphRenderer renderer = new FxGraphRenderer();
        FxViewer v = new FxViewer(_graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        v.enableAutoLayout();
        FxViewPanel panel = (FxViewPanel)v.addDefaultView(false, renderer);
        graphPane.getChildren().add(panel);
        threadChart.getData().add(_threadSeries);
    }

    public void markNode(State state) {
        for (Node node : _graph) {
            String colourMode = "default";
            for (int procNum : state.procKeys()) {
                Collection<Node> procNodes = state.getSchedule(procNum).values();
                if (procNodes.contains(node)) {
                    colourMode = "proc" + procNum;
                    break;
                }
            }
            String colour = colourMode;
            Platform.runLater(() -> {
                node.setAttribute("ui.class", colour);
            });
        }
        sleep();
    }

    public void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void incrementTimer(String time) {
        Platform.runLater(() -> {
            elapsedTimeLabel.setText(time);
        });
    }

    public void showThreadUsage(int threadCount) {
        _counter++;
        if (threadCount > 0) {
            Platform.runLater(() -> {
                if (_threadSeries.getData().size() > 20) {
                    _threadSeries.getData().remove(0);
                }
                _threadSeries.getData().add(new XYChart.Data<>(_counter, threadCount));
                _threadSeries.getNode().setStyle("-fx-stroke: #a3c2c2;");
            });
        }
    }
}