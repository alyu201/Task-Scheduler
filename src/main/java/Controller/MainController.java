package Controller;

import Model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
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
import java.util.concurrent.ConcurrentHashMap;
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

        // Retrieve graph instance
        _graph = Model.GraphProcessing.Graphprocessing().getGraph();

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
                    // Converted to ConcurrentHashMap for multithreading use
                    ConcurrentHashMap<Integer,Node> procSchedules = new ConcurrentHashMap<>(state.getSchedule(procNum));
                    Collection<Node> procNodes = procSchedules.values();
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
    }

    /**
     * A delay for the rendering of coloured tasks.
     */
    public static void sleep() {
        int timeout = 150;
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
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
            // Move the graph along to the left
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