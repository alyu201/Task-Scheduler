package Model;

import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.List;
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
        int upperBound = _branchAndBoundScheduler.getUpperBound();
        if (_branchAndBoundScheduler.goalStateReached(_currentState)) {
            if (_currentState.getUnderestimate() < upperBound) {

                _branchAndBoundScheduler.updateUpperBound(_currentState.getUnderestimate());
                _branchAndBoundScheduler.updateCompleteState(_currentState);
            }
        } else {
            if (_currentState.getUnderestimate() <= upperBound) {
                List<Node> schedulableTasks = _branchAndBoundScheduler.getNextTasks(_currentState);
                List<State> childStates = _branchAndBoundScheduler.addChildStates(_currentState, schedulableTasks);


                List<BranchAndBoundParallel> listActions = new ArrayList<>();
                for (State childState : childStates) {
                    BranchAndBoundParallel child = new BranchAndBoundParallel(_branchAndBoundScheduler, childState);
                    listActions.add(child);
                    child.fork();
                }

                for (BranchAndBoundParallel childThread : listActions){
                    childThread.join();
                }

            }
        }
    }
}
