package solution;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.*;

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
     * @param newTheta the new theta value for the robot to move to
     * @param boxToPush the box the robot is pushing
     *
     * @return a new node containing the new state and the action to get to this state
     *
     * @throws InvalidStateException if the new state is invalid
     */
    public TreeNode<RobotState, RobotAction> action(double dx, double dy, double newTheta, Box boxToPush)
            throws InvalidStateException {
        // Clone this state
        RobotState newState = clone();

        double distance = distanceWithNewTheta(dx, dy, newTheta);
        double numSteps = ceil(distance / 0.001);
        double stepSize = distance / numSteps;

        // Step along the line, checking the robot configuration at each step
        for (double i = 0; i < numSteps; i++) {
            // Move the robot by step size
            newState.robot.move(
                    (dx / distance) * stepSize,
                    (dy / distance) * stepSize,
                    ((newTheta - robot.getTheta()) / distance) * stepSize
            );

            // Check if this configuration is valid
            if (!(i == 0 || i == numSteps - 1) && !newState.isValid(boxToPush)) {
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
     * @param newTheta new value of theta
     *
     * @return the distance
     */
    private double distanceWithNewTheta(double dx, double dy, double newTheta) {
        double deltaCos = cos(newTheta) - cos(robot.getTheta());
        double deltaSin = sin(newTheta) - sin(robot.getTheta());
        return sqrt(max(pow(dx - robot.getWidth() * deltaCos, 2) + pow(dy - robot.getWidth() * deltaSin, 2),
                pow(dx + robot.getWidth() * deltaCos, 2) + pow(dy + robot.getWidth() * deltaSin, 2)));
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

            return distanceWithNewTheta(
                    robot.getPos().getX() - robotState.getRobot().getPos().getX(),
                    robot.getPos().getY() - robotState.getRobot().getPos().getY(),
                    robotState.getRobot().getTheta()
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
        Robot newRobot = new Robot(
                robot.getX() + (delta / distance) * (robotState.getRobot().getX() - robot.getX()),
                robot.getY() + (delta / distance) * (robotState.getRobot().getY() - robot.getY()),
                robot.getTheta() + (delta / distance) * (robotState.getRobot().getTheta() -
                                                                 robot.getTheta()),
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
