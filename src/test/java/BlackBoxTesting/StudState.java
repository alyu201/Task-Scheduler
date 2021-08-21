package BlackBoxTesting;

import Model.State;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * This is a stud of the ModelState object.
 * This StudState will be used for testing purposes to test out these things:
 *      * The A* algorithm implementation
 * @author Megan Lim and AMy Lyu
 */
public class StudState implements State {

    private HashMap<Integer,HashMap<Integer, Node>> _state;
    private int _underestimate = 0;
    private int _nextStartTime;
    private final int _numProcessors;

    public StudState(HashMap<Integer,HashMap<Integer, Node>> stateWithAddedTask, int maxUnderestimate, int nextStartTime, int numProcessors) {
        _state = stateWithAddedTask;
        _underestimate = maxUnderestimate;
        _nextStartTime = nextStartTime;
        _numProcessors = numProcessors;
    }

    /**
     * Gets the underestimate of this state
     * @return the underestimate
     */
    @Override
    public int getUnderestimate() {
        return _underestimate;
    }

    /**
     * This gets the earliest next starting time of the whole state
     * @param processor The processor number
     * @return int: The earliest next starting time of the whole state.
     */
    @Override
    public int getNextStartTime(int processor) {
        return _nextStartTime;
    }

    /**
     * Gets all processors' numbers in the state object.
     * @return A Set object of Integers representing all the processors' number in the state.
     */
    @Override
    public Set<Integer> procKeys() {
        return _state.keySet();
    }

    /**
     * Gets the schedule of a particular processor.
     * @param processor The processor number that we want to retrieve its schedule from.
     * @return A HashMap object which represents the schedule.
     */
    @Override
    public HashMap<Integer, Node> getSchedule(int processor) {
        return _state.get(processor);
    }

    /**
     * Returns all the schedules in a state object.
     * @return A List object containing all processor schedules.
     * @author: Amy Lyu
     */
    @Override
    public List<HashMap<Integer, Node>> getAllSchedules() {
        List<HashMap<Integer,Node>> allSchedules = new ArrayList<>();
        for (int processor: _state.keySet()) {
            HashMap<Integer, Node> schedule = _state.get(processor);
            allSchedules.add(schedule);
        }
        return allSchedules;
    }

    /**
     * Getting the actual _state.
     * @return _state
     */
    @Override
    public HashMap<Integer, HashMap<Integer, Node>> getState() {
        return _state;
    }

    /**
     * Get the number of processors that this state has.
     * @return int representing the number of processors this state has.
     */
    @Override
    public int getNumProcessors() {
        return _numProcessors;
    }

}
