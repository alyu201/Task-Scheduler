package Model;

import java.io.IOException;

/**
 * This class is responsible for starting the JavaFX application on a new thread.
 * This is a singleton class, which means that other parts of the program
 * can change the state of the thread by get the singleton instance.
 * @author kelvin
 */
public class VisualThread extends Thread{
    // static variable single_instance of type Singleton
    private static VisualThread _single_instance = null;

    /**
     *private constructor restricted to this class itself
     */
    private VisualThread() { }

    /**
     * static method to create instance of VisualThread class
     */
    public static VisualThread VisualThread() {
        // To ensure only one instance is created
        if (_single_instance == null) {
            _single_instance = new VisualThread();
        }
        return _single_instance;
    }

    /**
     * This method is responsible for starting the JavaFx application.
     */
    @Override
    public void run(){
        try {
            Visualiser.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
