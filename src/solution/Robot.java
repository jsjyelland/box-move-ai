package solution;

import java.awt.geom.*;
import java.util.ArrayList;

import static java.lang.Math.*;

/**
 * The robot. Consists of a line segment which can move vertically, horizontally and can rotate.
 */
public class Robot {
    /**
     * The point at the centre of the robot
     */
    private Point2D pos;

    /**
     * The angle of the robot in radians [0, 2pi].
     */
    private double theta;

    /**
     * The width of the robot
     */
    private double width;

    /**
     * Construct a robot with centre point pos and angle theta
     *
     * @param pos the centre point of the robot
     * @param theta the angle the robot is at
     * @param width the width of the robot
     */
    public Robot(Point2D pos, double theta, double width) {
        this.pos = pos;
        this.theta = theta;
        this.width = width;
    }

    /**
     * Construct a robot with centre point (x, y) and angle theta
     *
     * @param x the x value of the centre point of the robot
     * @param y the y value of the centre point of the robot
     * @param theta the angle the robot is at
     * @param width the width of the robot
     */
    public Robot(double x, double y, double theta, double width) {
        this(new Point2D.Double(x, y), theta, width);
    }

    /**
     * Get the centre x position
     *
     * @return the centre x position
     */
    public double getX() {
        return pos.getX();
    }

    /**
     * Get the centre y position
     *
     * @return the centre y position
     */
    public double getY() {
        return pos.getY();
    }

    /**
     * Get the x position of end 1
     *
     * @return the x position of end 1
     */
    public double getX1() {
        return pos.getX() - cos(theta) * width / 2;
    }

    /**
     * Get the x position of end 2
     *
     * @return the x position of end 2
     */
    public double getX2() {
        return pos.getX() + cos(theta) * width / 2;
    }

    /**
     * Get the y position of end 1
     *
     * @return the y position of end 1
     */
    public double getY1() {
        return pos.getY() - sin(theta) * width / 2;
    }

    /**
     * Get the y position of end 2
     *
     * @return the y position of end 2
     */
    public double getY2() {
        return pos.getY() + sin(theta) * width / 2;
    }

    /**
     * Get the point end 1
     *
     * @return the point end 1
     */
    public Point2D getP1() {
        return new Point2D.Double(getX1(), getY1());
    }

    /**
     * Get the point end 2
     *
     * @return the point end 2
     */
    public Point2D getP2() {
        return new Point2D.Double(getX2(), getY2());
    }

    /**
     * Get the width
     *
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Get the centre point
     *
     * @return the centre point
     */
    public Point2D getPos() {
        return pos;
    }

    /**
     * Get the angle
     *
     * @return the angle
     */
    public double getTheta() {
        return theta;
    }

    /**
     * Check if the robot configuration is valid given a list of static obstacles. The state is
     * valid if the robot doesn't collide with any of the static obstacles and is inside the
     * workspace.
     *
     * @param staticObstacles list of static obstacles
     *
     * @return whether the robot is valid or not
     */
    public boolean isValid(ArrayList<Box> staticObstacles) {
        // Check if the robot is inside the workspace
        Rectangle2D boundingRectangle = new Rectangle2D.Double(0, 0, 1, 1);
        if (!boundingRectangle.contains(getLine().getP1()) ||
                    !boundingRectangle.contains(getLine().getP2())) {
            return false;
        }

        // Check if the robot collides with any static obstacles
        for (Box box : staticObstacles) {
            if (box.getRect().intersectsLine(getLine())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Move the robot
     *
     * @param dx change in x
     * @param dy change in y
     * @param dtheta change in theta
     */
    public void move(double dx, double dy, double dtheta) {
        pos.setLocation(pos.getX() + dx, pos.getY() + dy);
        theta += dtheta;
    }

    /**
     * Check if this robot equals another object.
     *
     * @param obj the other object to check
     *
     * @return true if obj is a robot and the underlying angle, width and position are equal, false
     * otherwise
     */
    public boolean equals(Object obj) {
        if (obj instanceof Robot) {
            Robot r = (Robot) obj;
            return (theta == r.theta) && (pos.equals(r.pos)) && (width == r.width);
        }

        return false;
    }

    /**
     * Clone the robot
     *
     * @return a cloned robot
     */
    public Robot clone() {
        return new Robot((Point2D) pos.clone(), theta, width);
    }

    /**
     * Get a Line2D object representing the robot
     *
     * @return a Line2D representation of the robot
     */
    public Line2D getLine() {
        return new Line2D.Double(getP1(), getP2());
    }

    /**
     * Get a bounding circle
     *
     * @return a bounding circle
     */
    public Ellipse2D getCircleBounds() {
        return new Ellipse2D.Double(getX(), getY(), width, width);
    }

    /**
     * Get an area bounding the rotation
     *
     * @param newTheta the new angle
     *
     * @return the rotation bounding area
     */
    public Area getRotationBounds(double newTheta) {
        Arc2D arc1 = new Arc2D.Double(
                getCircleBounds().getBounds2D(),
                toDegrees(theta), toDegrees(newTheta - theta), Arc2D.PIE
        );

        Arc2D arc2 = new Arc2D.Double(
                getCircleBounds().getBounds2D(),
                toDegrees(theta + PI), toDegrees(newTheta - theta), Arc2D.PIE
        );

        Area area = new Area(arc1);
        area.add(new Area(arc2));

        return area;
    }
}
