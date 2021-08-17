package Model;

import org.graphstream.graph.Graph;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

/**
 * This is the Main class of the project, an entry point to the project.
 * This class responsible for input argument processing.
 * This class handles the displaying and controlling functions of the
 * visual representation of the algorithm scene.
 * It loads the appropriate the FXML file, and contains the required action handlers.
 * @author Kelvin Shen
 *
 */

public class Main {
    public static Boolean VISUALISATIONFLAG = false;
    public static String OUTPUTNAME;
    public static int NUMPROCESSORS = 1;

    public static void main(String[] args) {

        try {
            //through an exception if a dot file and number of processors not provided.
            if (args.length < 2) {
                throw new InvalidInputArgumentException("Input arguments must at least specify a .dot file and a number of processors");
            }

            //Process the input dot file
            String filePath = Paths.get(args[0]).toAbsolutePath().toString();
            if (!filePath.contains(".dot")) {
                throw new InvalidInputArgumentException("The input filename needs a .dot extension.");
            }
            GraphProcessing graphProcessing = GraphProcessing.Graphprocessing();
            graphProcessing.inputProcessing(filePath);
            Graph graph = graphProcessing.getGraph();

            //Process the optional arguments
            processingOptions(args, graphProcessing);

            //User specified to have application program open
            visualArgProcedure();

            //Process the number of processor argument, then start scheduling
            int numberOfProcess = Integer.parseInt(args[1]);
            AStarScheduler aStarScheduler = new AStarScheduler(graph, numberOfProcess);
            State state = aStarScheduler.generateSchedule();


            //End of program procedure
            VisualThread.VisualThread().join();
            outputArgProcedure(OUTPUTNAME, graphProcessing,state);
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is responsible for processing all the optional arguments such as
     * -p for number of cores; -v for visualization; - o for output filename.
     *
     * @throws IOException
     */
    public static void processingOptions(String[] args, GraphProcessing graphProcessing) throws IOException, InterruptedException {
        int numberArg = args.length;
        ArrayList<String> arguments = new ArrayList<>();
        while (numberArg > 2) {
            numberArg--;
            arguments.add(args[numberArg]);
        }

        //process -p argument
        if (arguments.contains("-p")) {
            int indexOfp = arguments.indexOf("-p");
            NUMPROCESSORS = Integer.parseInt(arguments.get(indexOfp - 1));
            System.out.print("the number of processes is "+ NUMPROCESSORS);
        }

        //process -v argument
        if (arguments.contains("-v")) {
            VISUALISATIONFLAG = true;
        }

        //process -o argument
        if (arguments.contains("-o")) {
            int indexOfo = arguments.indexOf("-o");
            OUTPUTNAME = arguments.get(indexOfo - 1);
        }else{
            OUTPUTNAME = args[0].substring(0,args[0].length()-4)+"-output";
        }
    }



    /**
     * This method is responsible for initiating the visualization if specified by the user.
     *
     * @throws IOException
     */
    public static void visualArgProcedure() throws IOException {
        if (VISUALISATIONFLAG) {
            VisualThread visualThread = VisualThread.VisualThread();
            visualThread.start();
        }
    }

    /**
     * This method is responsible for output a .dot file using graph that is in the system.
     *
     * @param outputFilename
     * @param graphProcessing
     * @throws IOException
     */
    public static void outputArgProcedure(String outputFilename, GraphProcessing graphProcessing, State state) throws IOException {
        graphProcessing.outputProcessing(outputFilename, state);
    }
}
