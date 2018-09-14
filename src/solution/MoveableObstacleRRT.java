package solution;

import java.util.ArrayList;

/**
 * An RRT for moving a moveable obstacle
 */
public class MoveableObstacleRRT extends MoveableBoxRRT {
    /**
     * The list of solution leaf nodes up to the top level RRT. Index of 0 means deepest solution.
     */
    private ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> solutionLeaves;

    /**
     * Construct a MoveableObstacleRRT
     *
     * @param initialBox the initial box
     * @param robotStartingPosition the starting position of the robot
     * @param solutionLeaves the solution leaves. These are the paths to avoid.
     */
    public MoveableObstacleRRT(MoveableBox initialBox, Robot robotStartingPosition,
            ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> solutionLeaves) {
        super(initialBox, robotStartingPosition);

        this.solutionLeaves = solutionLeaves;
    }

    /**
     * Check if a solution is valid. The box must be out of the way of the path given by
     * solutionLeaves.
     *
     * @param newestNode the newest node created by exploring
     *
     * @return whether the solution is valid or not
     */
    @Override
    protected boolean checkMoveableBoxPath(
            TreeNode<MoveableBoxState, MoveableBoxAction> newestNode) {
        for (TreeNode<MoveableBoxState, MoveableBoxAction> solutionLeaf : solutionLeaves) {
            // Start at the current leaf
            TreeNode<MoveableBoxState, MoveableBoxAction> currentNode = solutionLeaf;

            // Move up the tree to the root
            while (currentNode.getParent() != null) {
                TreeNode<MoveableBoxState, MoveableBoxAction> parent = currentNode.getParent();

                // Create a union representing the path from each node to its parent
                Box union = currentNode.getAction().getMovementBox();

                // Check if the solution intersects the path
                if (newestNode.getState().getMainBox().intersects(union)) {
                    return false;
                }

                currentNode = parent;
            }
        }

        solutionNode = newestNode;

        // Add the solution to the solution leaves list
        solutionLeaves.add(solutionNode);

        // No collisions
        return true;
    }

    /**
     * Gets a list of the leaf nodes of all solutions including parent RRTs. Array index 0 is the
     * deepest level. Increasing index means decreasing deepness.
     *
     * @return the list of solution leaf nodes
     */
    @Override
    protected ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> getSolutionLeaves() {
        return solutionLeaves;
    }

    /**
     * Push the box in the workspace
     *
     * @param boxToPush the box to push
     */
    @Override
    protected void pushBoxInWorkspace(MoveableBox boxToPush) {
        Workspace.getInstance().pushBox(boxToPush);
    }

    /**
     * Finish pushing the box in the workspace
     *
     * @param newPosition the new position of the box
     */
    @Override
    protected void finishPushBoxInWorkspace(MoveableBox newPosition) {
        Workspace.getInstance().finishPush(newPosition);
    }
}
