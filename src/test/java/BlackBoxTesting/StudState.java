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
 *      * The ModelState's constructor that takes a parentState as a parameter
 *      * The ModelState's clone method
 * @author Megan Lim
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

    @Override
    public int getUnderestimate() {
        return _underestimate;
    }

    @Override
    public int getNextStartTime(int processor) {
        return _nextStartTime;
    }

    @Override
    public Set<Integer> procKeys() {
        return _state.keySet();
    }

    @Override
    public HashMap<Integer, Node> getSchedule(int processor) {
        return _state.get(processor);
    }

    @Override
    public List<HashMap<Integer, Node>> getAllSchedules() {
        List<HashMap<Integer,Node>> allSchedules = new ArrayList<>();
        for (int processor: _state.keySet()) {
            HashMap<Integer, Node> schedule = _state.get(processor);
            allSchedules.add(schedule);
        }
        return allSchedules;
    }

    @Override
    public HashMap<Integer, HashMap<Integer, Node>> getState() {
        return _state;
    }

    @Override
    public int getNumProcessors() {
        return _numProcessors;
    }

}
