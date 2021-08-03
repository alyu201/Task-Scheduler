import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
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
