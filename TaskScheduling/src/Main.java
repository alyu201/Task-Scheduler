import java.io.IOException;

public class Main {
    public static void main(String... args) throws IOException {
        GraphProcessing graph = new GraphProcessing();
        String filePath = "C:\\Users\\kelvi\\Uni-Git\\project-1-project-1-team-6\\input.dot";
        graph.inputProcessing(filePath);
        filePath = "C:\\Users\\kelvi\\Uni-Git\\project-1-project-1-team-6\\input1.dot";
        graph.outputProcessing(filePath);
    }
}
