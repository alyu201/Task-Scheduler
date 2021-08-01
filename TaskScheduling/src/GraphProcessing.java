import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

import java.io.IOException;
import java.util.Iterator;

public class GraphProcessing {
    public static void main(String ... args) throws IOException {
        String filePath = "C:\\Users\\kelvi\\Uni-Git\\project-1-project-1-team-6\\input.dot";
        Graph g = new DefaultGraph("g");
        FileSource fs = FileSourceFactory.sourceFor(filePath);

        fs.addSink(g);
        try {
            fs.readAll(filePath);
            System.out.println(g);
            System.out.println(g.getEdgeCount());
            Iterator i = g.iterator();
            while (i.hasNext()) {
                System.out.println(i.next());
            }

        } catch( IOException e) {
            e.printStackTrace();
        } finally {
            fs.removeSink(g);
        }
    }
    }
