package Model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Main application that takes in input for preprocessing and initialising visualiser
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("../View/primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) throws IOException {
        //Launches visualiser window
        launch();

        //-----------From Main.java (below)------------------------------
        // TODO: 8/3/2021  Check number of argument must be at least 2
        // TODO: 8/3/2021 Creat Exceptions

        //Process the input dot file
        String firstArgument = args[0];
        System.out.println(firstArgument);
        String filePath = Paths.get(firstArgument).toAbsolutePath().toString();
        GraphProcessing graph = new GraphProcessing();
        graph.inputProcessing(filePath);
        graph.outputProcessing(filePath);

        //Process the number of processor argument
        String secondArgument = args[1];
        int numberOfProcess = Integer.parseInt(secondArgument);
        System.out.println(numberOfProcess);

        //Process the other arguments
        // TODO: 8/3/2021 processing other arguments
        int numberArg = args.length;
        ArrayList<String> arguments = new ArrayList<>();
        while (numberArg>2){
            numberArg--;
            arguments.add(args[numberArg]);
        }
        System.out.println(arguments);

    }
    public void coreArgProcedure(int numCores){
        // TODO: 8/3/2021  implementation for number of cores
    }

    public void visualArgProcedure(){
        // TODO: 8/3/2021 implement for visualization
    }

    public void outputArgProcedure(String outputFilename){
        // TODO: 8/3/2021 implement for output argument
    }
}