package BlackBoxTesting;

import Model.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class AlgorithmUnitTest {

    // Graph to use in tests
    private Graph _graph = new DefaultGraph("graph");

    // Num of processors
    private int _numOfProcessors = 2;

    // Scheduler to test
    private SchedulerPrivateData _scheduler;

    // List of states
    private List<State> _listOfStates = new ArrayList<>();

    static class SchedulerPrivateData {
        private Graph _taskGraph;
        private int _numProcessors;
        public PriorityQueue<State> _openList;
        private ExecutorService _executorService;
        private Set<State> _closedList;

        /**
         * This variable keeps track of the dummy root so that it can only be scheduled once
         */
        private boolean _dummyRootScheduled;


        //TODO: Update with the actual classes
        public SchedulerPrivateData (Graph taskGraph, int numProcessors) {
            _numProcessors = numProcessors;
            _openList = new PriorityQueue<State>(200, new StateComparator());
            _closedList = new HashSet<State>();
            _taskGraph = taskGraph;
            _dummyRootScheduled = false;
            _executorService = Executors.newFixedThreadPool(Main.NUMPROCESSORS);

        }

        /**
         * Create an optimal schedule using A* algorithm
         * @return The state of the processors with schedules
         */
        public State generateSchedule() throws ExecutionException, InterruptedException {

            State emptyState = new ModelState(_numProcessors);
            _openList.add(emptyState);
            int i = 1;
            int freq = (int) (_taskGraph.nodes().count()) * _numProcessors;

            TaskGraphUtil.removeDummyRootNode(_taskGraph);

            while (!_openList.isEmpty()) {
                State state = _openList.poll();

                // Update GUI at a frequency of 1/(numOfTasks*numProc) whenever a state is popped off openList
            if (i % freq == 0) { Visualiser.update(state); }
                i++;

                if (goalStateReached(state)) {
                    _executorService.shutdown();
                    // Call Visualiser to update GUI
                Visualiser.update(state);
                    return state;
                }

                List<Node> schedulableTasks = getNextTasks(state);
                addChildStates(state, schedulableTasks);
                _closedList.add(state);
            }
            _executorService.shutdown();
            return null;
        }

        /**
         * This method determines if a state has reached the goal
         * @param state The state to be checked
         * @return True if is goal state, false otherwise
         */
        public boolean goalStateReached(State state) {
            List<HashMap<Integer, Node>> schedules = state.getAllSchedules();

            List<Node> scheduledTasks = new ArrayList<Node>();

            for (HashMap<Integer, Node> i: schedules) {
                scheduledTasks.addAll(i.values());
            }

            return TaskGraphUtil.allTaskScheduled(_taskGraph, scheduledTasks);
        }

        /**
         * Create a set of child state from a parent state.
         * This method uses the ExecutorService to add the child state in parallel.
         * @param parentState The parent state
         * @param tasks The list of tasks that are to be scheduled in tathe child states.
         */
        public void addChildStates (State parentState, List<Node> tasks) throws ExecutionException, InterruptedException {
            Set<Future> futures = new HashSet<>();

            //for each task, add it to the openlist on a different thread
            for (Node task: tasks) {
                StateAdditionThread stateAdditionThread = new StateAdditionThread(parentState, task, _openList, _closedList);
                futures.add(_executorService.submit(stateAdditionThread));
            }

            //Wait for all the child thread to return
            for (Future future:futures){
                future.get();
            }
        }

        /**
         * Generate a list of schedulable tasks (nodes) for a state
         * @param state The state to be evaluated
         * @return A list of schedulable tasks (nodes)
         */
        public List<Node> getNextTasks(State state) {
            List<HashMap<Integer, Node>> schedules = state.getAllSchedules();

            List<Node> scheduledTasks = new ArrayList<Node>();

            for (HashMap<Integer, Node> i: schedules) {
                scheduledTasks.addAll(i.values());
            }

            List<Node> schedulableTasks = TaskGraphUtil.getNextSchedulableTasks(_taskGraph, scheduledTasks);

            return schedulableTasks;
        }

    }


    /*
        Before each test method, instantiate a new scheduler.
     */
    @BeforeEach
    private void instantiateScheduler() {

        // Clearing the _listOfStates list
        _listOfStates.clear();

        // Clearing the graph
        _graph = new DefaultGraph("graph");

        // Setting the up the graph
        _graph.addNode("A");
        _graph.addNode("B");
        _graph.addNode("C");
        _graph.addNode("D");

        Node nodeA = _graph.getNode("A");
        Node nodeB = _graph.getNode("B");
        Node nodeC = _graph.getNode("C");
        Node nodeD = _graph.getNode("D");

        nodeA.setAttribute("Weight", 2);
        nodeB.setAttribute("Weight", 2);
        nodeC.setAttribute("Weight", 3);
        nodeD.setAttribute("Weight", 2);

        Edge edgeAtoB = _graph.addEdge("A -> B", nodeA, nodeB);
        Edge edgeAtoC = _graph.addEdge("A -> C", nodeA, nodeC);
        Edge edgeBtoD = _graph.addEdge("B -> D", nodeB, nodeD);
        Edge edgeCtoD = _graph.addEdge("C -> D", nodeC, nodeD);

        edgeAtoB.setAttribute("Weight", 2);
        edgeAtoC.setAttribute("Weight", 1);
        edgeBtoD.setAttribute("Weight", 2);
        edgeCtoD.setAttribute("Weight", 1);

        // Instantiate scheduler
        _scheduler = new SchedulerPrivateData(_graph, _numOfProcessors);

        //These are the processors
        Integer proc1 = new Integer(1);
        Integer proc2 = new Integer(2);

        //These are the times
        Integer time0 = new Integer(0);
        Integer time2 = new Integer(2);
        Integer time3 = new Integer(3);
        Integer time4 = new Integer(4);
        Integer time5 = new Integer(5);
        Integer time6 = new Integer(6);
        Integer time7 = new Integer(7);
        Integer time8 = new Integer(8);
        Integer time9 = new Integer(9);
        Integer time10 = new Integer(10);


        // Creating State 0
        HashMap<Integer, HashMap<Integer, Node>> _emptyState0 = new HashMap<>();
        HashMap<Integer, Node> s0Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s0Proc2Schedule = new HashMap<>();
        _emptyState0.put(proc1, s0Proc1Schedule);
        _emptyState0.put(proc2, s0Proc2Schedule);
        int maxUnderestimate0 = 0; //Take the underestimate of schedule 2
        int nextStartTime0 = 0;
        State state0 = new StudState(_emptyState0, maxUnderestimate0, nextStartTime0, _numOfProcessors);
        _listOfStates.add(state0);

        // Creating State 1
        HashMap<Integer, HashMap<Integer, Node>> _emptyState1 = new HashMap<>();
        HashMap<Integer, Node> s1Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s1Proc2Schedule = new HashMap<>();
        s1Proc1Schedule.put(time0, nodeA);
        _emptyState1.put(proc1, s1Proc1Schedule);
        _emptyState1.put(proc2, s1Proc2Schedule);
        int maxUnderestimate1 = 9; //Take the underestimate of schedule 2
        int nextStartTime1 = 0;
        State state1 = new StudState(_emptyState1, maxUnderestimate1, nextStartTime1, _numOfProcessors);
        _listOfStates.add(state1);

        // Creating State 2
        HashMap<Integer, HashMap<Integer, Node>> _emptyState2 = new HashMap<>();
        HashMap<Integer, Node> s2Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s2Proc2Schedule = new HashMap<>();
        s2Proc1Schedule.put(time0, nodeA);
        s2Proc1Schedule.put(time2, nodeB);
        _emptyState2.put(proc1, s2Proc1Schedule);
        _emptyState2.put(proc2, s2Proc2Schedule);
        int maxUnderestimate2 = 9; //Take the underestimate of schedule 2
        int nextStartTime2 = 2;
        State state2 = new StudState(_emptyState2, maxUnderestimate2, nextStartTime2, _numOfProcessors);
        _listOfStates.add(state2);


        // Creating State 3
        HashMap<Integer, HashMap<Integer, Node>> _emptyState3 = new HashMap<>();
        HashMap<Integer, Node> s3Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s3Proc2Schedule = new HashMap<>();
        s3Proc1Schedule.put(time0, nodeA);
        s3Proc2Schedule.put(time4, nodeB);
        _emptyState3.put(proc1, s3Proc1Schedule);
        _emptyState3.put(proc2, s3Proc2Schedule);
        int maxUnderestimate3 = 10; //Take the underestimate of schedule 2
        int nextStartTime3 = 4;
        State state3 = new StudState(_emptyState3, maxUnderestimate3, nextStartTime3, _numOfProcessors);
        _listOfStates.add(state3);


        // Creating State 4
        HashMap<Integer, HashMap<Integer, Node>> _emptyState4 = new HashMap<>();
        HashMap<Integer, Node> s4Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s4Proc2Schedule = new HashMap<>();
        s4Proc1Schedule.put(time0, nodeA);
        s4Proc1Schedule.put(time2, nodeC);
        _emptyState4.put(proc1, s4Proc1Schedule);
        _emptyState4.put(proc2, s4Proc2Schedule);
        int maxUnderestimate4 = 10; //Take the underestimate of schedule 2
        int nextStartTime4 = 2;
        State state4 = new StudState(_emptyState4, maxUnderestimate4, nextStartTime4, _numOfProcessors);
        _listOfStates.add(state4);

        // Creating State 5
        HashMap<Integer, HashMap<Integer, Node>> _emptyState5 = new HashMap<>();
        HashMap<Integer, Node> s5Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s5Proc2Schedule = new HashMap<>();
        s5Proc1Schedule.put(time0, nodeA);
        s5Proc2Schedule.put(time3, nodeC);
        _emptyState5.put(proc1, s5Proc1Schedule);
        _emptyState5.put(proc2, s5Proc2Schedule);
        int maxUnderestimate5 = 11; //Take the underestimate of schedule 2
        int nextStartTime5 = 3;
        State state5 = new StudState(_emptyState5, maxUnderestimate5, nextStartTime5, _numOfProcessors);
        _listOfStates.add(state5);

        // Creating State 6
        HashMap<Integer, HashMap<Integer, Node>> _emptyState6 = new HashMap<>();
        HashMap<Integer, Node> s6Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s6Proc2Schedule = new HashMap<>();
        s6Proc1Schedule.put(time0, nodeA);
        s6Proc1Schedule.put(time2, nodeB);
        s6Proc1Schedule.put(time4, nodeC);
        _emptyState6.put(proc1, s6Proc1Schedule);
        _emptyState6.put(proc2, s6Proc2Schedule);
        int maxUnderestimate6 = 12; //Take the underestimate of schedule 2
        int nextStartTime6 = 4;
        State state6 = new StudState(_emptyState6, maxUnderestimate6, nextStartTime6, _numOfProcessors);
        _listOfStates.add(state6);

        // Creating State 7
        HashMap<Integer, HashMap<Integer, Node>> _emptyState7 = new HashMap<>();
        HashMap<Integer, Node> s7Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s7Proc2Schedule = new HashMap<>();
        s7Proc1Schedule.put(time0, nodeA);
        s7Proc1Schedule.put(time2, nodeB);
        s7Proc2Schedule.put(time3, nodeC);
        _emptyState7.put(proc1, s7Proc1Schedule);
        _emptyState7.put(proc2, s7Proc2Schedule);
        int maxUnderestimate7 = 11; //Take the underestimate of schedule 2
        int nextStartTime7 = 3;
        State state7 = new StudState(_emptyState7, maxUnderestimate7, nextStartTime7, _numOfProcessors);
        _listOfStates.add(state7);


        // Creating State 8
        HashMap<Integer, HashMap<Integer, Node>> _emptyState8 = new HashMap<>();
        HashMap<Integer, Node> s8Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s8Proc2Schedule = new HashMap<>();
        s8Proc1Schedule.put(time0, nodeA);
        s8Proc1Schedule.put(time2, nodeC);
        s8Proc2Schedule.put(time4, nodeB);
        _emptyState8.put(proc1, s8Proc1Schedule);
        _emptyState8.put(proc2, s8Proc2Schedule);
        int maxUnderestimate8 = 11; //Take the underestimate of schedule 2
        int nextStartTime8 = 2;
        State state8 = new StudState(_emptyState8, maxUnderestimate8, nextStartTime8, _numOfProcessors);
        _listOfStates.add(state8);

        // Creating State 9
        HashMap<Integer, HashMap<Integer, Node>> _emptyState9 = new HashMap<>();
        HashMap<Integer, Node> s9Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s9Proc2Schedule = new HashMap<>();
        s9Proc1Schedule.put(time0, nodeA);
        s9Proc2Schedule.put(time4, nodeB);
        s9Proc2Schedule.put(time6, nodeC);
        _emptyState9.put(proc1, s9Proc1Schedule);
        _emptyState9.put(proc2, s9Proc2Schedule);
        int maxUnderestimate9 = 14; //Take the underestimate of schedule 2
        int nextStartTime9 = 6;
        State state9 = new StudState(_emptyState9, maxUnderestimate9, nextStartTime9, _numOfProcessors);
        _listOfStates.add(state9);

        // Creating State 10
        HashMap<Integer, HashMap<Integer, Node>> _emptyState10 = new HashMap<>();
        HashMap<Integer, Node> s10Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s10Proc2Schedule = new HashMap<>();
        s10Proc1Schedule.put(time0, nodeA);
        s10Proc1Schedule.put(time2, nodeC);
        s10Proc1Schedule.put(time5, nodeB);
        _emptyState10.put(proc1, s10Proc1Schedule);
        _emptyState10.put(proc2, s10Proc2Schedule);
        int maxUnderestimate10 = 12; //Take the underestimate of schedule 2
        int nextStartTime10 = 5;
        State state10 = new StudState(_emptyState10, maxUnderestimate10, nextStartTime10, _numOfProcessors);
        _listOfStates.add(state10);

        // Creating State 11
        HashMap<Integer, HashMap<Integer, Node>> _emptyState11 = new HashMap<>();
        HashMap<Integer, Node> s11Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s11Proc2Schedule = new HashMap<>();
        s11Proc1Schedule.put(time0, nodeA);
        s11Proc1Schedule.put(time2, nodeC);
        s11Proc2Schedule.put(time4, nodeB);
        _emptyState11.put(proc1, s11Proc1Schedule);
        _emptyState11.put(proc2, s11Proc2Schedule);
        int maxUnderestimate11 = 10; //Take the underestimate of schedule 2
        int nextStartTime11 = 4;
        State state11 = new StudState(_emptyState11, maxUnderestimate11, nextStartTime11, _numOfProcessors);
        _listOfStates.add(state11);

        // Creating State 12
        HashMap<Integer, HashMap<Integer, Node>> _emptyState12 = new HashMap<>();
        HashMap<Integer, Node> s12Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s12Proc2Schedule = new HashMap<>();
        s12Proc1Schedule.put(time0, nodeA);
        s12Proc1Schedule.put(time2, nodeC);
        s12Proc2Schedule.put(time4, nodeB);
        s12Proc1Schedule.put(time8, nodeD);
        _emptyState12.put(proc1, s12Proc1Schedule);
        _emptyState12.put(proc2, s12Proc2Schedule);
        int maxUnderestimate12 = 12; //Take the underestimate of schedule 2
        int nextStartTime12 = 8;
        State state12 = new StudState(_emptyState12, maxUnderestimate12, nextStartTime12, _numOfProcessors);
        _listOfStates.add(state12);


        // Creating State 13
        HashMap<Integer, HashMap<Integer, Node>> _emptyState13 = new HashMap<>();
        HashMap<Integer, Node> s13Proc1Schedule = new HashMap<>();
        HashMap<Integer, Node> s13Proc2Schedule = new HashMap<>();
        s13Proc1Schedule.put(time0, nodeA);
        s13Proc1Schedule.put(time2, nodeC);
        s13Proc2Schedule.put(time4, nodeB);
        s13Proc2Schedule.put(time6, nodeD);
        _emptyState13.put(proc1, s13Proc1Schedule);
        _emptyState13.put(proc2, s13Proc2Schedule);
        int maxUnderestimate13 = 10; //Take the underestimate of schedule 2
        int nextStartTime13 = 6;
        State state13 = new StudState(_emptyState13, maxUnderestimate13, nextStartTime13, _numOfProcessors);
        _listOfStates.add(state13);

    }

    /**
        This method tests on whether the algorithm can correctly detect that
        we have reached the goal state.
        We have reached the goal state when we have:
            * Scheduled all tasks required
     **/
    @Test
    public void testGoalStateReached(){

        instantiateScheduler();

        // Get a State stud with an actual goal state
        State goalState = _listOfStates.get(13);

        try {
            assertTrue(_scheduler.goalStateReached(goalState));
        }catch (Exception e){
            fail("Goal state not detected.");
        }
    }

    /**
        This method tests on whether the algorithm can correctly detect that
        we have reached the goal state.
        We have reached the goal state when we have:
            * Scheduled all tasks required
     **/
    @Test
    public void testNonGoalStateReached(){

        instantiateScheduler();

        // Get State studs with non-goal states inside them
        State nonGoalState1 = _listOfStates.get(5);
        State nonGoalState2 = _listOfStates.get(8);

        try {
            assertEquals(false, _scheduler.goalStateReached(nonGoalState1));
            assertEquals(false, _scheduler.goalStateReached(nonGoalState2));
        }catch (Exception e){
            fail("False goal state incorrectly detected as goal state.");
        }
    }

    /**
     This method tests on whether the algorithm can correctly list out the
     current schedulable tasks of a given state. This test only expects 1 task to be scheduled next.
     **/
    @Test
    public void testGetNextTasks(){

        instantiateScheduler();

        // Get a state to test
        State stateToTest1 = _listOfStates.get(11);
        Node nodeD = _graph.getNode("D");

        try {
            List<Node> result = _scheduler.getNextTasks(stateToTest1);
            for (Node node : result) {
                assertEquals(node, nodeD);
            }
        }catch (Exception e){
            fail("Unable to correctly list out the 1 schedulable task.");
        }
    }

}
