package Model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import org.graphstream.graph.Node;

import java.util.*;

/**
 * This class is responsible for creating the gantt chart
 * displaying the optimal schedule of the output file using the XYChart
 * @author Tanya Li
 */

public class OptimalScheduleGraph {

    // A stacked bar chart is used to visualise the current best schedule found.
    // Each bar represents tasks scheduled on a processor.
    private StackedBarChart<String, Number> ganttChart;
    private CategoryAxis xAxis;

    private HashMap<Integer, HashMap<Integer, Node>> optimalSchedule;
    private int numProcessors;

    /**
     * Creates the gantt chart when the algorithm finishes
     * @param state The state of the final schedule
     */
    public OptimalScheduleGraph(State state){
        optimalSchedule = state.getState();

        initialiseSchedule();
        populateSchedule();
    }

    /**
     * This method sets up initial values for labels of the gantt chart in the GUI
     */
    public void initialiseSchedule() {
        numProcessors = optimalSchedule.size();

        //Configuring xAxis and yAxis
        xAxis = new CategoryAxis();
        xAxis.setLabel("Processors");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time");

        //Configuring StackedBarChart
        ganttChart = new StackedBarChart(xAxis, yAxis);
        ganttChart.setLegendVisible(false);
    }

    /**
     * This is called when the algorithm finalises, it adds tasks to specific
     * processors in the stackedBarChart on the GUI. When there is a gap between
     * the start time of the task and the current finish time of the processor,
     * this method creates idle time to be added to the stackedBarChart, and makes it
     * invisible, as the bar chart cannot have gaps.
     * The schedule graph reflects the output dot file.
     */
    public void populateSchedule() {
        // Clear the data already in the bar chart
        ganttChart.getData().clear();

        String[] processors = new String[numProcessors];
        for (int i = 0; i < numProcessors; i++) {
            processors[i] = "" + (i+1);
        }

        // For each process, get all of its nodes and place them in the schedules chart
        for (int i = 0; i < numProcessors; i++) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(processors)));
            series.setName(processors[i]);
        }

        Node prevNode = null;
        int prevKey = 0;

        // Working implementation
        for (int proc : optimalSchedule.keySet()) {
            XYChart.Series series = new XYChart.Series();
            // Adding series of tasks to the gantt chart (stackedBarChart)
            ganttChart.getData().add(series);

            HashMap<Integer, Node> schedule = optimalSchedule.get(proc);
            Map<Integer,Node> ordered = new TreeMap<>(schedule);

            String currProcessor = processors[proc - 1];
            boolean firstNode = true;

            XYChart.Data<String, Number> idle;
            XYChart.Data<String, Number> data;
            // Looping through all tasks scheduled on the processor
            for (int startTime : ordered.keySet()) {

                if(prevNode == null){
                    prevNode = ordered.get(startTime);
                }
                double prevFinishTime = prevKey + (double)prevNode.getAttribute("Weight");
                double duration = (double)ordered.get(startTime).getAttribute("Weight");

                if(startTime != 0 && firstNode){
                    // Add idle time if the first task does not start at 0
                    idle = new XYChart.Data<>(currProcessor, startTime);
                    series.getData().add(idle);
                    idle.getNode().setStyle("-fx-background-color: transparent;");
                    // Add the first task to the corresponding processor
                    data = new XYChart.Data<>(currProcessor, duration);
                    series.getData().add(data);
                    data.getNode().setStyle(" -fx-border-color: #f4f4f4; -fx-border-width: 2px;");
                } else if (startTime != prevFinishTime && startTime != 0){ //finish time of prev task scheduled
                    // Add idle time blocks between two scheduled tasks
                    idle = new XYChart.Data<>(currProcessor, startTime-prevFinishTime);
                    series.getData().add(idle);
                } else {
                    // Add actual task blocks
                    data = new XYChart.Data<>(currProcessor, duration);
                    series.getData().add(data);
                    data.getNode().setStyle(" -fx-border-color: #f4f4f4; -fx-border-width: 2px;");
                }
                prevKey = startTime;
                prevNode = ordered.get(startTime);
                firstNode = false;
            }
        }
    }

    /**
     * Getter method for the gantt chart displaying the final optimal schedule solution
     * @return ganttChart
     */
    public StackedBarChart<String, Number> getStackedBarChart() {
        return ganttChart;
    }
}