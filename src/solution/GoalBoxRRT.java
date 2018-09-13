package solution;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An RRT for moving a goal box
 */
public class GoalBoxRRT extends MoveableBoxRRT {
    /**
     * The goal box
     */
    private MoveableBox goalBox;

    /**
     * Construct a GoalBoxRRT
     *
     * @param staticObstacles the static obstacles
     * @param initialBox the initial box
     * @param goalBox the goal box
     * @param robotWidth the width of the robot
     */
    public GoalBoxRRT(ArrayList<Box> staticObstacles, ArrayList<MoveableBox> moveableObstacles,
            MoveableBox initialBox, MoveableBox goalBox, double robotWidth) {
        super(staticObstacles, moveableObstacles, initialBox, robotWidth);
        this.goalBox = goalBox;
    }

    /**
     * Check if a solution is valid. The box must be able to connect to the goal.
     *
     * @param newestNode the newest node created by exploring
     *
     * @return whether the solution is valid or not
     */
    @Override
    protected boolean checkMoveableBoxPath(
            TreeNode<MoveableBoxState, MoveableBoxAction> newestNode) {
        try {
            // Try to connect to the goal
            solutionNode = connectNodeToState(newestNode, new MoveableBoxState(
                    goalBox,
                    newestNode.getState().getStaticObstacles(),
                    newestNode.getState().getMoveableObstacles()
            ), true);

            return true;
        } catch (InvalidStateException e) {
            // Couldn't connect to the goal
            return false;
        }
    }

    /**
     * Get the leaves of the solution trees. This is the top level, so the leaves list contains
     * only one element, solutionNode.
     *
     * @return the leaves of the solution trees
     */
    @Override
    protected ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> getSolutionLeaves() {
        return new ArrayList<>(Arrays.asList(solutionNode));
    }
}
