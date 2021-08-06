package Model;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * This is the Main class of the project, an entry point to the project.
 * This class responisble for input argument processing.
 *
 * @author Kelvin
 */
public class Main {
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
            GraphProcessing graph = GraphProcessing.Graphprocessing();
            graph.inputProcessing(filePath);

            //Process the number of processor argument
            int numberOfProcess = Integer.parseInt(args[1]);

            //Process the other arguments
            processingOptions(args, graph);

            System.out.println("The Program Ends");
            System.exit(0);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * This method is responsible for processing all the optional arguments such as
     * -p for number of cores; -v for visualization; - o for output filename.
     *
     * @throws IOException
     */
    public static void processingOptions(String[] args, GraphProcessing graph) throws IOException {
        int numberArg = args.length;
        ArrayList<String> arguments = new ArrayList<>();
        while (numberArg > 2) {
            numberArg--;
            arguments.add(args[numberArg]);
        }

        //process -p argument
        if (arguments.contains("-p")) {
            int indexOfp = arguments.indexOf("-p");
            int numCore = Integer.parseInt(arguments.get(indexOfp - 1));
            coreArgProcedure(numCore);
        }

        //process -v argument
        if (arguments.contains("-v")) {
            visualArgProcedure();
        }

        //process -o argument
        if (arguments.contains("-o")) {
            int indexOfo = arguments.indexOf("-o");
            String outputFilename = arguments.get(indexOfo - 1);
            outputArgProcedure(outputFilename, graph);
        }
    }

    /**
     * This method is responsible for initiating parallelization when
     * the client specify number of cores to work in parallel.
     *
     * @param numCores
     */
    public static void coreArgProcedure(int numCores) {
        System.out.println("the number of core is " + numCores);
        // TODO: 8/3/2021 implementation for parallelization required
    }

    /**
     * This method is responsible for initiating the visualization if specified by the user.
     *
     * @throws IOException
     */
    public static void visualArgProcedure() throws IOException {
        System.out.println("require visualization");
        Visualiser.start();
    }

    /**
     * This method is responsible for output a .dot file using graph that is in the system.
     *
     * @param outputFilename
     * @param graph
     * @throws IOException
     */
    public static void outputArgProcedure(String outputFilename, GraphProcessing graph) throws IOException {
        State state = new State(2);
        graph.outputProcessing(outputFilename, state);
    }
}
