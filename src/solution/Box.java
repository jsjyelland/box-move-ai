package solution;

import java.awt.geom.Rectangle2D;

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
     * @param rect the rectangle for the box
     */
    public Box(Rectangle2D rect) {
        this.rect = (Rectangle2D) rect.clone();
    }

    /**
     * If this box intersects another box
     * @param other the other box to check intersection with
     * @return whether this box intersects the other
     */
    public boolean intersects(Box other) {
        return rect.intersects(other.rect);
    }

    /**
     * Create a bounding box around both this and another box
     * @param otherBox the other box to including in the bounding
     * @return the bounding box
     */
    public Box union(Box otherBox) {
        return new Box(rect.createUnion(otherBox.rect));
    }
}
