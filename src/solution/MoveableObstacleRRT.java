package solution;

import java.util.ArrayList;

/**
 * An RRT for moving a moveable obstacle
 */
public class MoveableObstacleRRT extends MoveableBoxRRT {
    /**
     * The list of solution leaf nodes up to the top level RRT
     */
    private ArrayList<TreeNode<MoveableBoxState, Action>> solutionLeaves;

    /**
     * Construct a MoveableObstacleRRT
     *
     * @param staticObstacles the static obstacles
     * @param initialBox the initial box
     * @param solutionLeaves the leaf of the top level solution. This is the path to avoid
     */
    public MoveableObstacleRRT(ArrayList<Box> staticObstacles,
            ArrayList<MoveableBox> moveableObstacles, MoveableBox initialBox,
            ArrayList<TreeNode<MoveableBoxState, Action>> solutionLeaves) {
        super(staticObstacles, moveableObstacles, initialBox);

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
    protected boolean checkSolution(TreeNode<MoveableBoxState, Action> newestNode) {
        // Start at the current leaf
        for (TreeNode<MoveableBoxState, Action> solutionLeaf : solutionLeaves) {
            TreeNode<MoveableBoxState, Action> currentNode = solutionLeaf;

            // Move up the tree to the root
            while (currentNode.getParent() != null) {
                TreeNode<MoveableBoxState, Action> parent = currentNode.getParent();

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
        solutionLeaves.add(0, solutionNode);

        moveMoveableObstacles();

        Visualiser visualiser = new Visualiser();
        Window window = new Window(visualiser);

        visualiser.paintTree(getTree());
        visualiser.paintSolution(solutionNode);

        // No collisions
        return true;
    }

    @Override
    protected ArrayList<TreeNode<MoveableBoxState, Action>> getSolutionLeaves() {
        return solutionLeaves;
    }
}
