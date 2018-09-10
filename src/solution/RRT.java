package solution;

import java.util.ArrayList;

import static java.lang.Math.random;

/**
 * An abstract rapidly exploring random tree
 */
public abstract class RRT {
    /**
     * Max distance a node can randomly expand doing RRT
     */
    private static double MAX_DISTANCE = 0.5;

    /**
     * The tree of states
     */
    private TreeNode<State, Action> tree;

    /**
     * List of all the nodes
     */
    private ArrayList<TreeNode<State, Action>> nodes;

    /**
     * The solution node for this RRT.
     */
    protected TreeNode<State, Action> solutionNode = null;

    /**
     * The initial box
     */
    private MoveableBox initialBox;

    /**
     * Construct an RRT
     *
     * @param staticObstacles the static obstacles
     * @param initialBox the initial box
     */
    public RRT(ArrayList<Box> staticObstacles, ArrayList<MoveableBox> moveableObstacles, MoveableBox initialBox) {
        this.initialBox = initialBox;

        // Make an initial tree
        tree = new TreeNode<>(new State(initialBox, staticObstacles, moveableObstacles), null);

        // List of all the nodes
        nodes = new ArrayList<>();
        nodes.add(tree);
    }

    /**
     * Expand the tree one step. This function has to be implemented by the subclasses
     *
     * @return if a solution is found or not
     */
    public boolean expand() {
        // Sample a random node in free space
        while (true) {
            try {
                double randX = random();
                double randY = random();

                State newRandomState = new State(
                        new MoveableBox(randX, randY, initialBox.getRect().getWidth()), null, null
                );

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
                TreeNode<State, Action> newNode = connectNodeToState(node, newState, true);

                // Try connecting this new state to the goal
                return checkSolution(newNode);
            } catch (InvalidStateException e) {
                // If this happens, try again. Means the new state is in collision
            }
        }
    }

    /**
     * Check to see if the current tree has a solution
     *
     * @return if a solution is found or not
     */
    protected abstract boolean checkSolution(TreeNode<State, Action> newestNode);

    /**
     * Find the nearest node in the tree to a given state. Uses straight line distance
     *
     * @param state the state to find the node nearest to
     *
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
     * Attempt to connect a node to a state using only horizontal or vertical lines. Will only
     * attempt two movements (e.g. up then right).
     *
     * @param node the parent node
     * @param state the child state
     * @param addChild whether to add the new node to the tree or not
     *
     * @throws InvalidStateException if there is no connection
     */
    protected TreeNode<State, Action> connectNodeToState(TreeNode<State, Action> node, State state,
            boolean addChild) throws InvalidStateException {
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
                TreeNode<State, Action> newNode = node.getState().action(dx, dy, node);

                // Add the new node to the tree
                if (addChild) {
                    addChildNode(node, newNode);
                }

                return newNode;
            } else {
                // Requires two movements to get to child. Call connectNodes
                // again with the parent being a new node in between the current
                // parent and child. Try both routes.

                try {
                    // First attempt. Corner node with (stateX, nodeY).
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<State, Action> cornerNode = connectNodeToState(node, new State(
                            new MoveableBox(stateX, nodeY, state.getMainBox().getRect().getWidth()),
                            node.getState().getStaticObstacles(),
                            node.getState().getMoveableObstacles()
                    ), false);

                    // Now connect this corner node to the state.
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<State, Action> endNode = connectNodeToState(cornerNode, state, true);

                    // Add the corner node as a child of the parent node
                    addChildNode(node, cornerNode);

                    return endNode;
                } catch (InvalidStateException e) {
                    // Second attempt. Corner node with (nodeX, stateY).
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<State, Action> cornerNode = connectNodeToState(node, new State(
                            new MoveableBox(nodeX, stateY, state.getMainBox().getRect().getWidth()),
                            node.getState().getStaticObstacles(),
                            node.getState().getMoveableObstacles()
                    ), false);

                    // Now connect this corner node to the state.
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<State, Action> endNode = connectNodeToState(cornerNode, state, true);

                    // Add the corner node as a child of the parent node
                    addChildNode(node, cornerNode);

                    return endNode;
                }
            }
        }

        // They're already in the same place. Just return the node
        return node;
    }

    /**
     * Add a child node to a parent, and also add to the list of nodes.
     *
     * @param parent the parent node
     * @param child the child node
     */
    private void addChildNode(TreeNode<State, Action> parent, TreeNode<State, Action> child) {
        parent.addChild(child);
        nodes.add(child);
    }

    /**
     * Gets the solution node
     *
     * @return the solution node
     */
    public TreeNode<State, Action> getSolution() {
        return solutionNode;
    }

    /**
     * Gets the tree
     *
     * @return the tree
     */
    public TreeNode<State, Action> getTree() {
        return tree;
    }
}
