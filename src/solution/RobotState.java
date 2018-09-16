package solution;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.*;
import static solution.Utility.angleBetween;
import static solution.Utility.smallestAngleBetween;

/**
 * A state object, containing the location and orientation of the robot.
 */
public class RobotState extends State {
    /**
     * The robot
     */
    private Robot robot;

    /**
     * Construct a new robot state
     *
     * @param robot the robot
     */
    public RobotState(Robot robot) {
        this.robot = robot;
    }

    /**
     * Moving from one state to another.
     *
     * @param dx x distance to move robot by
     * @param dy y distance to move robot by
     * @param boxToPush the box the robot is pushing
     * @param dtheta change in theta
     *
     * @return a new node containing the new state and the action to get to this state
     *
     * @throws InvalidStateException if the new state is invalid
     */
    public TreeNode<RobotState, RobotAction> action(double dx, double dy, double dtheta,
            Box boxToPush) throws InvalidStateException {
        RobotState newState = clone();

        double distance = distanceDelta(dx, dy, dtheta);
        double numSteps = ceil(distance / 0.001);

        // Step along the line, checking the robot configuration at each step
        for (double i = 1; i <= numSteps; i++) {
            // Clone this state and move the robot along a line
            newState = clone();
            newState.robot.move(i / numSteps * dx, i / numSteps * dy, i / numSteps * dtheta);

            // Check if this configuration is valid
            if (i == 1 || i == numSteps) {
                if (!newState.isValid()) {
                    throw new InvalidStateException();
                }
            } else if (!newState.isValid(boxToPush)) {
                throw new InvalidStateException();
            }
        }

        // Create and return a new node with this new state
        return new TreeNode<>(newState, new RobotAction(robot, newState.robot));
    }

    /**
     * Check if the state is valid. The state is valid if the robot doesn't collide with any of the
     * static obstacles and is inside the workspace.
     *
     * @return whether the state is valid or not
     */
    @Override
    public boolean isValid() {
        // Check if the robot is valid
        return robot.isValid(Workspace.getInstance().getAllObstacles());
    }

    /**
     * Clone the state
     *
     * @return the cloned state
     */
    @Override
    public RobotState clone() {
        return new RobotState(robot.clone());
    }

    /**
     * Calculate the distance of a change in x, y, and theta. This represents maximum distance the
     * ends of the robot will have to move
     *
     * @param dx change in x
     * @param dy change in y
     * @param dtheta change in theta
     *
     * @return the distance
     */
    private double distanceDelta(double dx, double dy, double dtheta) {
        // Clone this state's robot
        Robot newRobot = robot.clone();

        // move it, then calculate the distance to the old position
        newRobot.move(dx, dy, dtheta);

        return robot.distanceToOtherRobot(newRobot);
    }

    /**
     * Calculate the distance to another state. Is the euclidean distance on a 3D graph with axes x,
     * y, theta.
     *
     * @param other the other state to calculate the distance to
     *
     * @return the distance between states if other is a RobotState. -1 otherwise.
     */
    @Override
    public double distanceTo(State other) {
        if (other instanceof RobotState) {
            RobotState robotState = (RobotState) other;

            return distanceDelta(
                    robot.getPos().getX() - robotState.getRobot().getPos().getX(),
                    robot.getPos().getY() - robotState.getRobot().getPos().getY(),
                    robot.getTheta() - robotState.getRobot().getTheta()
            );
        } else {
            return -1;
        }
    }

    /**
     * Create a new state that is at most delta distance along the line between this state and a new
     * one. Distance means the euclidean distance on a 3D graph with axes x, y, theta.
     *
     * @param other the other state
     * @param delta the distance to move along the line between the two states
     *
     * @return the new state
     *
     * @throws InvalidStateException if other is not a RobotState
     */
    @Override
    public RobotState stepTowards(State other, double delta) throws InvalidStateException {
        // Make sure other is a RobotState
        if (!(other instanceof RobotState)) {
            throw new InvalidStateException();
        }

        RobotState robotState = (RobotState) other;

        // Distance to the other state
        double distance = distanceTo(other);

        // No need to do anything
        if (distance <= delta) {
            return robotState;
        }

        RobotState newState = clone();

        // Move along the line between this state and other by amount delta
        double dx = robotState.getRobot().getX() - robot.getX();
        double dy = robotState.getRobot().getY() - robot.getY();

        double smallestAngle = smallestAngleBetween(robotState.getRobot().getTheta(), robot.getTheta());
        double dtheta = angleBetween(robot.getTheta(), robotState.getRobot().getTheta()) < PI ? smallestAngle : -smallestAngle;

        Robot newRobot = new Robot(
                robot.getX() + (delta / distance) * dx,
                robot.getY() + (delta / distance) * dy,
                robot.getTheta() + (delta / distance) * dtheta,
                robot.getWidth()
        );

        newState.setRobot(newRobot);

        return newState;
    }

    /**
     * Get the robot
     *
     * @return the robot
     */
    public Robot getRobot() {
        return robot;
    }

    /**
     * Set the robot
     *
     * @param robot the new robot
     */
    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    /**
     * Whether the state is valid or not given a box to push
     *
     * @param boxPushing the box to push
     *
     * @return whether the state is valid or not
     */
    public boolean isValid(Box boxPushing) {
        return isValid() && robot.isValid(new ArrayList<>(Arrays.asList(boxPushing)));
    }

    /**
     * Validates the state given a box to push
     *
     * @param boxPushing the box to push
     *
     * @throws InvalidStateException if the state is invalid
     */
    public void validate(Box boxPushing) throws InvalidStateException {
        if (!isValid(boxPushing)) {
            throw new InvalidStateException();
        }
    }
}
