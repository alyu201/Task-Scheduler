package Model;


import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;

import java.io.IOException;

public class VisualThread extends Thread{
    // static variable single_instance of type Singleton
    private static VisualThread _single_instance = null;

    /**
     *private constructor restricted to this class itself
     */
    private VisualThread() { }


    /**
     * static method to create instance of GraphProcessing class
     */
    public static VisualThread VisualThread() {
        // To ensure only one instance is created
        if (_single_instance == null) {
            _single_instance = new VisualThread();
        }
        return _single_instance;
    }

    public void run(){
        System.out.println("The new thread's id is "+ currentThread().getId());
        try {
            System.out.println("here");
            Visualiser.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
