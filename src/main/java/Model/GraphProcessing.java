package Model;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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
            for (Node node : graph) {
                System.out.println(node);
                for(Edge edge:node)
                {
                    System.out.println(edge);
                }
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
        try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.dot")))){
            out.write("digraph {");
            out.newLine();
//            for(Edge e:d.getEdges()){
//                out.write(e.v+" -> "+e.w);
//                out.newLine();
//            }
            out.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
