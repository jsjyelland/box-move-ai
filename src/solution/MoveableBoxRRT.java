package solution;

import java.util.ArrayList;

import static java.lang.Math.random;

/**
 * An RRT for moving any moveable box
 */
public abstract class MoveableBoxRRT extends RRT<MoveableBoxState, MoveableBoxAction> {
    /**
     * The initial box
     */
    private MoveableBox initialBox;

    /**
     * Construct a MoveableBoxRRT
     *
     * @param staticObstacles the static obstacles
     * @param moveableObstacles the moveable obstacles
     * @param initialBox the box to move
     */
    public MoveableBoxRRT(ArrayList<Box> staticObstacles, ArrayList<MoveableBox> moveableObstacles,
            MoveableBox initialBox) {
        this.initialBox = initialBox;

        // Make an initial tree
        tree = new TreeNode<>(new MoveableBoxState(initialBox, staticObstacles, moveableObstacles),
                null
        );

        // Add the root to nodes
        nodes.add(tree);
    }

    /**
     * Attempt to connect a node to a state using only horizontal or vertical lines. Will only
     * attempt two movements (e.g. up then right).
     *
     * @param node the parent node
     * @param state the child state
     * @param addChild whether to add the new node to the tree or not
     *
     * @return a new node containing the child state. Will return node if they are in the same
     * place.
     *
     * @throws InvalidStateException if this is not possible
     */
    @Override
    protected TreeNode<MoveableBoxState, MoveableBoxAction> connectNodeToState(
            TreeNode<MoveableBoxState, MoveableBoxAction> node, MoveableBoxState state, boolean addChild)
            throws InvalidStateException {
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
                TreeNode<MoveableBoxState, MoveableBoxAction> newNode = node.getState().action(dx, dy);

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
                    TreeNode<MoveableBoxState, MoveableBoxAction> cornerNode = connectNodeToState(node, new MoveableBoxState(
                            new MoveableBox(stateX, nodeY, state.getMainBox().getRect().getWidth()),
                            node.getState().getStaticObstacles(),
                            node.getState().getMoveableObstacles()
                    ), false);

                    // Now connect this corner node to the state.
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<MoveableBoxState, MoveableBoxAction> endNode = connectNodeToState(cornerNode, state, true);

                    // Add the corner node as a child of the parent node
                    addChildNode(node, cornerNode);

                    return endNode;
                } catch (InvalidStateException e) {
                    // Second attempt. Corner node with (nodeX, stateY).
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<MoveableBoxState, MoveableBoxAction> cornerNode = connectNodeToState(node, new MoveableBoxState(
                            new MoveableBox(nodeX, stateY, state.getMainBox().getRect().getWidth()),
                            node.getState().getStaticObstacles(),
                            node.getState().getMoveableObstacles()
                    ), false);

                    // Now connect this corner node to the state.
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<MoveableBoxState, MoveableBoxAction> endNode = connectNodeToState(cornerNode, state, true);

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
     * Move moveable obstacles out of the way of the solution path
     */
    public void moveMoveableObstacles() {
        // Make sure a solution has been found
        if (solutionNode == null) {
            return;
        }

        // Loop through the solution path
        TreeNode<MoveableBoxState, MoveableBoxAction> currentNode = solutionNode;

        while (currentNode.getParent() != null) {
            currentNode.getAction().moveBoxesOutOfPath(getSolutionLeaves());
            currentNode = currentNode.getParent();
        }
    }

    /**
     * Generate a new random state
     *
     * @return a new random state
     */
    @Override
    protected MoveableBoxState newRandomState() {
        return new MoveableBoxState(
                new MoveableBox(random(), random(), initialBox.getRect().getWidth()), null, null
        );
    }

    /**
     * Gets a list of the leaf nodes of all solutions including parent RRTs. Array index 0 is the
     * deepest level. Increasing index means decreasing deepness.
     *
     * @return the list of solution leaf nodes
     */
    protected abstract ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> getSolutionLeaves();
}
