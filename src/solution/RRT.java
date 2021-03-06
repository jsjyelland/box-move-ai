package solution;

import java.util.ArrayList;

/**
 * An abstract rapidly exploring random tree
 *
 * @param <T> the state class
 * @param <U> the action class
 */
public abstract class RRT<T extends State, U> {
    /**
     * Max distance a node can randomly expand doing RRT
     */
    private static double MAX_DISTANCE = 0.2;

    /**
     * Max number of nodes before exiting search
     */
    private static int MAX_NODES = 3000;

    /**
     * The tree of states
     */
    protected TreeNode<T, U> tree;

    /**
     * List of all the nodes
     */
    protected ArrayList<TreeNode<T, U>> nodes;

    /**
     * The solution node for this RRT.
     */
    protected TreeNode<T, U> solutionNode = null;

    /**
     * The visualiser attached to this RRT.
     */
    public Visualiser visualiser;

    /**
     * Construct an RRT
     */
    public RRT() {
        // List of all the nodes
        nodes = new ArrayList<>();
    }

    /**
     * Expand the tree one step.
     *
     * @return if a solution is found or not
     */
    private boolean expand() {
        // Sample a random node in free space
        while (true) {
            try {
                T newRandomState = newRandomState();

                // Get the nearest node to the new one
                TreeNode<T, U> node = nearestNode(newRandomState);

                // Make sure this is valid
                validateState(newRandomState);

                // Step towards the new random state up to MAX_DISTANCE
                T newState = (T) node.getState().stepTowards(newRandomState, MAX_DISTANCE);

                // Make sure this is valid still
                validateState(newState);

                // Add the new state to the tree
                TreeNode<T, U> newNode = connectNodeToState(node, newState, true);

                return checkSolution(newNode);
            } catch (InvalidStateException e) {
                // If this happens, try again. Means the new state is in collision
            }
        }
    }

    /**
     * Solve this rrt. If a visualiser is attached, the solution will be drawn on it.
     */
    public boolean solve() {
        while (nodes.size() <= MAX_NODES) {
            if (expand()) {
                if (visualiserAttached()) {
                    visualiser.paintSolution(solutionNode);
                }

                return true;
            }

            if (visualiserAttached()) {
                visualiser.paintTree(tree);
            }
        }

        return false;
    }

    /**
     * Check to see if the current tree has a solution
     *
     * @return if a solution is found or not
     */
    protected abstract boolean checkSolution(TreeNode<T, U> newestNode);

    /**
     * Attach a visualiser to this RRT.
     *
     * @param visualiser the visualiser to attach
     */
    public void attachVisualiser(Visualiser visualiser) {
        this.visualiser = visualiser;
    }

    /**
     * Find the nearest node in the tree to a given state. Uses straight line distance
     *
     * @param state the state to find the node nearest to
     *
     * @return the nearest node
     */
    private TreeNode<T, U> nearestNode(T state) {
        TreeNode<T, U> bestNode = nodes.get(0);
        double shortestDistance = state.distanceTo(bestNode.getState());

        for (TreeNode<T, U> node : nodes) {
            double distance = node.getState().distanceTo(state);

            if (distance < shortestDistance) {
                shortestDistance = distance;
                bestNode = node;
            }
        }

        return bestNode;
    }

    /**
     * Attempt to connect a node to a state.
     *
     * @param node the parent node
     * @param state the child state
     * @param addChild whether to add the new node to the tree or not
     *
     * @throws InvalidStateException if there is no connection
     */
    protected abstract TreeNode<T, U> connectNodeToState(TreeNode<T, U> node, T state,
            boolean addChild) throws InvalidStateException;

    /**
     * Add a child node to a parent, and also add to the list of nodes.
     *
     * @param parent the parent node
     * @param child the child node
     */
    protected void addChildNode(TreeNode<T, U> parent, TreeNode<T, U> child) {
        parent.addChild(child);
        nodes.add(child);
    }

    /**
     * Gets the solution node
     *
     * @return the solution node
     */
    public TreeNode<T, U> getSolution() {
        return solutionNode;
    }

    /**
     * Generate a new random state
     *
     * @return the new state
     */
    protected abstract T newRandomState();

    /**
     * Whether this rrt has a visualiser attached
     *
     * @return true if visualiser != null, false otherwise
     */
    public boolean visualiserAttached() {
        return visualiser != null;
    }

    /**
     * Validate a state
     *
     * @param state the state to validate
     *
     * @throws InvalidStateException if the state is invalid
     */
    protected abstract void validateState(T state) throws InvalidStateException;

    /**
     * Get the obstacles to avoid
     *
     * @return the obstacles to avoid
     */
    public abstract ArrayList<Box> getObstacles();
}
