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
     * The initial box the robot is pushing
     */
    private MoveableBox initialBoxPushing;

    /**
     * The final box the robot is pushing
     */
    private MoveableBox finalBoxPushing;

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
     * @param initialBoxPushing the initial box the robot is pushing
     * @param finalBoxPushing the final box the robot is pushing
     */
    public RobotAction(Robot initialRobot, Robot finalRobot, boolean rotateFirst, MoveableBox initialBoxPushing, MoveableBox finalBoxPushing) {
        this(initialRobot, finalRobot, rotateFirst);
        this.initialBoxPushing = initialBoxPushing;
        this.finalBoxPushing = finalBoxPushing;

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
     * Set the initial box the robot is pushing
     * @param initialBoxPushing the initial box the robot is pushing
     */
    public void setInitialBoxPushing(MoveableBox initialBoxPushing) {
        this.initialBoxPushing = initialBoxPushing;
    }

    /**
     * Set the final box the robot is pushing
     * @param finalBoxPushing the initial box the robot is pushing
     */
    public void setFinalBoxPushing(MoveableBox finalBoxPushing) {
        this.finalBoxPushing = finalBoxPushing;
    }

    /**
     * Get the initial box the robot is pushing
     * @return the initial box the robot is pushing
     */
    public MoveableBox getInitialBoxPushing() {
        return initialBoxPushing;
    }

    /**
     * Get the final box the robot is pushing
     * @return the final box the robot is pushing
     */
    public MoveableBox getFinalBoxPushing() {
        return finalBoxPushing;
    }
}
