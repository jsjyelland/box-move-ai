package solution;

import java.util.ArrayList;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * A state object, containing the location of a box needing to be moved.
 * Could be a goal box or a regular moveable box
 */
public class State {
    /**
     * The box to move
     */
    private MoveableBox mainBox;

    /**
     * A list of the static obstacles in the workspace
     */
    private Box[] staticObstacles;

    /**
     * A list of moveable obstacles in the workspace
     */
    private MoveableBox[] moveableObstacles;

    /**
     * Construct a new state, checking if it is in collision
     * @param mainBox the moveable box
     * @param staticObstacles the static obstacles to check collision with
     */
    public State(MoveableBox mainBox, Box[] staticObstacles, MoveableBox[] moveableObstacles) {
        this.staticObstacles = staticObstacles;
        this.moveableObstacles = moveableObstacles;
        this.mainBox = mainBox;
    }

    /**
     * Moving from one state to another. Must be either a horizontal or vertical line.
     * @param dx x distance to move mainBox by
     * @param dy y distance to move mainBox by
     * @param staticObstacles the static obstacles
     * @return the new state
     * @throws InvalidStateException if the new state is invalid, or if the move is in two directions.
     */
    public State action(double dx, double dy, Box[] staticObstacles)
            throws InvalidStateException {
        if (dx != 0 && dy != 0) {
            throw new InvalidStateException();
        }

        // Clone this state
        State newState = clone();

        // Move the goal box
        newState.mainBox.move(dx, dy);

        // Create a union box encapsulating the old and new positions of the goal box
        Box union = mainBox.union(newState.mainBox);

        // Check if this union is valid
        if (!union.isValid(staticObstacles)) {
            throw new InvalidStateException();
        }

        return newState;
    }

    /**
     * Check if the state is valid.
     * The state is valid if the moveable box doesn't collide with any of the
     * static obstacles and is inside the workspace.
     * @return whether the state is valid or not
     */
    public boolean isValid() {
        // Check if the mainBox is valid
        return mainBox.isValid(staticObstacles);
    }

    /**
     * Validate the state
     * @throws InvalidStateException if the state is invalid
     */
    public void validate() throws InvalidStateException {
        if (!isValid()) {
            throw new InvalidStateException();
        }
    }

    /**
     * Clone the state
     * @return the cloned state
     */
    public State clone() {
        return new State(mainBox.clone(), staticObstacles, moveableObstacles);
    }

    /**
     * Compute the distance to another state (distance between moveable boxes)
     * @param other the other state to computer the distance to
     * @return the distance between the states
     */
    public double distanceTo(State other) {
        return mainBox.distanceTo(other.mainBox);
    }

    /**
     * Create a new state that is at most delta distance along the line
     * between this state and a new one.
     * Distance means straight line distance of the moveable box.
     * @param other the other state
     * @param delta the distance to move along the line between the two states
     * @return the new state
     */
    public State stepTowards(State other, double delta) throws InvalidStateException {
        if (distanceTo(other) <= delta) {
            return other;
        }

        double theta = atan2(other.mainBox.getRect().getY() - mainBox.getRect().getY(),
                other.mainBox.getRect().getX() - mainBox.getRect().getX());

        State newState = clone();

        MoveableBox newMainBox = new MoveableBox(mainBox.getRect().getX() + delta * cos(theta),
                mainBox.getRect().getY() + delta * sin(theta),
                mainBox.getRect().getWidth());

        newState.setMainBox(newMainBox);

        return newState;
    }

    /**
     * Set the main box. Note that this will change the main box regardless of whether the new
     * state is valid or not.
     * @param mainBox the main box
     * @throws InvalidStateException if the state given the new main box is invalid
     */
    public void setMainBox(MoveableBox mainBox) throws InvalidStateException {
        this.mainBox = mainBox;

        if (!isValid()) {
            throw new InvalidStateException();
        }
    }

    /**
     * Get the main box
     * @return the main box
     */
    public MoveableBox getMainBox() {
        return mainBox;
    }

    /**
     * Get the static obstacles
     * @return the static obstacles
     */
    public Box[] getStaticObstacles() {
        return staticObstacles;
    }

    /**
     * Get the moveable obstacles
     * @return the moveable obstacles
     */
    public MoveableBox[] getMoveableObstacles() {
        return moveableObstacles;
    }

    /**
     * Set the static obstacles
     * @param staticObstacles the static obstacles
     */
    public void setStaticObstacles(Box[] staticObstacles) {
        this.staticObstacles = staticObstacles;
    }

    /**
     * Set the moveable obstacles
     * @param moveableObstacles the moveable obstacles
     */
    public void setMoveableObstacles(MoveableBox[] moveableObstacles) {
        this.moveableObstacles = moveableObstacles;
    }
}
