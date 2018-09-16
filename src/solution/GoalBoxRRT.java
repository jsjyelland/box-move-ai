package solution;

import java.util.ArrayList;

/**
 * An RRT for moving a goal box
 */
public class GoalBoxRRT extends MoveableBoxRRT {
    /**
     * The goal box
     */
    private MoveableBox goalBox;

    /**
     * The solutions of all the goal box RRTs
     */
    private ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> allSolutions;

    /**
     * Construct a GoalBoxRRT
     *
     * @param initialBox the initial box
     * @param goalBox the goal box
     */
    public GoalBoxRRT(MoveableBox initialBox, MoveableBox goalBox) {
        super(initialBox, null);
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
            solutionNode = connectNodeToState(newestNode, new MoveableBoxState(goalBox), true);

            return true;
        } catch (InvalidStateException e) {
            // Couldn't connect to the goal
            return false;
        }
    }

    /**
     * Get the leaves of the solution trees. This is all the goal box rrt solutions.
     *
     * @return the leaves of the solution trees
     */
    @Override
    protected ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> getSolutionLeaves() {
        return new ArrayList<>(allSolutions);
    }

    /**
     * Push the box in the workspace
     *
     * @param boxToPush the box to push
     */
    @Override
    protected void pushBoxInWorkspace(MoveableBox boxToPush) {
        Workspace.getInstance().pushGoalBox(boxToPush);
    }

    /**
     * Finish pushing the box in the workspace
     *
     * @param newPosition the new position of the box
     */
    @Override
    protected void finishPushBoxInWorkspace(MoveableBox newPosition) {
        Workspace.getInstance().finishPushGoalBox(newPosition);
    }

    /**
     * Perform any actions after finding a solution to the RRT
     *
     * @return whether this was successful or not
     */
    @Override
    protected boolean finishSolution() {
        // Nothing to do here
        return true;
    }

    /**
     * Solve a path for the robot to move moveable obstacles given all the rrt solutions.
     *
     * @param allSolutions all the rrt solutions
     * @param previousRobotPosition the robot starting position
     *
     * @return the robot path
     *
     * @throws NoPathException if no path could be found
     */
    public ArrayList<RobotAction> solveMoveableObstacles(
            ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> allSolutions,
            Robot previousRobotPosition)
            throws NoPathException {
        this.allSolutions = allSolutions;

        return moveMoveableObstacles(previousRobotPosition);
    }

    /**
     * Get the goal box (final position)
     *
     * @return the goal box
     */
    public MoveableBox getGoalBox() {
        return goalBox;
    }

    /**
     * Get the obstacles to avoid
     *
     * @return the obstacles to avoid
     */
    @Override
    public ArrayList<Box> getObstacles() {
        return Workspace.getInstance().getStaticObstacles();
    }
}
