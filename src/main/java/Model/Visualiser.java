package Model;

import Controller.PrimaryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;

import java.io.IOException;

/**
 * This class handles the displaying and controlling functions of the
 * main scene. It loads the appropriate FXML file, and contains
 * the required action handlers.
 * @author Amy Lyu
 */

public class Visualiser extends Application {

    private static Stage _primaryStage;
    private static Scene _scene;
    private static Graph _graph;

    @Override
    public void start(Stage primaryStage) throws IOException {
        _primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(Visualiser.class.getResource("/View/primary.fxml"));
        _scene = new Scene(loader.load());
        primaryStage.setScene(_scene);
        primaryStage.setTitle("Visualiser");
        primaryStage.setResizable(false);
        PrimaryController controller = loader.getController();
        controller.initialize(_graph);
        primaryStage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        _scene.setRoot(loadFXML(fxml));
    }

    public static Parent getRoot() {
        return _scene.getRoot();
    }

    public static void setScene(Scene scene) {
        _primaryStage.setScene(scene);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Visualiser.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void start(Graph graph) throws IOException {
        System.setProperty("org.graphstream.ui", "javafx");
        _graph = graph;
        launch();
    }
}