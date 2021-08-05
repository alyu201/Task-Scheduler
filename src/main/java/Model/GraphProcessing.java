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
        String outputFilename = filePath.concat(".dot");
        try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilename)))){
            out.write("digraph \""+filePath+"\" "+"{");
            out.newLine();
            //writing nodes one by one to the file
            for (Node node : graph) {
                String nodeWeight = node.getAttribute("Weight").toString();
                out.write(node.toString()+" ["+"Weight="+nodeWeight+"];");
                out.newLine();

                //writing out going edges one by one to the file
                node.leavingEdges().forEach(edge -> {
                    String edgeUnformatted = edge.toString();
                    int subStart=edgeUnformatted.indexOf("[")+1;
                    int subEnd=edgeUnformatted.length()-1;
                    String edgeFormatted = edgeUnformatted.substring(subStart,subEnd);
                    String edgeWeight = edge.getAttribute("Weight").toString();
                    try {
                        out.write(edgeFormatted+" ["+"Weight="+edgeWeight+"];");
                        out.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            out.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
