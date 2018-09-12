package solution;

public class RobotAction {
    /**
     * Change in x
     */
    private double dx;

    /**
     * Change in y
     */
    private double dy;

    /**
     * Change in theta
     */
    private double dtheta;

    /**
     * Construct a RobotAction
     *
     * @param dx change in x
     * @param dy change in y
     * @param dtheta change in theta
     */
    public RobotAction(double dx, double dy, double dtheta) {
        this.dx = dx;
        this.dy = dy;
        this.dtheta = dtheta;
    }

    /**
     * Get the change in x
     * @return the change in x
     */
    public double getDx() {
        return dx;
    }

    /**
     * Get the change in y
     * @return the change in y
     */
    public double getDy() {
        return dy;
    }

    /**
     * Get the change in theta
     * @return the change in theta
     */
    public double getDtheta() {
        return dtheta;
    }
}
