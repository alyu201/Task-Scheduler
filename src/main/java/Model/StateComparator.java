package Model;

import java.util.Comparator;

public class StateComparator implements Comparator<State> {
    @Override
    public int compare(State state1, State state2) {
        return state1.getUnderestimate() - state2.getUnderestimate();
    }
}
