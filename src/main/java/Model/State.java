package Model;

import java.util.HashMap;

import org.graphstream.graph.Node;

/**
 State class represents a state produced at a particular time in the task scheduling process. This object stores the
 maximum underestimate and methods for retrieving the state information.
 @author alyu
 */

public class State {
    private HashMap<Integer,HashMap<Integer, Node>> _state = new HashMap<Integer,HashMap<Integer, Node>>();
    private int _underestimate = 0;

    public State(State parentState, int MaxUnderestimate, Node childTask, int processor, int startTime) {
        HashMap<Integer,Node> newSchedule = new HashMap<Integer,Node>();
        newSchedule.put(startTime, childTask);

        // Deep copy of parentState - uses custom clone method
        if (parentState != null) {
            _state = clone(parentState);

            if (_state.get(processor) == null) {
                _state.put(processor, newSchedule);
            }
            _state.get(processor).put(startTime, childTask);
        } else {
            _state.put(processor,newSchedule);
        }

        // Assigns underestimate score for state
        _underestimate = MaxUnderestimate;
    }

    /**
     * Clones parent state for creating child state.
     * @return HashMap representing the clone of the given State object.
     */
    public static HashMap<Integer,HashMap<Integer,Node>> clone(State st) {
        HashMap<Integer,HashMap<Integer,Node>> clone = new HashMap<Integer,HashMap<Integer,Node>>();
        HashMap<Integer,HashMap<Integer,Node>> state = st.getState();
        for (int proc : state.keySet()) {
            for (int time : state.get(proc).keySet()) {
                Node task = state.get(proc).get(time);
                HashMap<Integer,Node> schedule = new HashMap<Integer,Node>();
                schedule.put(time,task);
                clone.put(proc,schedule);
            }
        }
        return clone;
    }

    /**
     * Retrieves the state object as a HashMap.
     * @return HashMap type of state object.
     */
    public HashMap<Integer,HashMap<Integer,Node>> getState() {
        return _state;
    }

    /**
     * Retrieves the maximum underestimate of the state.
     * @return int of the maximum underestimate of the state object.
     */
    public int getUnderestimate() {
        return _underestimate;
    }

    /**
     * Retrieve the next starting time for a given processor.
     * @return int of the next starting time for the given processor.
     */
    public int getNextStartTime(int processor) {
        HashMap<Integer,Node> procSchedule = _state.get(processor);
        int nextStartTime = (int)procSchedule.keySet().toArray()[0];

        for (int key : procSchedule.keySet()) {
            Node node = procSchedule.get(key);
            int weight = (int) node.getAttribute("weight");
            nextStartTime += weight;
        }
        return nextStartTime;
    }

    /**
     * Returns string format of state object.
     * @return String form of State object.
     */
    public String toString() {
        return _state.toString();
    }
}
