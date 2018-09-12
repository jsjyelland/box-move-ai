package solution;

import java.util.ArrayList;

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
     * @param staticObstacles the static obstacles to check collision with
     */
    public RobotState(Robot robot, ArrayList<Box> staticObstacles) {
        super(staticObstacles);
        this.robot = robot;
    }

    /**
     * Moving from one state to another.
     *
     * @param dx x distance to move robot by
     * @param dy y distance to move robot by
     * @param dtheta amount to rotate robot by
     *
     * @return a new node containing the new state and the action to get to this state
     *
     * @throws InvalidStateException if the new state is invalid
     */
    public TreeNode<RobotState, RobotAction> action(double dx, double dy, double dtheta)
            throws InvalidStateException {
        // Clone this state
        RobotState newState = clone();

        double distance = distanceWithDelta(dx, dy, dtheta);
        double numSteps = ceil(distance / 0.001);
        double stepSize = distance / numSteps;

        // Step along the line, checking the robot configuration at each step
        for (double i = 1; i <= numSteps; i++) {
            // Move the robot by step size
            newState.robot.move(
                    (stepSize / distance) * dx,
                    (stepSize / distance) * dy,
                    (stepSize / distance) * dtheta
            );

            // Check if this configuration is valid
            if (!newState.robot.isValid(staticObstacles)) {
                throw new InvalidStateException();
            }
        }

        // Create and return a new node with this new state
        return new TreeNode<>(newState, new RobotAction(dx, dy, dtheta));
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
        return robot.isValid(staticObstacles);
    }

    /**
     * Clone the state
     *
     * @return the cloned state
     */
    @Override
    public RobotState clone() {
        return new RobotState(robot.clone(), new ArrayList<>(staticObstacles));
    }

    /**
     * Calculate the distance of a change in x, y, and theta. This represents the euclidean distance
     * on a 3D graph of x, y and theta.
     *
     * @param dx change in x
     * @param dy change in y
     * @param dtheta change in theta
     *
     * @return the distance
     */
    private double distanceWithDelta(double dx, double dy, double dtheta) {
        return sqrt(pow(dx, 2) + pow(dy, 2) + pow(dtheta, 2));
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

            return distanceWithDelta(
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
     * Configure a state, given the nearest node in the search tree. Sets the static obstacles from
     * this node. The node must have a state class of RobotState
     *
     * @param nearestNode the nearest node in the search tree
     * @param <T> the class of state
     * @param <U> the class of action
     */
    @Override
    public <T extends State, U> void configure(TreeNode<T, U> nearestNode) {
        if (nearestNode.getState() instanceof RobotState) {
            RobotState state = (RobotState) nearestNode.getState();
            setStaticObstacles(state.getStaticObstacles());
        }
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
}
