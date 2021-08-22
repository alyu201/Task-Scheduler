package Model;

import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
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

        // Working implementation
        for (int proc : optimalSchedule.keySet()) {
            XYChart.Series series = new XYChart.Series();
            // Adding series of tasks to the gantt chart (stackedBarChart)
            ganttChart.getData().add(series);

            HashMap<Integer, Node> schedule = optimalSchedule.get(proc);
            String currProcessor = processors[proc - 1];
            int prevKey = 0;
            Node prevNode = null;

            XYChart.Data<String, Number> idle = null;
            // Looping through all tasks scheduled on the processor
            Map<Integer,Node> orderedSchedule = new TreeMap<>(schedule);
            System.out.println(orderedSchedule);
            for (int startTime : orderedSchedule.keySet()) {
                if(prevNode == null){
                    prevNode = orderedSchedule.get(startTime);
                }
                double prevFinishTime = prevKey + (double)prevNode.getAttribute("Weight");
                double duration = (double)orderedSchedule.get(startTime).getAttribute("Weight");

                System.out.printf("PrevKey: %s PrevFin: %s Duration: %s\n", prevKey, prevFinishTime, duration);

                 if (startTime != prevFinishTime) { //finish time of prev task scheduled
                     // Add idle time blocks between two scheduled tasks
                     System.out.println("idle time is from in between");
                     System.out.println("startime: " + startTime + ", previousfintime: " + prevFinishTime);
                     System.out.println("");
                     idle = new XYChart.Data<>(currProcessor, Math.abs(startTime - prevFinishTime));
                     series.getData().add(idle);
                    idle.getNode().setStyle("-fx-background-color: transparent");
                 } else if(startTime != 0){
                    // Add idle time if the first task does not start at 0
                    System.out.println("idle time is from start");
                    idle = new XYChart.Data<>(currProcessor, startTime);
                    series.getData().add(idle);
                    idle.getNode().setStyle("-fx-background-color: transparent");
                    // Add the first task to the corresponding processor
                    series.getData().add(new XYChart.Data<>(currProcessor, duration));
                } else {
                    // Add actual task blocks
                    series.getData().add(new XYChart.Data<>(currProcessor, duration));
                }
                prevKey = startTime;
                prevNode = orderedSchedule.get(startTime);
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