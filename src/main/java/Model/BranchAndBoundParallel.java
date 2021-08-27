package Model;

import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

/**
 * This class is responsible for recursively compute the final goal state.
 * This class is called from the BranchAndBoundScheduler class.
 * @Author Kelvin
 */
public class BranchAndBoundParallel extends RecursiveAction {
    private BranchAndBoundScheduler _branchAndBoundScheduler;
    private State _currentState;

    public BranchAndBoundParallel(BranchAndBoundScheduler branchAndBoundScheduler, State state){
        _branchAndBoundScheduler = branchAndBoundScheduler;
        _currentState = state;

    }

    /**
     * This method computes the for the goal state. it will recursively fork and join threads to do the computations in parallel.
     */
    @Override
    protected void compute() {
        _branchAndBoundScheduler.exploreState(_currentState);
    }
}
