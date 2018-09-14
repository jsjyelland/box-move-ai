package solution;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.Math.PI;
import static java.lang.Math.signum;

/**
 * Information about a transition between states
 */
public class MoveableBoxAction {
    /**
     * The initial state of the box
     */
    private Box initialBox;

    /**
     * The final state of the box
     */
    private Box finalBox;

    /**
     * The path for the movement of the robot to the beginning of this action
     */
    private ArrayList<RobotAction> robotPath;

    /**
     * Construct an action
     *
     * @param initialBox the initial state of the box
     * @param finalBox the final state of the box
     */
    public MoveableBoxAction(Box initialBox, Box finalBox) {
        this.initialBox = initialBox;
        this.finalBox = finalBox;

        robotPath = new ArrayList<>();
    }

    /**
     * Move moveable obstacles out of the way
     *
     * @param solutionNodes the solutions of the trees above
     * @param previousRobotPosition the previous position of the robot
     * @param attachVisualisers whether to attach visualisers to the moveable obstacle RRTs.
     *
     * @return a list of robot actions to perform this move
     *
     * @throws NoPathException if this cannot be done
     */
    public ArrayList<RobotAction> moveBoxesOutOfPath(
            ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> solutionNodes,
            Robot previousRobotPosition,
            boolean attachVisualisers) throws NoPathException {
        Box movementBox = getMovementBox();

        ArrayList<MoveableBox> boxesToMove = new ArrayList<>();

        // Check if any of the moveable obstacles intersect this movement
        for (MoveableBox moveableObstacle : Workspace.getInstance().getMoveableObstacles()) {
            if (moveableObstacle.intersects(movementBox)) {
                Workspace.getInstance().markBoxNeedsMoving(moveableObstacle);
                boxesToMove.add(moveableObstacle);
            }
        }

        // Make sure there are boxes to move
        if (boxesToMove.size() == 0) {
            // No actions required
            return new ArrayList<>();
        }

        ArrayList<RobotAction> robotPath = new ArrayList<>();

        // Move each box out of the way
        for (MoveableBox box : boxesToMove) {
            // Create an RRT to move the box out of the way
            MoveableObstacleRRT obstacleRRT = new MoveableObstacleRRT(box, previousRobotPosition,
                    solutionNodes
            );

            // Attach a visualiser if it is required
            if (attachVisualisers) {
                Visualiser visualiser = new MoveableBoxVisualiser();
                Window window = new Window(visualiser);
                obstacleRRT.attachVisualiser(visualiser);
            }

            // Solve the RRT
            if (obstacleRRT.solve()) {
                ArrayList<RobotAction> obstacleRobotPath = obstacleRRT.getRobotPath();

                // Set the previous robot position to the end of the latest path
                previousRobotPosition = obstacleRobotPath.get(
                        obstacleRobotPath.size() - 1
                ).getFinalRobot();

                robotPath.addAll(obstacleRobotPath);

                // Make the moved box static
                Workspace.getInstance().finishPush(
                        obstacleRRT.getSolution().getState().getMainBox()
                );
            } else {
                throw new NoPathException();
            }
        }

        return robotPath;
    }

    /**
     * Get the x distance moved
     *
     * @return the x distance moved
     */
    public double getDx() {
        return finalBox.getRect().getX() - initialBox.getRect().getX();
    }

    /**
     * Get the y distance moved
     *
     * @return the y distance moved
     */
    public double getDy() {
        return finalBox.getRect().getY() - initialBox.getRect().getY();
    }

    /**
     * Gets the box representing the movement
     *
     * @return the movement box
     */
    public Box getMovementBox() {
        return initialBox.union(finalBox);
    }

    /**
     * Get the position of the robot required to initiate pushing the box
     *
     * @return the initial position of the robot
     */
    public Robot getRobotPushingPosition() {
        return new Robot(
                new Point2D.Double(
                        initialBox.getRect().getCenterX() +
                                -signum(getDx()) * (initialBox.getRect().getWidth() / 2),
                        initialBox.getRect().getCenterY() +
                                -signum(getDy()) * (initialBox.getRect().getHeight() / 2)
                ),
                getDx() == 0 ? 0 : PI / 2,
                Workspace.getInstance().getRobotWidth()
        );
    }

    /**
     * Get the position of the robot after the push
     *
     * @return the final position of the robot
     */
    public Robot getFinalRobotPosition() {
        Robot initialRobot = getRobotPushingPosition();
        initialRobot.move(getDx(), getDy(), 0);
        return initialRobot;
    }

    /**
     * Calculate the path for the robot to move to get to the start of this action and push the box.
     * If previousRobotPosition is null, the path will only contain the action to push the box.
     *
     * @param previousRobotPosition the starting position of the robot
     *
     * @throws NoPathException if no path could be found
     */
    public void solveRobotPath(Robot previousRobotPosition) throws NoPathException {
        Robot pushingPosition = getRobotPushingPosition();

        if (previousRobotPosition != null) {
            // Create an RRT for the robot
            RobotRRT rrt = new RobotRRT(previousRobotPosition, pushingPosition, initialBox);

            // Solve the rrt
            if (rrt.solve()) {
                robotPath = rrt.getSolution().actionPathFromRoot();
            } else {
                // No path found
                throw new NoPathException();
            }
        }

        // Add the action that moves the box
        robotPath.add(new RobotAction(pushingPosition, getFinalRobotPosition(), initialBox));
    }

    /**
     * Gets the path of robot actions to move the robot from the previous position to the start of
     * this action and to push the box. Index 0 is the start of the path.
     *
     * @return the path of robot actions
     */
    public ArrayList<RobotAction> getRobotPath() {
        return robotPath;
    }
}
