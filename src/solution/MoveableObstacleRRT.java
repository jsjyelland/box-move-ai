package solution;

import java.util.ArrayList;

/**
 * An RRT for moving a moveable obstacle
 */
public class MoveableObstacleRRT extends RRT {
    private TreeNode<State, Action> currentLeaf;

    private Visualiser visualiser;

    /**
     * Construct a MoveableObstacleRRT
     *
     * @param staticObstacles the static obstacles
     * @param initialBox the initial box
     */
    public MoveableObstacleRRT(ArrayList<Box> staticObstacles,
            ArrayList<MoveableBox> moveableObstacles, MoveableBox initialBox,
            TreeNode<State, Action> currentLeaf) {
        super(staticObstacles, moveableObstacles, initialBox);

        this.currentLeaf = currentLeaf;

        visualiser = new Visualiser(staticObstacles, moveableObstacles);
        Window window = new Window(visualiser);
    }

    /**
     * Check if a solution is valid. The box must be out of the way of the path given by
     * currentLeaf.
     *
     * @param newestNode the newest node created by exploring
     *
     * @return whether the solution is valid or not
     */
    @Override
    protected boolean checkSolution(TreeNode<State, Action> newestNode) {
        visualiser.paintTree(getTree());

        // Start at the current leaf
        TreeNode<State, Action> currentNode = currentLeaf;

        // Move up the tree to the root
        while (currentNode.getParent() != null) {
            TreeNode<State, Action> parent = currentNode.getParent();

            // Create a union representing the path from each node to its parent
            Box union = parent.getState().getMainBox().union(currentNode.getState().getMainBox());

            // Check if the solution intersects the path
            if (newestNode.getState().getMainBox().intersects(union)) {
                return false;
            }

            currentNode = parent;
        }

        // No collisions
        return true;
    }
}
