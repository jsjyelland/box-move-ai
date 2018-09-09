package solution;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class State {
    public MoveableBox goalBox;
//    public MoveableBox[] goalBoxes;
//    public MoveableBox[] movingObstacles;

    /**
     * New state with a moveable box
     * @param x box x
     * @param y box y
     * @param w box width
     */
    public State(double x, double y, double w) {
        this(new MoveableBox(x, y, w));
    }

    /**
     * New state with a moveable box object
     * @param goalBox the moveable box
     */
    public State(MoveableBox goalBox) {
        this.goalBox = goalBox;
    }

    /**
     * Construct a new state, checking if it is in collision
     * @param goalBox the moveable box
     * @param staticObstacles the static obstacles to check collision with
     * @throws InvalidStateException if the state is in collision
     */
    public State(MoveableBox goalBox, Box[] staticObstacles) throws InvalidStateException {
        this.goalBox = goalBox;

        if (!isValid(staticObstacles)) {
            throw new InvalidStateException();
        }
    }

    /**
     * Moving from one state to another. Must be either a horizontal or vertical line.
     * @param dx x distance to move goalBox by
     * @param dy y distance to move goalBox by
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
        State newState = new State(goalBox.clone());

        // Move the goal box
        newState.goalBox.move(dx, dy);

        // Create a union box encapsulating the old and new positions of the goal box
        Box union = goalBox.union(newState.goalBox);

        // Check if this union is valid
        if (!union.isValid(staticObstacles)) {
            throw new InvalidStateException();
        }

        return newState;
    }

    /**
     * Check if the state is valid given a list of static obstacles.
     * The state is valid if the moveable box doesn't collide with any of the
     * static obstacles and is inside the workspace.
     * @param staticObstacles list of static obstacles
     * @return whether the state is valid or not
     */
    public boolean isValid(Box[] staticObstacles) {
        // Check if the goalBox is valid
        return goalBox.isValid(staticObstacles);
    }

    /**
     * Clone the state
     * @return the cloned state
     */
    public State clone() {
        return new State(goalBox.clone());
    }

    /**
     * Compute the distance to another state (distance between moveable boxes)
     * @param other the other state to computer the distance to
     * @return the distance between the states
     */
    public double distanceTo(State other) {
        return goalBox.distanceTo(other.goalBox);
    }

    /**
     * Create a new state that is at most delta distance along the line
     * between this state and a new one.
     * Distance means straight line distance of the moveable box.
     * @param other the other state
     * @param delta the distance to move along the line between the two states
     * @return the new state
     */
    public State stepTowards(State other, double delta) {
        if (distanceTo(other) <= delta) {
            return other;
        }

        double theta = atan2(other.goalBox.getRect().getY() - goalBox.getRect().getY(),
                other.goalBox.getRect().getX() - goalBox.getRect().getX());

        return new State(goalBox.getRect().getX() + delta * cos(theta),
                goalBox.getRect().getY() + delta * sin(theta),
                goalBox.getRect().getWidth());
    }
}
