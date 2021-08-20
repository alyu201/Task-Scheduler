package Model;
import org.graphstream.graph.Node;

import java.util.HashMap;
import java.util.Set;

/**
 * This interface represents a general state in the state graph.
 * It has all the methods that the StudState and the ModelState should have.
 *
 * The main reason for creating this interface was so that we can have the
 * StudState and the ModelState classes implement it. This will allow us to
 * not need to refactor so much code in order to continue to implement code
 * while testing the implementation at the same time.

 * @author Megan Lim
 */
public interface State {

    /**
     * Retrieves the maximum underestimate of the state.
     * @return int of the maximum underestimate of the state object.
     */
    public int getUnderestimate();


    /**
     * Retrieve the next starting time for a given processor.
     * @param processor The processor from which the next starting time should be calculated.
     * @return int of the next starting time for the given processor.
     */
    public int getNextStartTime(int processor);


    /**
     * Retrieves all processors in the state object.
     * @return A Set object of Integers representing all processors in the state object.
     */
    public Set<Integer> procKeys();


    /**
     * Returns the schedule in a processor. The keys represent the start time of corresponding Node values.
     * @param processor The processor to retrieve the schedule from of the state.
     * @return A HashMap object representing the schedule.
     */
    public HashMap<Integer,Node> getSchedule(int processor);


    /**
     * This method gets the _state attribute.
     * @return _state attribute
     */
    public HashMap<Integer, HashMap<Integer, Node>> getState();


    /**
     * This method returns the number of processors.
     * @return _state attribute
     */
    public int getNumProcessors();

}
