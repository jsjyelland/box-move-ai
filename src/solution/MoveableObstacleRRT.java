package solution;

import java.util.ArrayList;

/**
 * An RRT for moving a moveable obstacle
 */
public class MoveableObstacleRRT extends RRT {
    private ArrayList<TreeNode<State, Action>> solutionLeaves;

    private Visualiser visualiser;

    /**
     * Construct a MoveableObstacleRRT
     *
     * @param staticObstacles the static obstacles
     * @param initialBox the initial box
     * @param solutionLeaves the leaf of the top level solution. This is the path to avoid
     */
    public MoveableObstacleRRT(ArrayList<Box> staticObstacles,
            ArrayList<MoveableBox> moveableObstacles, MoveableBox initialBox,
            ArrayList<TreeNode<State, Action>> solutionLeaves) {
        super(staticObstacles, moveableObstacles, initialBox);

        this.solutionLeaves = solutionLeaves;

        visualiser = new Visualiser(staticObstacles, moveableObstacles);
        Window window = new Window(visualiser);
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
    protected boolean checkSolution(TreeNode<State, Action> newestNode) {
        visualiser.paintTree(getTree());

        // Start at the current leaf
        for(TreeNode<State, Action> solutionLeaf: solutionLeaves) {
            TreeNode<State, Action> currentNode = solutionLeaf;

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
        }

        solutionNode = newestNode;

        solutionLeaves.add(0, solutionNode);

        TreeNode<State, Action> currentNode = solutionNode;

        while (currentNode.getParent() != null) {
            // Move any moveable obstacles out of the way
            currentNode.getAction().moveBoxesOutOfPath(solutionLeaves);
            currentNode = currentNode.getParent();
        }

        visualiser.paintSolution(solutionNode);

        // No collisions
        return true;
    }
}
