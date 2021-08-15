package BlackBoxTesting;

import Model.State;
import org.graphstream.graph.Node;

import java.util.HashMap;
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

    private HashMap<Integer,HashMap<Integer, Node>> _state = new HashMap<>();
    private int _underestimate = 0;
    private int _nextStartTime = 0;
    private Set<Integer> _procKeys = null;
    private final int _numProcessors;

    public StudState(HashMap<Integer,HashMap<Integer, Node>> state, int underestimate, int nextStartTime, Set<Integer> procKeys, int numProcessors) {
        _state = state;
        _underestimate = underestimate;
        _nextStartTime = nextStartTime;
        _procKeys = procKeys;
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
        return _procKeys;
    }

    @Override
    public HashMap<Integer, Node> getSchedule(int processor) {
        return _state.get(processor);
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
