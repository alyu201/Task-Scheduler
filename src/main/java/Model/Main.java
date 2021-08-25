package Model;

import javafx.animation.AnimationTimer;
import org.apache.commons.lang3.time.StopWatch;
import org.graphstream.graph.Graph;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This is the Main class of the project, an entry point to the project. This
 * class responsible for input argument processing. This class handles the
 * displaying and controlling functions of the visual representation of the
 * algorithm scene. It loads the appropriate the FXML file, and contains the
 * required action handlers.
 * 
 * @author Kelvin Shen
 *
 */

public class Main {
    public static Boolean VISUALISATIONFLAG = false;
    public static Boolean PARALLELISATIONFLAG = false;
    public static String OUTPUTNAME;
    public static String INPUTNAME;
    public static int INPUTPROCNUM;
    public static int NUMPROCESSORS = 1;

    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());

        try {
            long startTime = System.currentTimeMillis();

            // through an exception if a dot file and number of processors not provided.
            if (args.length < 2) {
                throw new InvalidInputArgumentException(
                        "Input arguments must at least specify a .dot file and a number of processors");
            }

            // Process the input dot file
            INPUTNAME = args[0];
            String filePath = Paths.get(INPUTNAME).toAbsolutePath().toString();
            if (!filePath.contains(".dot")) {
                throw new InvalidInputArgumentException("The input filename needs a .dot extension.");
            }
            GraphProcessing graphProcessing = GraphProcessing.Graphprocessing();
            graphProcessing.inputProcessing(filePath);
            Graph graph = graphProcessing.getGraph();

            // Process the optional arguments
            processingOptions(args, graphProcessing, logger);

            // User specified to have application program open
            visualArgProcedure();

            // Process the number of processor argument, then start scheduling
            int numberOfProcess = Integer.parseInt(args[1]);
            INPUTPROCNUM = numberOfProcess;
            logger.info("Start scheduling...");
            long startScheduleTime = System.currentTimeMillis();
            Scheduler scheduler;
            if (graph.getNodeCount() > 11 || (graph.getNodeCount() == 11 && INPUTPROCNUM > 5)) {
                scheduler = new BranchAndBoundScheduler(graph, numberOfProcess);
            } else {
                scheduler = new AStarScheduler(graph, numberOfProcess);
            }
            State state = scheduler.generateSchedule();

            // Update visualisation and terminate the visualisation thread
            if (VISUALISATIONFLAG) {
                Visualiser.stopElapsedTime();
                Visualiser.displayStateChart(state);
                VisualThread.VisualThread().join();
            }

            // Exit program procedure
            long endScheduleTime = System.currentTimeMillis();
            logger.info("The scheduling process took " + (endScheduleTime - startScheduleTime) + " milliseconds to finish.");
            outputArgProcedure(OUTPUTNAME, graphProcessing, state);
            long endTime = System.currentTimeMillis();
            logger.info("The program took " + (endTime - startTime) + " milliseconds to finish.");
            System.exit(0);

        } catch (IOException e) {
            logger.info("Make sure your dot file is in the same directory level as the jar file!");
        } catch (InvalidInputArgumentException | InterruptedException e1) {
            logger.info("There is an error in your input argument!");
        }
    }

    /**
     * This method is responsible for processing all the optional arguments such as
     * -p for number of cores; -v for visualization; - o for output filename.
     *
     * @throws IOException
     */
    public static void processingOptions(String[] args, GraphProcessing graphProcessing, Logger logger)
            throws IOException, InterruptedException {
        int numberArg = args.length;
        ArrayList<String> arguments = new ArrayList<>();
        while (numberArg > 2) {
            numberArg--;
            arguments.add(args[numberArg]);
        }

        // process -p argument
        if (arguments.contains("-p")) {
            PARALLELISATIONFLAG = true;
            int indexOfp = arguments.indexOf("-p");
            NUMPROCESSORS = Integer.parseInt(arguments.get(indexOfp - 1));
        }

        // process -v argument
        if (arguments.contains("-v")) {
            logger.info("Starting visualisation...");
            VISUALISATIONFLAG = true;
        }

        // process -o argument
        if (arguments.contains("-o")) {
            int indexOfo = arguments.indexOf("-o");
            OUTPUTNAME = arguments.get(indexOfo - 1);
        } else {
            OUTPUTNAME = args[0].substring(0, args[0].length() - 4) + "-output";
        }
    }

    /**
     * This method is responsible for initiating the visualization if specified by
     * the user.
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
     * This method is responsible for output a .dot file using graph that is in the
     * system.
     *
     * @param outputFilename
     * @param graphProcessing
     * @throws IOException
     */
    public static void outputArgProcedure(String outputFilename, GraphProcessing graphProcessing, State state)
            throws IOException {
        graphProcessing.outputProcessing(outputFilename, state);
    }
}
