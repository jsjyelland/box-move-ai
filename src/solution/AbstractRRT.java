package solution;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import static java.lang.Math.random;

/**
 * A rapidly exploring random tree interface
 */
public abstract class AbstractRRT {
    /**
     * The tree of states
     */
    protected TreeNode<State, Action> tree;

    /**
     * List of all the nodes
     */
    protected ArrayList<TreeNode<State, Action>> nodes;

    /**
     * The solution node for this RRT.
     */
    protected TreeNode<State, Action> solutionNode = null;

    /**
     * The initial box
     */
    protected MoveableBox initialBox;

    /**
     * Construct an RRT
     * @param staticObstacles the static obstacles
     * @param initialBox the initial box
     */
    public AbstractRRT(Box[] staticObstacles, MoveableBox[] moveableObstacles, MoveableBox initialBox) throws InvalidStateException{
        this.initialBox = initialBox;

        // Make an initial tree
        tree = new TreeNode<>(new State(initialBox, staticObstacles, moveableObstacles), null);

        // List of all the nodes
        nodes = new ArrayList<>();
        nodes.add(tree);
    }

    /**
     * Expand the tree one step. This function has to be implemented by the subclasses
     * @return if a solution is found or not
     */
    public boolean expand() {
        throw new NotImplementedException();
    }

    /**
     * Find the nearest node in the tree to a given state. Uses straight line distance
     * @param state the state to find the node nearest to
     * @return the nearest node
     */
    protected TreeNode<State, Action> nearestNode(State state) {
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
    protected TreeNode<State, Action> connectNodeToState(TreeNode<State, Action> node, State state) throws InvalidStateException {
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
    protected void addChildNode(TreeNode<State, Action> parent, TreeNode<State, Action> child) {
        parent.addChild(child);
        nodes.add(child);
    }

    /**
     * Gets the solution node
     * @return the solution node
     */
    public TreeNode<State, Action> getSolution() {
        return solutionNode;
    }

    /**
     * Gets the tree
     * @return the tree
     */
    public TreeNode<State, Action> getTree() {
        return tree;
    }
}
