package solution;

import java.awt.geom.Rectangle2D;

/**
 * A moveable box. Used for both the moveable obstacles and the goal boxes
 */
public class MoveableBox extends Box {
    /**
     * Construct a moveable box with rectangle parameters. Note that in the assignment spec,
     * any moveable box is a square and so only requires a width.
     * @param x box x
     * @param y box y
     * @param w box w
     */
    public MoveableBox(double x, double y, double w) {
        super(x, y, w, w);
    }

    /**
     * Construct a moveable box with a rectangle object
     * @param rect the rectangle to use for the box.
     * @throws BoxSizeException if the rectangle is not a square (height == width)
     */
    public MoveableBox(Rectangle2D rect) throws BoxSizeException {
        super(rect);

        // Make sure the rect is a square
        if (rect.getWidth() != rect.getHeight()) {
            throw new BoxSizeException();
        }
    }

    /**
     * Move the box
     * @param dx change in x
     * @param dy change in y
     */
    public void move(double dx, double dy) {
        rect.setRect(rect.getX() + dx, rect.getY() + dy, rect.getWidth(), rect.getHeight());
    }

    /**
     * Clone a movable box
     * @return the cloned box
     */
    public MoveableBox clone() {
        try {
            return new MoveableBox((Rectangle2D.Double) rect.clone());
        } catch (BoxSizeException e){
            // This will not happen because the rect will be a square. If this object is valid, so will the clone
            return new MoveableBox(0, 0, 0);
        }
    }
}
