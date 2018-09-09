package solution;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.random;

/**
 * A rapidly exploring random tree
 */
public class RRT {
    /**
     * Max distance a node can randomly expand doing RRT
     */
    private static double MAX_DISTANCE = 0.5;

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
        // Sample a random node in free space
        while (true) {
            try {
                double randX = random();
                double randY = random();

                State newRandomState = new State(
                        new MoveableBox(randX, randY, goalState.goalBox.getRect().getWidth()),
                        staticObstacles);

                // Get the nearest node to the new one
                TreeNode<State> node = nearestNode(newRandomState);

                // Step towards the new random state up to MAX_DISTANCE
                State newState = node.getValue().stepTowards(newRandomState, MAX_DISTANCE);

                // Make sure this is valid still
                if (!newState.isValid(staticObstacles)) {
                    throw new InvalidStateException();
                }

                // Add the new state to the tree
                TreeNode<State> newNode = connectNodeToState(node, newState);

                // Try connecting this new state to the goal
                try {
                    TreeNode<State> goalNode = connectNodeToState(newNode, goalState);
                    visualiser.paintSolution(goalNode);
                    visualiser.paintTree(tree);

                    return true;
                } catch (InvalidStateException e) {
                    // Couldn't connect to the goal. Exit the loop
                    break;
                }
            } catch (InvalidStateException e) {
                // If this happens, try again. Means the new state is in collision
            }
        }

        // Visualise the new tree
        visualiser.paintTree(tree);

        // Wait because this is too fast to watch
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        // No solution was found
        return false;
    }

    /**
     * Find the nearest node in the tree to a given state. Uses straight line distance
     * @param state the state to find the node nearest to
     * @return the nearest node
     */
    private TreeNode<State> nearestNode(State state) {
        TreeNode<State> bestNode = nodes.get(0);
        double shortestDistance = state.distanceTo(bestNode.getValue());

        for (TreeNode<State> node : nodes) {
            double distance = node.getValue().distanceTo(state);

            if (distance < shortestDistance) {
                shortestDistance = distance;
                bestNode = node;
            }
        }

        return bestNode;
    }

    /**
     * Attempt to connect two nodes using only horizontal or vertical lines.
     * Will add child to a node and add that as a child of parent
     * Will only attempt two movements (e.g. up then right).
     * @param node the parent node
     * @param state the child state
     * @throws InvalidStateException if there is no connection
     */
    private TreeNode<State> connectNodeToState(TreeNode<State> node, State state) throws InvalidStateException {
        double nodeX = node.getValue().goalBox.getRect().getX();
        double nodeY = node.getValue().goalBox.getRect().getY();
        double stateX = state.goalBox.getRect().getX();
        double stateY = state.goalBox.getRect().getY();

        double dx = stateX - nodeX;
        double dy = stateY - nodeY;

        if (!(dx == 0 && dy == 0)) {
            if (dx == 0 || dy == 0) {
                // Only requires one movement to get to child

                // Check if the action is valid. Will throw an
                // InvalidStateException if not.
                node.getValue().action(dx, dy, staticObstacles);

                TreeNode<State> newNode = new TreeNode<>(state);
                addChildNode(node, newNode);

                return newNode;
            } else {
                // Requires two movements to get to child. Call connectNodes
                // again with the parent being a new node in between the current
                // parent and child. Try both routes.

                try {
                    // First attempt. Corner node with (stateX, nodeY).
                    TreeNode<State> newNode = new TreeNode<>(
                            new State(stateX, nodeY, state.goalBox.getRect().getWidth()));

                    // Check if the line to this corner is valid
                    node.getValue().action(dx, 0, staticObstacles);

                    // Now connect this corner node to the state.
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<State> endNode = connectNodeToState(newNode, state);

                    // Add the corner node as a child of the parent node
                    addChildNode(node, newNode);

                    return endNode;
                } catch (InvalidStateException e) {
                    // Second attempt. Corner node with (nodeX, stateY).
                    TreeNode<State> newNode = new TreeNode<>(
                            new State(nodeX, stateY, state.goalBox.getRect().getWidth()));

                    // Check if the line to this corner is valid
                    node.getValue().action(0, dy, staticObstacles);

                    // Now connect this corner node to the state.
                    // Will throw an InvalidStateException if it fails.
                    // If this fails, the function will throw
                    TreeNode<State> endNode = connectNodeToState(newNode, state);

                    // Add the corner node as a child of the parent node
                    addChildNode(node, newNode);

                    return endNode;
                }
            }
        }

        // They're already in the same place. Just return the node
        return node;
    }

    /**
     * Add a child node to a parent, and also add to the list of nodes.
     * @param parent the parent node
     * @param child the child node
     */
    private void addChildNode(TreeNode<State> parent, TreeNode<State> child) {
        parent.addChild(child);
        nodes.add(child);
    }
}
