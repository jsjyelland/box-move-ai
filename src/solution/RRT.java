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
     * The goal box
     */
    private MoveableBox goalBox;

    /**
     * The initial box
     */
    private MoveableBox initialBox;

    /**
     * Visualiser to visualise the tree
     */
    private Visualiser visualiser;

    /**
     * The tree of states
     */
    private TreeNode<State, Action> tree;

    /**
     * List of all the nodes
     */
    private ArrayList<TreeNode<State, Action>> nodes;

    /**
     * Construct an RRT
     * @param staticObstacles the static obstacles
     * @param initialBox the initial box
     * @param goalBox the goal box
     */
    public RRT(Box[] staticObstacles, MoveableBox[] moveableObstacles, MoveableBox initialBox, MoveableBox goalBox)
            throws InvalidStateException {
        this.goalBox = goalBox;
        this.initialBox = initialBox;

        // Create a visualiser for the tree
        visualiser = new Visualiser(staticObstacles);
        Window window = new Window(visualiser);

        // Make an initial tree
        tree = new TreeNode<>(new State(initialBox, staticObstacles, moveableObstacles), null);

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
                        new MoveableBox(randX, randY, goalBox.getRect().getWidth()), null, null);

                // Get the nearest node to the new one
                TreeNode<State, Action> node = nearestNode(newRandomState);

                newRandomState.setStaticObstacles(node.getState().getStaticObstacles());
                newRandomState.setMoveableObstacles(node.getState().getMoveableObstacles());

                // Make sure this is valid
                newRandomState.validate();

                // Step towards the new random state up to MAX_DISTANCE
                State newState = node.getState().stepTowards(newRandomState, MAX_DISTANCE);

                // Make sure this is valid still
                newState.validate();

                // Add the new state to the tree
                TreeNode<State, Action> newNode = connectNodeToState(node, newState);

                // Try connecting this new state to the goal
                try {
                    TreeNode<State, Action> goalNode = connectNodeToState(newNode, new State(
                            goalBox,
                            newNode.getState().getStaticObstacles(),
                            newNode.getState().getMoveableObstacles()
                    ));

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
    private TreeNode<State, Action> nearestNode(State state) {
        TreeNode<State, Action> bestNode = nodes.get(0);
        double shortestDistance = state.distanceTo(bestNode.getState());

        for (TreeNode<State, Action> node : nodes) {
            double distance = node.getState().distanceTo(state);

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
    private TreeNode<State, Action> connectNodeToState(TreeNode<State, Action> node, State state) throws InvalidStateException {
        double nodeX = node.getState().getMainBox().getRect().getX();
        double nodeY = node.getState().getMainBox().getRect().getY();
        double stateX = state.getMainBox().getRect().getX();
        double stateY = state.getMainBox().getRect().getY();

        double dx = stateX - nodeX;
        double dy = stateY - nodeY;

        if (!(dx == 0 && dy == 0)) {
            if (dx == 0 || dy == 0) {
                // Only requires one movement to get to child

                // Check if the action is valid. Will throw an
                // InvalidStateException if not.
                node.getState().action(dx, dy, node.getState().getStaticObstacles());

                TreeNode<State, Action> newNode = new TreeNode<>(state, null);
                addChildNode(node, newNode);

                return newNode;
            } else {
                // Requires two movements to get to child. Call connectNodes
                // again with the parent being a new node in between the current
                // parent and child. Try both routes.

                try {
                    // First attempt. Corner node with (stateX, nodeY).
                    TreeNode<State, Action> newNode = new TreeNode<>(
                            new State(
                                    new MoveableBox(stateX, nodeY, state.getMainBox().getRect().getWidth()),
                                    node.getState().getStaticObstacles(),
                                    node.getState().getMoveableObstacles()
                            ), null);

                    // Check if the line to this corner is valid
                    node.getState().action(dx, 0, node.getState().getStaticObstacles());

                    // Now connect this corner node to the state.
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<State, Action> endNode = connectNodeToState(newNode, state);

                    // Add the corner node as a child of the parent node
                    addChildNode(node, newNode);

                    return endNode;
                } catch (InvalidStateException e) {
                    // Second attempt. Corner node with (nodeX, stateY).
                    TreeNode<State, Action> newNode = new TreeNode<>(
                            new State(
                                    new MoveableBox(nodeX, stateY, state.getMainBox().getRect().getWidth()),
                                    node.getState().getStaticObstacles(),
                                    node.getState().getMoveableObstacles()
                            ), null);

                    // Check if the line to this corner is valid
                    node.getState().action(0, dy, node.getState().getStaticObstacles());

                    // Now connect this corner node to the state.
                    // Will throw an InvalidStateException if it fails.
                    // If this fails, the function will throw
                    TreeNode<State, Action> endNode = connectNodeToState(newNode, state);

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
    private void addChildNode(TreeNode<State, Action> parent, TreeNode<State, Action> child) {
        parent.addChild(child);
        nodes.add(child);
    }
}
