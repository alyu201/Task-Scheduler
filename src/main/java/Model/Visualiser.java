package Model;

import Controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class handles the displaying and controlling functions of the
 * main scene. It loads the appropriate FXML file, and contains
 * the required action handlers.
 * @author Amy Lyu
 */

public class Visualiser extends Application {

    private static Scene _scene;
    private static Boolean _completed = false;
    private static int _threadCount = 0;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Visualiser.class.getResource("/View/MainScene.fxml"));
        _scene = new Scene(loader.load());
        MainController controller = loader.getController();

        primaryStage.setScene(_scene);
        primaryStage.setTitle("Visualiser");
        primaryStage.setMinWidth(1259);
        primaryStage.setMinHeight(893);

        primaryStage.show();

        // Start elapsed time count
        long startTime = System.nanoTime();
        AtomicLong currentTime = new AtomicLong();

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        Runnable updateElapsedTime = () -> {
            String format = String.format("%%0%dd", 2);
            // Update current elapsed time
            currentTime.set(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime));
            long time = currentTime.longValue() / 1000;
            String milliSeconds = String.format("%03d", currentTime.longValue() % 1000);
            String seconds = String.format(format, time % 60);
            String minutes = String.format(format, (time % 3600) / 60);
            // Formats elapsed time into mm:ss.sss
            String elapsedTime =  minutes + ":" + seconds + "." + milliSeconds;
            // Update GUI
            controller.incrementTimer(elapsedTime);
            // Display the number of threads used at every 100ms
            if (currentTime.longValue() % 100 == 0) {
                controller.showThreadUsage(_threadCount);
            }
            // Stop elapsed time counter when algorithm finishes
            if (_completed) {
                executor.shutdown();
            }
        };
        executor.scheduleAtFixedRate(updateElapsedTime, 0, 1, TimeUnit.MILLISECONDS);
    }

    public static void start() throws IOException {
        launch();
    }

    public static void update(State state) {
        FXMLLoader loader = new FXMLLoader(Visualiser.class.getResource("/View/MainScene.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MainController ctrl = loader.getController();
        ctrl.markNode(state);
//        MainController.markNode(state);
    }

    public static void stopElapsedTime () {
        _completed = true;
    }

    public static void incrThreadCount() {
        _threadCount++;
    }

    public static void resetThreadCount() {
        _threadCount = 0;
    }
}