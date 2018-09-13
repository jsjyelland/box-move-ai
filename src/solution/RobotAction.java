package solution;

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
     * Whether the robot should rotate before moving or not
     */
    private boolean rotateFirst;

    /**
     * The box the robot is pushing
     */
    private Box boxPushing;

    /**
     * Construct a RobotAction
     *
     * @param initialRobot the initial state of the robot
     * @param finalRobot the final state of the robot
     * @param rotateFirst whether the robot should rotate before moving or not
     */
    public RobotAction(Robot initialRobot, Robot finalRobot, boolean rotateFirst) {
        this.initialRobot = initialRobot;
        this.finalRobot = finalRobot;
        this.rotateFirst = rotateFirst;
    }

    /**
     * Construct a RobotAction
     *
     * @param initialRobot the initial state of the robot
     * @param finalRobot the final state of the robot
     * @param rotateFirst whether the robot should rotate before moving or not
     * @param boxPushing the box the robot is pushing
     */
    public RobotAction(Robot initialRobot, Robot finalRobot, boolean rotateFirst, Box boxPushing) {
        this(initialRobot, finalRobot, rotateFirst);
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
        return finalRobot.getTheta() - initialRobot.getTheta();
    }

    /**
     * Should the robot rotate before moving or not
     *
     * @return whether the robot should rotate before moving or not
     */
    public boolean shouldRotateFirst() {
        return rotateFirst;
    }

    /**
     * Set the box the robot is pushing
     * @param boxPushing the box the robot is pushing
     */
    public void setBoxPushing(Box boxPushing) {
        this.boxPushing = boxPushing;
    }

    /**
     * Get the box the robot is pushing
     * @return the box the robot is pushing
     */
    public Box getBoxPushing() {
        return boxPushing;
    }
}
