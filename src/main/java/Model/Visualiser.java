package Model;

import Controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
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
    private static int _processorUseCount = 1;
    private static State _state;
    private static int _freq = Main.INPUTPROCNUM;
    private static int _count = 0;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Set the max between the number of tasks and the number of scheduling processors
        if (GraphProcessing.Graphprocessing().getGraph().getNodeCount() > _freq) {
            _freq = GraphProcessing.Graphprocessing().getGraph().getNodeCount();
        }

        // Setup window
        FXMLLoader loader = new FXMLLoader(Visualiser.class.getResource("/View/MainScene.fxml"));
        _scene = new Scene(loader.load());
        MainController controller = loader.getController();

        primaryStage.setScene(_scene);
        primaryStage.setTitle("Visualiser");
        primaryStage.setMinWidth(1400);
        primaryStage.setMinHeight(860);

        _scene.getStylesheets().add("/Style/VisualiserStyle.css");

        primaryStage.show();

        // Display the number of processors used at every 100ms
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Display processor usage going back down to 1
                if (_completed) {
                    for (int i = 0; i < 2; i++) { controller.showProcessorUsage(1); }
                } else {
                    controller.showProcessorUsage(_processorUseCount);
                }
            }
        }, 0, 100);

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

            // Stop elapsed time counter and processor usage timer when algorithm finishes
            // Update GUI with final optimal state info
            if (_completed) {
                controller.markNode(_state);
                controller.showGanttChart(_state);
                executor.shutdown();
                timer.cancel();
            }
        };
        executor.scheduleAtFixedRate(updateElapsedTime, 0, 1, TimeUnit.MILLISECONDS);
    }

    /**
     * This method is called to initiate the visualier application.
     * @throws IOException
     */
    public static void start() throws IOException {
        launch();
    }

    /**
     * This updates the task graph of the GUI by rendering the tasks in the given state object with its corresponding
     * processor colour.
     * @param state The State object to update the task graph of the GUI with.
     */
    public static void update(State state) {
        _state = state;
        _count++;
        if (_count == _freq) {
            FXMLLoader loader = new FXMLLoader(Visualiser.class.getResource("/View/MainScene.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            MainController ctrl = loader.getController();
            ctrl.markNode(state);
            _count = 0;
        }
    }

    /**
     * Stops the elapsed time background thread.
     */
    public static void stopElapsedTime () {
        _completed = true;
    }

    /**
     * Increments the processor/thread counter for processor usage tracking.
     */
    public static void incrThreadCount() {
        _processorUseCount++;
    }

    /**
     * Resets the processor/thread counter back to 1.
     */
    public static void resetThreadCount() {
        _processorUseCount = 1;
    }

    /**
     * Called to display a gantt chart format of the optimal state (schedule) output.
     * @param state The optimal state (schedule) to be rendered.
     */
    public static void displayStateChart(State state) {
        _state = state;
    }
}