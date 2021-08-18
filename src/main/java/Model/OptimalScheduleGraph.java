package Model;

import java.util.Arrays;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.graphstream.graph.Node;

public class OptimalScheduleGraph{

    public OptimalScheduleGraph(State stateDiagram) {

//      stage.setTitle("Gantt Chart Sample");

        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();

        final GanttChart<Number, String> chart = new GanttChart<Number,String>(xAxis,yAxis);
        xAxis.setLabel("");
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setMinorTickCount(4);

        yAxis.setLabel("");
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);
//        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(processes)));

        chart.setTitle("Optimal Schedule");
        chart.setLegendVisible(false);
        chart.setBlockHeight(50);
        HashMap<Integer, HashMap<Integer, Node>> d = stateDiagram.getState();
        for(int i=0; i<d.size(); i++){
            String process = "Process" + i;
            XYChart.Series series = new XYChart.Series();
            series.getData().add(new XYChart.Data());
            chart.getData().add(series);
        }

//        process = processes[1];
//        XYChart.Series series2 = new XYChart.Series();
//        series2.getData().add(new XYChart.Data(0, process, new ExtraData( 1, "status-green")));

//        Scene scene  = new Scene(chart,620,350);
//        stage.setScene(scene);
//        stage.show();
    }
}
