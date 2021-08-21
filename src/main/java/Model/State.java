package Model;
import org.graphstream.graph.Node;

import java.util.HashMap;
import java.util.List;
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
     * Gets the underestimate of this state
     * @return the underestimate
     */
    public int getUnderestimate();


    /**
     * Gets the next starting time of a given processor.
     * @param processor The processor number to calculate its next start time.
     * @return int the next start time of the given processor.
     */
    public int getNextStartTime(int processor);


    /**
     * Gets all processors' numbers in the state object.
     * @return A Set object of Integers representing all the processors' number in the state.
     */
    public Set<Integer> procKeys();


    /**
     * Gets the schedule of a particular processor.
     * @param processor The processor number that we want to retrieve its schedule from.
     * @return A HashMap object which represents the schedule.
     */
    public HashMap<Integer,Node> getSchedule(int processor);

    /**
     * Gets all the schedules of the state.
     * @return A List object containing all the schedules in the state.
     */
    public List<HashMap<Integer,Node>> getAllSchedules();


    /**
     * Getting the actual _state.
     * @return _state
     */
    public HashMap<Integer, HashMap<Integer, Node>> getState();


    /**
     * Get the number of processors that this state has.
     * @return int representing the number of processors this state has.
     */
    public int getNumProcessors();

    @Override
    public boolean equals(Object o);

    @Override
    public int hashCode();

}
