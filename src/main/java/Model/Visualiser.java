package Model;

import Controller.MainController;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
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

    private static Stage _primaryStage;
    private static Scene _scene;
    private static Boolean _completed = false;

    @Override
    public void start(Stage primaryStage) throws IOException {
        _primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(Visualiser.class.getResource("/View/MainScene.fxml"));
        _scene = new Scene(loader.load());
        MainController controller = loader.getController();

        primaryStage.setScene(_scene);
        primaryStage.setTitle("Visualiser");
        primaryStage.setMinWidth(1259);
        primaryStage.setMinHeight(893);

        controller.initialize();
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
            String seconds = String.format(format, time % 60);
            String minutes = String.format(format, (time % 3600) / 60);
            // Formats elapsed time into mm:ss
            String elapsedTime =  minutes + ":" + seconds;
            // Update GUI
            controller.incrementTimer(elapsedTime);

            // Stop elapsed time counter when algorithm finishes
            if (_completed) {
                executor.shutdown();
            }
        };
        executor.scheduleAtFixedRate(updateElapsedTime, 0, 1, TimeUnit.SECONDS);
    }

    public static void setRoot(String fxml) throws IOException {
        _scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Visualiser.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void start() throws IOException {
        launch();
    }

    public static void update(State state) {
        FXMLLoader loader = new FXMLLoader(Visualiser.class.getResource("/View/MainScene.fxml"));
        try {
            loader.load();
            MainController ctrl = loader.getController();
            ctrl.markNode(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopElapsedTime () {
        _completed = true;
    }
}