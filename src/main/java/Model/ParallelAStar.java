package Model;

import org.graphstream.graph.Node;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.RecursiveTask;

public class ParallelAStar implements Callable {
    private PriorityQueue<State> _openList;
    private State _currentParentState;
    private Node _currentTask;
    private AStarScheduler _aStar;

    public ParallelAStar(AStarScheduler aStar, State state){
        _aStar = aStar;
        _currentParentState = state;
    }

    @Override
    public Object call() throws Exception {
        State state = _currentParentState;

        // Update GUI at a frequency of 1/(numOfTasks*numProc) whenever a state is popped off openList
//        if (Main.VISUALISATIONFLAG && i % freq == 0) {
//            Visualiser.update(state);
//        }
//        i++;

        if (_aStar.goalStateReached(state)) {
            // Call Visualiser to update GUI
            if (Main.VISUALISATIONFLAG) {
                Visualiser.update(state);
            }
            return state;
        }

        List<Node> schedulableTasks = _aStar.getNextTasks(state);
        _aStar.addChildStates(state, schedulableTasks);
        return null;
    }
}
