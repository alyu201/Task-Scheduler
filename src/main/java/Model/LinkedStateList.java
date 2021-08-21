package Model;

import java.util.LinkedList;

public class LinkedStateList {
    private LinkedList<State> _LinkedList;

    public LinkedStateList(){
        _LinkedList = new LinkedList<State>();
    }

    public void add(State state){
        _LinkedList.add(state);
    }

    //need TESTING
    public State getLowest(){
        State lowestEstimateState = null;
        if (!_LinkedList.isEmpty()) {
            lowestEstimateState = _LinkedList.get(0);
        }
        for (State state : _LinkedList){
            if (state.getUnderestimate() < lowestEstimateState.getUnderestimate()){
                lowestEstimateState = state;
            }
        }
        _LinkedList.remove(lowestEstimateState);
        return lowestEstimateState;
    }

    public void remove(State state){
        _LinkedList.remove(state);
    }

    public Boolean stillAvailable(){
        return !_LinkedList.isEmpty();
    }
}
