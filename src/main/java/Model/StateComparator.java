package Model;

import java.util.Comparator;

/**
 * This comparator allows different State to be compared based on their underestimates (cost function)
 * author: Sherman Chin
 */
public class StateComparator implements Comparator<State> {
    @Override
    public int compare(State state1, State state2) {
        return state1.getUnderestimate() - state2.getUnderestimate();
    }
}
