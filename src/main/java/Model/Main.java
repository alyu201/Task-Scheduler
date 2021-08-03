package Model;

import Model.GraphProcessing;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        // TODO: 8/3/2021  Check number of argument must be at least 2
        // TODO: 8/3/2021 Creat Exceptions

        try {
            //Process the input dot file
            String filePath = Paths.get(args[0]).toAbsolutePath().toString();
            if (!filePath.contains(".dot")){
                throw new InvalidInputFilenameException("The input filename needs a .dot extension.");
            }
            GraphProcessing graph = new GraphProcessing();
            graph.inputProcessing(filePath);

            //Process the number of processor argument
            int numberOfProcess = Integer.parseInt(args[1]);
            System.out.println(numberOfProcess);

            //Process the other arguments
            processingOptions(args, graph);

            //Example output a dot file
            graph.outputProcessing(filePath);

        }catch(Exception e){
            System.out.println(e);
        }





    }

    public static void processingOptions(String[] args, GraphProcessing graph) throws IOException {
        int numberArg = args.length;
        ArrayList<String> arguments = new ArrayList<>();
        while (numberArg>2){
            numberArg--;
            arguments.add(args[numberArg]);
        }

        if (arguments.contains("-p")){
            int indexOfp = arguments.indexOf("-p");
            int numCore = Integer.parseInt(arguments.get(indexOfp-1));
            coreArgProcedure(numCore);
        }

        if (arguments.contains("-v")){
            visualArgProcedure();
        }

        if (arguments.contains("-o")){
            int indexOfo = arguments.indexOf("-o");
            String outputFilename = arguments.get(indexOfo-1);
            outputArgProcedure(outputFilename,graph);
        }
    }

    public static void coreArgProcedure(int numCores){
        System.out.println("the number of core is "+numCores);
        // TODO: 8/3/2021 implementation for parallelization required
    }

    public static void visualArgProcedure(){
        System.out.println("require visualization");
        // TODO: 8/3/2021 implementation for visualization required
    }

    public static void outputArgProcedure(String outputFilename, GraphProcessing graph) throws IOException {
        graph.outputProcessing(outputFilename);
    }
}
