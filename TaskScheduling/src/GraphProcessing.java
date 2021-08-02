import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

import java.io.IOException;
import java.util.Iterator;

/**
This class is used for processing input dot file and output dot file.
 @author kelvi
 */
public class GraphProcessing {
    private Graph graph = new DefaultGraph("graph");


    /**
     * This method takes a path to a dot file and uses the GraphStream library to convert the file content to a graph.
     * @throws IOException
     * @author Kelvin
     */
    public void inputProcessing(String filePath) throws IOException {
        FileSource fileSource = FileSourceFactory.sourceFor(filePath);
        fileSource.addSink(this.graph);
        try {
            fileSource.readAll(filePath);
            System.out.println(graph);
            System.out.println(graph.getEdgeCount());
            Iterator i = graph.iterator();
            while (i.hasNext()) {
                System.out.println(i.next());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileSource.removeSink(graph);
        }
    }

    /**
     * This method will write the graph that is currently in the system to a dot file.
     */
    public void outputProcessing(String filePath) throws IOException {
        graph.write(filePath);
    }
}
