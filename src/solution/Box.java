package solution;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * An abstract box object. Is used for the static obstacles
 */
public class Box {
    /**
     * The underlying rectangle
     */
    protected Rectangle2D rect;

    /**
     * Construct a box with rectangle parameters
     *
     * @param x box x
     * @param y box y
     * @param w box width
     * @param h box height
     */
    public Box(double x, double y, double w, double h) {
        rect = new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Construct a box with a rectangle
     *
     * @param rect the rectangle for the box
     */
    public Box(Rectangle2D rect) {
        this.rect = rect;
    }

    /**
     * Construct a box with a position, width and height
     *
     * @param pos the position
     * @param w the width
     * @param h the height
     */
    public Box(Point2D pos, double w, double h) {
        this(pos.getX(), pos.getY(), w, h);
    }

    /**
     * Check if this box intersects another box
     *
     * @param other the other box to check intersection with
     *
     * @return whether this box intersects the other
     */
    public boolean intersects(Box other) {
        return rect.intersects(other.rect);
    }

    /**
     * Create a bounding box around both this and another box
     *
     * @param otherBox the other box to including in the bounding
     *
     * @return the bounding box
     */
    public Box union(Box otherBox) {
        return new Box(rect.createUnion(otherBox.rect));
    }

    /**
     * Get the underlying rectangle
     *
     * @return the rectangle
     */
    public Rectangle2D getRect() {
        return rect;
    }

    /**
     * Calculate the distance to another box
     *
     * @param other the other box
     *
     * @return the distance between the boxes
     */
    public double distanceTo(Box other) {
        return sqrt(
                pow(other.getRect().getX() - rect.getX(), 2) +
                        pow(other.getRect().getY() - rect.getY(), 2)
        );
    }

    /**
     * Check if the box is valid given a list of static obstacles. The state is valid if the box
     * doesn't collide with any of the static obstacles and is inside the workspace.
     *
     * @param staticObstacles list of static obstacles
     *
     * @return whether the box is valid or not
     */
    public boolean isValid(ArrayList<Box> staticObstacles) {
        // Check if the box is inside the workspace
        Rectangle2D boundingRectangle = new Rectangle2D.Double(0, 0, 1, 1);
        if (!boundingRectangle.contains(rect)) {
            return false;
        }

        // Check if the box collides with any static obstacles
        for (Box box : staticObstacles) {
            if (intersects(box)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if this box equals another object.
     *
     * @param obj the other object to check
     *
     * @return true if obj is a box and the underlying rects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        double MAX_ERROR = 0.00001;

        if (obj instanceof Box) {
            Box box = (Box) obj;

            if (box.rect == rect) {
                return true;
            }

            // Check with an error
            Rectangle2D r2d = box.rect;
            return ((abs(rect.getX() - r2d.getX()) < MAX_ERROR) &&
                            (abs(rect.getY() - r2d.getY()) < MAX_ERROR) &&
                            (abs(rect.getWidth() - r2d.getWidth()) < MAX_ERROR) &&
                            (abs(rect.getHeight() - r2d.getHeight()) < MAX_ERROR));
        }

        return false;
    }

    /**
     * Clone the box
     *
     * @return the cloned box
     */
    @Override
    public Box clone() {
        return new Box((Rectangle2D.Double) rect.clone());
    }
}
