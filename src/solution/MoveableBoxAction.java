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
     * The solution nodes of paths to move boxes out of the way
     */
    private ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> moveableBoxSolutionNodes;

    /**
     * The initial state of the box
     */
    private MoveableBox initialBox;

    /**
     * The final state of the box
     */
    private MoveableBox finalBox;

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
    public MoveableBoxAction(MoveableBox initialBox, MoveableBox finalBox) {
        this.initialBox = initialBox;
        this.finalBox = finalBox;

        moveableBoxSolutionNodes = new ArrayList<>();
        robotPath = new ArrayList<>();
    }

    /**
     * Move moveable obstacles out of the way
     *
     * @param solutionNodes the solutions of the trees above
     * @param attachVisualisers whether to attach visualisers to the moveable obstacle RRTs.
     * @param robotWidth the width of the robot
     *
     * @return a list of robot actions to perform this move
     *
     * @throws NoPathException if this cannot be done
     */
    public ArrayList<RobotAction> moveBoxesOutOfPath(
            ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> solutionNodes,
            boolean attachVisualisers, double robotWidth) throws NoPathException {
        TreeNode<MoveableBoxState, MoveableBoxAction> topLevelSolution =
                solutionNodes.get(solutionNodes.size() - 1);
        ArrayList<MoveableBox> boxesToMove = new ArrayList<>();

        for (MoveableBox moveableObstacle :
                new ArrayList<>(topLevelSolution.getState().getMoveableObstacles())) {
            if (moveableObstacle.intersects(getMovementBox())) {
                boxesToMove.add(moveableObstacle);
                topLevelSolution.getState().removeMoveableObstacle(moveableObstacle);
            }
        }

        // Make sure there are boxes to move
        if (boxesToMove.size() == 0) {
            // No actions required
            return new ArrayList<>();
        }

        ArrayList<RobotAction> robotPath = new ArrayList<>();
        Robot previousRobotPosition = null;

        // Move each box out of the way
        for (MoveableBox box : boxesToMove) {
            // Create an RRT to move the box out of the way
            MoveableObstacleRRT obstacleRRT = new MoveableObstacleRRT(
                    topLevelSolution.getState().getStaticObstacles(),
                    topLevelSolution.getState().getMoveableObstacles(),
                    box,
                    solutionNodes,
                    robotWidth
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

                if (previousRobotPosition != null) {
                    // Move the robot from the end of the last moveable obstacle path to the beginning
                    // of the next
                    RobotRRT rrt = new RobotRRT(
                            topLevelSolution.getState().getAllObstacles(),
                            previousRobotPosition,
                            obstacleRobotPath.get(0).getInitialRobot(),
                            obstacleRRT.getInitialBox()
                    );

                    if (rrt.solve()) {
                        // Add the path to get to the start of the box moving path, and the box moving
                        // path
                        robotPath.addAll(rrt.getSolution().actionPathFromRoot());
                        robotPath.addAll(obstacleRobotPath);
                    } else {
                        throw new NoPathException();
                    }
                }

                // Set the previous robot position to the end of the latest path
                previousRobotPosition = obstacleRobotPath.get(
                        obstacleRobotPath.size() - 1
                ).getFinalRobot();

                // Get the solution
                TreeNode<MoveableBoxState, MoveableBoxAction> solution = obstacleRRT.getSolution();

                // Add the solution to the list
                moveableBoxSolutionNodes.add(0, solution);

                // Make a new Box to represent the position of the moved box,
                // now as a static obstacle
                Box newBox = new Box(solution.getState().getMainBox().getRect());

                // Make the moveable obstacle static
                topLevelSolution.getState().addStaticObstacle(newBox);
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
        return initialBox.union(finalBox);
    }

    /**
     * Get the position of the robot required to initiate pushing the box
     *
     * @return the initial position of the robot
     */
    public Robot getRobotPushingPosition(double width) {
        return new Robot(
                new Point2D.Double(
                        initialBox.getRect().getCenterX() +
                                -signum(getDx()) * (initialBox.getRect().getWidth() / 2),
                        initialBox.getRect().getCenterY() +
                                -signum(getDy()) * (initialBox.getRect().getHeight() / 2)
                ),
                getDx() == 0 ? 0 : PI / 2,
                width
        );
    }

    /**
     * Get the position of the robot after the push
     *
     * @return the final position of the robot
     */
    public Robot getFinalRobotPosition(double width) {
        Robot initialRobot = getRobotPushingPosition(width);
        initialRobot.move(getDx(), getDy(), 0);
        return initialRobot;
    }

    /**
     * Calculate the path for the robot to move to get to the start of this action and push the box.
     * If previousRobotPosition is null, the path will only contain the action to push the box.
     *
     * @param staticObstacles the static obstacles to avoid
     * @param previousRobotPosition the starting position of the robot
     * @param robotWidth the width of the robot
     *
     * @throws NoPathException if no path could be found
     */
    public void solveRobotPath(ArrayList<Box> staticObstacles,
            Robot previousRobotPosition, double robotWidth) throws NoPathException {
        Robot pushingPosition = getRobotPushingPosition(robotWidth);

        if (previousRobotPosition != null) {
            // Create an RRT for the robot
            RobotRRT rrt = new RobotRRT(staticObstacles, previousRobotPosition, pushingPosition,
                    initialBox
            );

//            Visualiser visualiser = new RobotVisualiser();
//            Window window = new Window(visualiser);
//            rrt.attachVisualiser(visualiser);

            // Solve the rrt
            if (rrt.solve()) {
                robotPath = rrt.getSolution().actionPathFromRoot();
            } else {
                // No path found
                throw new NoPathException();
            }
        }

        // Add the action that moves the box
        robotPath.add(new RobotAction(pushingPosition, getFinalRobotPosition(robotWidth), false, initialBox, finalBox));
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

    /**
     * Get the final state of the box
     *
     * @return the final state of the box
     */
    public Box getFinalBox() {
        return finalBox;
    }

    /**
     * Get the initial state of the box
     *
     * @return the initial state of the box
     */
    public Box getInitialBox() {
        return initialBox;
    }
}
