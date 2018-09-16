package solution;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A moveable box. Used for both the moveable obstacles and the goal boxes
 */
public class MoveableBox extends Box {
    /**
     * Construct a moveable box with rectangle parameters. Note that in the assignment spec, any
     * moveable box is a square and so only requires a width.
     *
     * @param x box x
     * @param y box y
     * @param w box w
     */
    public MoveableBox(double x, double y, double w) {
        super(x, y, w, w);
    }

    /**
     * Construct a moveable box with a rectangle object
     *
     * @param rect the rectangle to use for the box.
     */
    public MoveableBox(Rectangle2D rect) {
        super(rect);
    }

    /**
     * Construct a moveable box with a position and width
     *
     * @param pos the position
     * @param w the width
     */
    public MoveableBox(Point2D pos, double w) {
        super(pos, w, w);
    }

    /**
     * Move the box
     *ArrayList<MoveableBox> moveableBoxGoalPositions
     * @param dx change in x
     * @param dy change in y
     */
    public void move(double dx, double dy) {
        rect.setRect(rect.getX() + dx, rect.getY() + dy, rect.getWidth(), rect.getHeight());
    }

    /**
     * Clone a movable box
     *
     * @return the cloned box
     */
    @Override
    public MoveableBox clone() {
        return new MoveableBox((Rectangle2D.Double) rect.clone());
    }
}
