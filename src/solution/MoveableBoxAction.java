package solution;

import java.util.ArrayList;

/**
 * Information about a transition between states
 */
public class MoveableBoxAction {
    /**
     * The solution nodes of paths to move boxes out of the way
     */
    private ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> moveableBoxSolutionNodes;

    /**
     * The x distance moved
     */
    private double dx;

    /**
     * The y distance moved
     */
    private double dy;

    /**
     * The bounding box representing the movement
     */
    private Box movementBox;

    /**
     * Construct an action
     *
     * @param movementBox the bounding box representing the movement
     * @param dx distance moved in the x direction
     * @param dy distance moved in the y direction
     */
    public MoveableBoxAction(Box movementBox, double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
        this.movementBox = movementBox;

        moveableBoxSolutionNodes = new ArrayList<>();
    }

    /**
     * Move moveable obstacles out of the way
     *
     * @param solutionNodes the solutions of the trees above
     */
    public void moveBoxesOutOfPath(
            ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> solutionNodes) {
        TreeNode<MoveableBoxState, MoveableBoxAction> topLevelSolution =
                solutionNodes.get(solutionNodes.size() - 1);
        ArrayList<MoveableBox> boxesToMove = new ArrayList<>();

        for (MoveableBox moveableObstacle :
                new ArrayList<>(topLevelSolution.getState().getMoveableObstacles())) {
            if (moveableObstacle.intersects(movementBox)) {
                boxesToMove.add(moveableObstacle);
                topLevelSolution.getState().removeMoveableObstacle(moveableObstacle);
            }
        }

        // Make sure there are boxes to move
        if (boxesToMove.size() == 0) {
            return;
        }

        // Move each box out of the way
        for (MoveableBox box : boxesToMove) {
            // Create an RRT to move the box out of the way
            MoveableObstacleRRT obstacleRRT = new MoveableObstacleRRT(
                    solutionNodes.get(0).getState().getStaticObstacles(),
                    solutionNodes.get(0).getState().getMoveableObstacles(),
                    box,
                    solutionNodes
            );

            // Solve the RRT
            while (!obstacleRRT.expand());

            // Get the solution
            TreeNode<MoveableBoxState, MoveableBoxAction> solution = obstacleRRT.getSolution();

            // Add the solution to the list
            moveableBoxSolutionNodes.add(0, solution);

            // Make a new Box to represent the position of the moved box,
            // now as a static obstacle
            Box newBox = new Box(solution.getState().getMainBox().getRect());

            // Make the moveable obstacle static
            topLevelSolution.getState().addStaticObstacle(newBox);
        }
    }

    /**
     * Get the x distance moved
     *
     * @return the x distance moved
     */
    public double getDx() {
        return dx;
    }

    /**
     * Get the y distance moved
     *
     * @return the y distance moved
     */
    public double getDy() {
        return dy;
    }

    /**
     * Get the solution nodes for the moveable box paths
     *
     * @return the solution nodes
     */
    public ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> getMoveableBoxSolutionNodes() {
        return moveableBoxSolutionNodes;
    }

    /**
     * Gets the box representing the movement
     *
     * @return the movement box
     */
    public Box getMovementBox() {
        return movementBox;
    }
}
