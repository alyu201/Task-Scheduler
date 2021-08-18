package Model;

import Controller.MainController;
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
    private static MainController _controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        _primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(Visualiser.class.getResource("/View/MainScene.fxml"));
        _scene = new Scene(loader.load());
        _controller = loader.getController();

        primaryStage.setScene(_scene);
        primaryStage.setTitle("Visualiser");
        primaryStage.setResizable(false);

        _controller.initialize();
        primaryStage.show();
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
}