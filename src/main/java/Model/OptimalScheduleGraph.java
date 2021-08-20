package Model;

import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import org.graphstream.graph.Node;

import java.util.*;

public class OptimalScheduleGraph {

    // A stacked bar chart is used to visualise the current best schedule found.
    // Each bar represents tasks scheduled on a processor.
    @FXML
    private StackedBarChart<String, Number> ganttChart;

    private HashMap<Integer, HashMap<Integer, Node>> optimalSchedule;
    private int numProcessors;

    public void OptimalScheduleGraph(State state){
        optimalSchedule = state.getState();

        initialiseSchedule();
        populateSchedule();
        getStackedBarChart();
    }

    /**
     * This method sets up initial values for labels in the GUI
     */
    public void initialiseSchedule() {
        numProcessors = optimalSchedule.size();

        //Configuring xAxis and yAxis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Processors");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Time");

        //Configuring StackedBarChart
        ganttChart = new StackedBarChart(xAxis, yAxis);
        ganttChart.setTitle("Optimal Schedule");
    }

    /**
     * This is called when the algorithm finalises, it builds the nodes for each
     * processor to be visualised based on the final output. The schedule graph
     * reflects the output dot file.
     */
    public void populateSchedule() {
        String[] processors = new String[numProcessors];
        for (int i = 1; i <= numProcessors; i++) {
            processors[i] = "Processor" + (i);
        }

        // for each process, get all of its nodes and place them in the schedules chart
        for (int i = 1; i<=numProcessors; i++) {
            XYChart.Series series = new XYChart.Series();

            int key = optimalSchedule.get(i).hashCode();
            Node node = optimalSchedule.get(i).get(key);

            series.getData().add(new XYChart.Data<>(processors[i], node));

            //Adding series java to the stackedBarChart
            ganttChart.getData().add(series);
        }
    }

    private StackedBarChart<String, Number> getStackedBarChart() {
        return ganttChart;
    }
}