package Model;

import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.RecursiveAction;

/**
 * This class is responsible for recursively compute the final goal state.
 * This class is called from the BranchAndBoundScheduler class.
 *
 * @Author Kelvin Shen and Sherman Chin
 */
public class BranchAndBoundParallel extends RecursiveAction {
    private BranchAndBoundScheduler _branchAndBoundScheduler;
    private State _currentState;

    public BranchAndBoundParallel(BranchAndBoundScheduler branchAndBoundScheduler, State currenState) {
        _branchAndBoundScheduler = branchAndBoundScheduler;
        _currentState = currenState;

    }

    /**
     * This method computes the for the goal state. it will recursively fork and join threads to do the computations in parallel.
     */
    @Override
    protected void compute() {
        if (Main.VISUALISATIONFLAG){
            Visualiser.update(_currentState);
        }
        // only count number of processors/threads if parallelisation is required
        if (Main.PARALLELISATIONFLAG) {

            Visualiser.incrThreadCount();
        }

        if (_branchAndBoundScheduler.getClosedList().contains(_currentState) || _currentState.getUnderestimate() >= _branchAndBoundScheduler.getUpperBound()) {
            return;
        }

        if (_branchAndBoundScheduler.goalStateReached(_currentState)) {
            _branchAndBoundScheduler.checkAndUpdateUpperBound(_currentState);
        } else {
            List<Node> schedulableTasks = _branchAndBoundScheduler.getNextTasks(_currentState);
            _branchAndBoundScheduler.fixTaskOrder(schedulableTasks, _currentState);
            PriorityQueue<State> childStates = _branchAndBoundScheduler.addChildStates(_currentState, schedulableTasks);

            List<BranchAndBoundParallel> listActions = allocateThreadTask(childStates);

            for (State i: childStates) {
                BranchAndBoundParallel childTask = new BranchAndBoundParallel(_branchAndBoundScheduler, i);
                childTask.compute();
            }

            for (BranchAndBoundParallel childThread : listActions) {
                childThread.join();
            }
        }
        _branchAndBoundScheduler.updateClosedList(_currentState);
    }

    /**
     * Allocate a thread to do a task if a thread is available
     * @param states The state to be expanded in a task
     * @return A list of child thread tasks which can be used for joining to main thread when finished
     */
    private synchronized List<BranchAndBoundParallel> allocateThreadTask (PriorityQueue<State> states) {
        int availableThreads = BranchAndBoundScheduler.Pool.getParallelism() - BranchAndBoundScheduler.Pool.getPoolSize();

        List<BranchAndBoundParallel> listActions = new ArrayList<>();
        for (int i = 0; i < availableThreads; i++) {
            //Remain one task for original thread
            if (states.size() <= 1) {
                break;
            }
            BranchAndBoundParallel task = new BranchAndBoundParallel(_branchAndBoundScheduler, states.poll());
            task.fork();
            listActions.add(task);
        }

        return listActions;
    }
}
