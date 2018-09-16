package solution;

import static java.lang.Math.PI;
import static solution.Utility.angleBetween;
import static solution.Utility.smallestAngleBetween;

/**
 * An action the robot takes to move from one position to another
 */
public class RobotAction {
    /**
     * The initial state of the robot
     */
    private Robot initialRobot;

    /**
     * The final state of the robot
     */
    private Robot finalRobot;

    /**
     * The box the robot is pushing
     */
    private MoveableBox boxPushing;

    /**
     * Construct a RobotAction
     *
     * @param initialRobot the initial state of the robot
     * @param finalRobot the final state of the robot
     */
    public RobotAction(Robot initialRobot, Robot finalRobot) {
        this.initialRobot = initialRobot;
        this.finalRobot = finalRobot;
    }

    /**
     * Construct a RobotAction
     *
     * @param initialRobot the initial state of the robot
     * @param finalRobot the final state of the robot
     * @param boxPushing the box the robot is pushing
     */
    public RobotAction(Robot initialRobot, Robot finalRobot, MoveableBox boxPushing) {
        this(initialRobot, finalRobot);
        this.boxPushing = boxPushing;
    }

    /**
     * Get the initial robot state
     *
     * @return the initial robot state
     */
    public Robot getInitialRobot() {
        return initialRobot;
    }

    /**
     * Get the final robot state
     *
     * @return the final robot state
     */
    public Robot getFinalRobot() {
        return finalRobot;
    }

    /**
     * Get the change in x
     *
     * @return the change in x
     */
    public double getDx() {
        return finalRobot.getX() - initialRobot.getX();
    }

    /**
     * Get the change in y
     *
     * @return the change in y
     */
    public double getDy() {
        return finalRobot.getY() - initialRobot.getY();
    }

    /**
     * Get the change in theta
     *
     * @return the change in theta
     */
    public double getDtheta() {
        double smallestAngle = smallestAngleBetween(finalRobot.getTheta(), initialRobot.getTheta());
        double dtheta = angleBetween(initialRobot.getTheta(), finalRobot.getTheta()) < PI ? smallestAngle : -smallestAngle;
        return dtheta;
    }

    /**
     * Set the box the robot is pushing
     * @param boxPushing the box the robot is pushing
     */
    public void setBoxPushing(MoveableBox boxPushing) {
        this.boxPushing = boxPushing;
    }

    /**
     * Get the box the robot is pushing
     * @return the box the robot is pushing
     */
    public MoveableBox getBoxPushing() {
        return boxPushing;
    }
}
