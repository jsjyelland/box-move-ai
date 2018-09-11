package solution;

import java.util.ArrayList;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * A state object, containing the location and orientation of the robot.
 */
public class RobotState {
    /**
     * The robot
     */
    private Robot robot;

    /**
     * A list of the static obstacles in the workspace
     */
    private ArrayList<Box> staticObstacles;

    /**
     * Construct a new state, checking if it is in collision
     *
     * @param robot the robot
     * @param staticObstacles the static obstacles to check collision with
     */
    public RobotState(Robot robot, ArrayList<Box> staticObstacles) {
        this.robot = robot;
        this.staticObstacles = staticObstacles;
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
     * @throws InvalidStateException if the new state is invalid, or if the move is in two
     * directions.
     */
    public TreeNodeSingle<RobotState> action(double dx, double dy, double dtheta) throws InvalidStateException{

        // Clone this state
        RobotState newState = clone();

        // Move the robot
        newState.robot.move(dx, dy, dtheta);

        // TODO collision check

        // Create a new node with this new state and an action
        TreeNodeSingle<RobotState> newNode = new TreeNodeSingle<>(newState);

        return newNode;
    }

    /**
     * Check if the state is valid. The state is valid if the robot doesn't collide with any
     * of the static obstacles and is inside the workspace.
     *
     * @return whether the state is valid or not
     */
    public boolean isValid() {
        // Check if the mainBox is valid
        return robot.isValid(staticObstacles);
    }

    /**
     * Validate the state
     *
     * @throws InvalidStateException if the state is invalid
     */
    public void validate() throws InvalidStateException {
        if (!isValid()) {
            throw new InvalidStateException();
        }
    }

    /**
     * Clone the state
     *
     * @return the cloned state
     */
    public RobotState clone() {
        return new RobotState(robot.clone(), new ArrayList<>(staticObstacles));
    }

    public double distanceTo(RobotState other) {
        // TODO not sure about this one
        return 0;
    }

    public RobotState stepTowards(RobotState other, double delta) throws InvalidStateException {
        if (distanceTo(other) <= delta) {
            return other;
        }

        RobotState newState = clone();

        // TODO not sure about this one

        return newState;
    }

    /**
     * Add a static obstacle
     *
     * @param newObstacle the obstacle to add
     */
    public void addStaticObstacle(Box newObstacle) {
        staticObstacles.add(newObstacle);
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
     * Get the static obstacles
     *
     * @return the static obstacles
     */
    public ArrayList<Box> getStaticObstacles() {
        return staticObstacles;
    }

    /**
     * Set the static obstacles
     *
     * @param staticObstacles the static obstacles
     */
    public void setStaticObstacles(ArrayList<Box> staticObstacles) {
        this.staticObstacles = staticObstacles;
    }
}