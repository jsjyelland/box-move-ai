package solution;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.random;
import static solution.Util.randomTo;

public class RRT {
    /**
     * Max distance a node can randomly expand doing RRT
     */
    private static double MAX_DISTANCE = 0.6;

    /**
     * List of static obstacles
     */
    private Box[] staticObstacles;

    /**
     * The goal state
     */
    private State goalState;

    /**
     * The initial state
     */
    private State initialState;

    /**
     * Visualiser to visualise the tree
     */
    private Visualiser visualiser;

    /**
     * The tree of states
     */
    private TreeNode<State> tree;

    /**
     * List of all the nodes
     */
    private ArrayList<TreeNode<State>> nodes;

    /**
     * Construct an RRT
     * @param staticObstacles the static obstacles
     * @param goalState the goal state
     * @param initialState the initial state
     */
    public RRT(Box[] staticObstacles, State goalState, State initialState) {
        this.staticObstacles = staticObstacles;
        this.goalState = goalState;
        this.initialState = initialState;

        // Create a visualiser for the tree
        visualiser = new Visualiser(staticObstacles);
        Window window = new Window(visualiser);

        // Make an initial tree
        tree = new TreeNode<>(initialState);

        // List of all the nodes
        nodes = new ArrayList<>();
        nodes.add(tree);
    }

    /**
     * Expand the tree one step
     * @return if a solution is found or not
     */
    public boolean expand() {
        // Pick a random node to expand
        int randIndex = randomTo(nodes.size() - 1);
        TreeNode<State> randomNode = nodes.get(randIndex);
        State state = randomNode.getValue();

        // Find an action that is valid
        State newState;
        while (true) {
            try {
                // Move in a random direction with a random length
                newState = state.action(
                        MoveDirection.values()[randomTo(MoveDirection.values().length - 1)],
                        random() * MAX_DISTANCE,
                        staticObstacles);
                break;
            } catch (InvalidStateException e) {
                // If this happens, try again. Means the state is in collision
            }
        }

        // Add the new state to the tree
        TreeNode<State> newNode = new TreeNode<>(newState);
        randomNode.addChild(newNode);
        nodes.add(newNode);

        // Visualise the new tree
        visualiser.paintTree(tree);

        // Wait because this is too fast to watch
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        // Temporarily. Will return true when a solution is found
        return false;
    }
}
