package Model;

import java.util.*;

import org.graphstream.graph.Node;

/**
 * This class represents a state produced at a particular time
 * in the task scheduling process. This object stores the maximum
 * underestimate and methods for retrieving the state information.
 * @author Amy Lyu
 */

public class State {
    private HashMap<Integer,HashMap<Integer, Node>> _state = new HashMap<>();
    private int _underestimate = 0;
    private final int _numProcessors;
    private int _totalIdleTime = 0;

    /**
     * Creates an empty state object.
     * @param numProcessor The number of processors for all subsequent states.
     */
    public State(int numProcessor) {
        _numProcessors = numProcessor;
        initialise(_state);
    }

    /**
     * Constructor for creating a typical state object that accounts for both parent and child state instantiations.
     * @param parentState The parent state to create child state from. This state is deep copied to separate parent and
     *                    child objects.
     * @param MaxUnderestimate The max underestimate associated with the created state.
     * @param childTask The child task represented as a Node object to be scheduled in the created state.
     * @param processor The processor number the child task to be scheduled on.
     * @param startTime The starting executing time for the child task to be scheduled on.
     */
    public State(State parentState, int MaxUnderestimate, Node childTask, int processor, int startTime) {
        HashMap<Integer,Node> newSchedule = new HashMap<>();
        newSchedule.put(startTime, childTask);
        _numProcessors = parentState.getNumProcessors();

        // Deep copy of parentState - uses custom clone method
        _state = clone(parentState);
        _state.putIfAbsent(processor, newSchedule); // Assigns new schedule
        _state.get(processor).put(startTime, childTask);

        // Assigns underestimate score for state
        _underestimate = MaxUnderestimate;
    }

    /**
     * Clones parent state for creating child state.
     * @param state The State object to be cloned.
     * @return HashMap representing the clone of the given State object.
     */
    private HashMap<Integer,HashMap<Integer,Node>> clone(State state) {
        HashMap<Integer,HashMap<Integer,Node>> clone = new HashMap<>();
        initialise(clone);
        for (int proc : state.procKeys()) {
            HashMap<Integer,Node> schedule = new HashMap<>();
            for (int time : state.getSchedule(proc).keySet()) {
                Node task = state.getSchedule(proc).get(time);
                schedule.put(time,task);
                clone.put(proc,schedule);
            }
        }
        return clone;
    }

    /**
     * Initialises empty processor schedules for an empty state.
     * @param state The state object to initialise empty processor schedules for.
     */
    private void initialise(HashMap<Integer, HashMap<Integer, Node>> state) {
        if (_numProcessors == 0) {
            state.put(0, new HashMap<>());
        }
        for (int i = 0; i < _numProcessors; i++) {
            state.put(i+1, new HashMap<>());
        }
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
     * @param processor The processor from which the next starting time should be calculated.
     * @return int of the next starting time for the given processor.
     */
    public int getNextStartTime(int processor) {
        HashMap<Integer,Node> procSchedule = _state.get(processor);
        if (procSchedule.keySet().size() == 0) {
            return 0;
        }

        int latestStartTime = Collections.max(procSchedule.keySet());
        Node latestTask = procSchedule.get(latestStartTime);
        int weight = Double.valueOf(latestTask.getAttribute("Weight").toString()).intValue();
        return latestStartTime + weight;
    }

    /**
     * Returns string format of state object.
     * @return String form of State object.
     */
    public String toString() {
        return _state.toString();
    }

    /**
     * Retrieves all processors in the state object.
     * @return A Set object of Integers representing all processors in the state object.
     */
    public Set<Integer> procKeys() {
        return _state.keySet();
    }

    /**
     * Returns the schedule in a processor. The keys represent the start time of corresponding Node values.
     * @param processor The processor to retrieve the schedule from of the state.
     * @return A HashMap object representing the schedule.
     */
    public HashMap<Integer,Node> getSchedule(int processor) {
        return _state.get(processor);
    }

    /**
     * Returns all the schedules in a state object.
     * @return A List object containing all processor schedules.
     */
    public List<HashMap<Integer,Node>> getAllSchedules() {
        List<HashMap<Integer,Node>> allSchedules = new ArrayList<>();
        for (int processor: _state.keySet()) {
            HashMap<Integer, Node> schedule = _state.get(processor);
            allSchedules.add(schedule);
        }
        return allSchedules;
    }

    /**
     * This method gets the _state attribute.
     * @return _state attribute
     */
    public HashMap<Integer, HashMap<Integer, Node>> getState() {
        return _state;
    }

    /**
     * This method sets _totalIdleTime
     * @param totalIdleTime
     */
    public void setTotalIdleTime(int totalIdleTime){
        _totalIdleTime = totalIdleTime;
    }

    /**
     * This method gets _totalIdleTime
     * @return _totalIdleTime
     */
    public int getTotalIdleTime(){
        return _totalIdleTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return _state.equals(state._state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_state);
    }

    public int getNumProcessors() {
        return _numProcessors;
    }
}
