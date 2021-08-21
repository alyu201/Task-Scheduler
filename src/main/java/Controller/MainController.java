package Controller;

import Model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.GraphRenderer;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class handles the displaying and controlling functions of the
 * visual representation of the algorithm scene.
 * It loads the appropriate the FXML file,
 * and contains the required action handlers.
 * @author Amy Lyu
 *
 */

public class MainController implements Initializable {

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
    @FXML
    private Label modeLabel;
    @FXML
    private Label loadingLabel;
    @FXML
    private Label ganttMessageLabel;

    private Graph _graph;
    private XYChart.Series<Number,Number> _threadSeries = new XYChart.Series<>();
    private int _counter = 5; // for x axis of the processor usage line graph

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inputNameLabel.setText(Main.INPUTNAME);
        processorNumLabel.setText(String.valueOf(Main.INPUTPROCNUM));
        // Plot 5 points on line graph as 1 processors used in cases of fast computation speeds
        for (int i = 0; i < 5; i++) {
            _threadSeries.getData().add(new XYChart.Data<>(i,1));
        }
        threadChart.getData().add(_threadSeries);
        _threadSeries.getNode().setStyle("-fx-stroke: #a3c2c2;");
        // Determine the scheduler mode
        if (Main.PARALLELISATIONFLAG) {
            modeLabel.setText("Parallisation (" + Main.NUMPROCESSORS + " processors)");
        }

        String graphStyle = "node {"
                + "size: 25px;"
                + "fill-color: dimgray;"
                + "text-size: 15;"
                + "text-alignment: at-right;"
                + "text-background-mode: rounded-box;"
                + "text-offset: 8, -15;"
                + "text-padding: 5;"
                + "text-color: dimgray;"
                + "z-index: 1;"
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
                + "padding: 60;"
                + "}"

                + "edge {"
                + "fill-color: dimgray;"
                + "text-size: 15;"
                + "text-alignment: center;"
                + "text-background-mode: rounded-box;"
                + "text-offset: 0, 2;"
                + "text-padding: 5;"
                + "text-color: dimgray;"
                + "z-index: 0;"
                + "}";

        // Retrieve graph instance
        _graph = Model.GraphProcessing.Graphprocessing().getGraph();
        // TODO: remove try catch block
        try {
            _graph.setAttribute("ui.stylesheet", graphStyle);
        } catch(NoSuchElementException e) {
            System.out.println("NoSuchException occurred from graph setAttribute");
        }

        // Setup graph display pane
        Platform.runLater(() -> {
            GraphRenderer renderer = new FxGraphRenderer();
            FxViewer v = new FxViewer(_graph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
            v.enableAutoLayout();
            FxViewPanel panel = (FxViewPanel)v.addDefaultView(false, renderer);
            graphPane.getChildren().add(panel);
        });
    }

    /**
     * Mark the tasks in the given state object with its corresponding processor colour.
     * @param state The State object containing the tasks to be coloured.
     */
    public void markNode(State state) {
        if (_graph != null) {
            // Iterate through every node in the graph to set the correct colour for the given state object
            Stream<Node> nodesIterator = _graph.nodes();
            List<Node> nodesList = nodesIterator.collect(Collectors.toList());
            for (Node node : nodesList) {
                String colourMode = "default";
                for (int procNum : state.procKeys()) {
                    // TODO: recheck if this throws a ConcurrentModificationException
                    Collection<Node> procNodes = state.getSchedule(procNum).values();
                    if (procNodes.contains(node)) {
                        colourMode = "proc" + procNum;
                        break;
                    }
                }
                String colour = colourMode;
                Platform.runLater(() -> {
                    // TODO: remove try catch block
                    try {
                        node.setAttribute("ui.class", colour);
                    } catch(ConcurrentModificationException e) {
                        System.out.println("ConcurrentModificationException occurred from markNode");
                    }
                });
            }
            sleep();
        }
    }

    /**
     * A delay for the rendering of coloured tasks.
     */
    public static void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update GUI elapsed time watch.
     * @param time The current elapsed time to be rendered.
     */
    public void incrementTimer(String time) {
        Platform.runLater(() -> {
            elapsedTimeLabel.setText(time);
        });
    }

    /**
     * Updates processor usage line graph with new data.
     * @param processorUseCount The next number of processors used data to be rendered.
     */
    public void showProcessorUsage(int processorUseCount) {
        _counter++;
        // Trim processor usage to the max number of processors specified by the user
        if (processorUseCount > Main.NUMPROCESSORS) {
            processorUseCount = Main.NUMPROCESSORS;
        }

        // Update processor usage
        int finalProcessorUseCount = processorUseCount;
        Platform.runLater(() -> {
            if (_threadSeries.getData().size() > 20) {
                _threadSeries.getData().remove(0);
            }
            _threadSeries.getData().add(new XYChart.Data<>(_counter, finalProcessorUseCount));
            _threadSeries.getNode().setStyle("-fx-stroke: #a3c2c2;");
        });
    }

    /**
     * Displays the gantt chart form of the optimal schedule output when invoked.
     * @param state The optimal output schedule to be rendered.
     */
    public void showGanttChart(State state) {
        Platform.runLater(() -> {
            loadingLabel.setVisible(false);
            ganttMessageLabel.setVisible(false);
            OptimalScheduleGraph stateChart = new OptimalScheduleGraph(state);
            ganttChartPane.getChildren().add(stateChart.getStackedBarChart());
        });
    }
}