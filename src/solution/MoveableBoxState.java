package solution;

import java.util.ArrayList;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * A state object, containing the location of a box needing to be moved. Could be a goal box or a
 * regular moveable box
 */
public class MoveableBoxState extends State {
    /**
     * The box to move
     */
    private MoveableBox mainBox;

    /**
     * Construct a new state
     *
     * @param mainBox the moveable box
     */
    public MoveableBoxState(MoveableBox mainBox) {
        this.mainBox = mainBox;
    }

    /**
     * Moving from one state to another. Must be either a horizontal or vertical line. Will move any
     * moveable obstacles out of the way of the path represented by currentLeaf.
     *
     * @param dx x distance to move mainBox by
     * @param dy y distance to move mainBox by
     * @param obstacles the obstacles to avoid
     *
     * @return a new node containing the new state and the action to get to this state
     *
     * @throws InvalidStateException if the new state is invalid, or if the move is in two
     * directions.
     */
    public TreeNode<MoveableBoxState, MoveableBoxAction> action(double dx, double dy,
            ArrayList<Box> obstacles) throws InvalidStateException {
        // Make sure the action is only in one direction
        if (dx != 0 && dy != 0) {
            throw new InvalidStateException();
        }

        // Clone this state
        MoveableBoxState newState = clone();

        // Move the goal box
        newState.mainBox.move(dx, dy);

        // Create a union box encapsulating the old and new positions of the goal box
        Box union = mainBox.union(newState.mainBox);

        // Check if this union is valid
        if (!union.isValid(obstacles)) {
            throw new InvalidStateException();
        }

        // Create and return a new node with this new state and an action
        return new TreeNode<>(newState, new MoveableBoxAction(
                mainBox, newState.mainBox
        ));
    }

    /**
     * Check if the state is valid. The state is valid if the moveable box doesn't collide with any
     * of the static obstacles and is inside the workspace.
     *
     * @param obstacles the obstacles to avoid
     *
     * @return whether the state is valid or not
     */
    @Override
    public boolean isValid(ArrayList<Box> obstacles) {
        // Check if the mainBox is valid
        return mainBox.isValid(obstacles);
    }

    /**
     * Clone the state
     *
     * @return the cloned state
     */
    @Override
    public MoveableBoxState clone() {
        return new MoveableBoxState(mainBox.clone());
    }

    /**
     * Compute the distance to another state (distance between moveable boxes)
     *
     * @param other the other state to compute the distance to
     *
     * @return the distance between the states if other is a MoveableBoxState. -1 otherwise.
     */
    @Override
    public double distanceTo(State other) {
        if (other instanceof MoveableBoxState) {
            MoveableBoxState moveableBoxState = (MoveableBoxState) other;
            return mainBox.distanceTo(moveableBoxState.mainBox);
        }

        return -1;
    }

    /**
     * Create a new state that is at most delta distance along the line between this state and a new
     * one. Distance means straight line distance of the moveable box.
     *
     * @param other the other state
     * @param delta the distance to move along the line between the two states
     *
     * @return the new state
     *
     * @throws InvalidStateException if other is not a MoveableBoxState
     */
    @Override
    public MoveableBoxState stepTowards(State other, double delta) throws InvalidStateException {
        // Make sure other is a MoveableBoxState
        if (!(other instanceof MoveableBoxState)) {
            throw new InvalidStateException();
        }

        MoveableBoxState moveableBoxState = (MoveableBoxState) other;

        // No need to do anything
        if (distanceTo(other) <= delta) {
            return (MoveableBoxState) other;
        }

        // The angle to move
        double theta = atan2(moveableBoxState.mainBox.getRect().getY() - mainBox.getRect().getY(),
                moveableBoxState.mainBox.getRect().getX() - mainBox.getRect().getX()
        );

        MoveableBoxState newState = clone();

        // Move along the line with direction theta by distance delta
        MoveableBox newMainBox = new MoveableBox(mainBox.getRect().getX() + delta * cos(theta),
                mainBox.getRect().getY() + delta * sin(theta),
                mainBox.getRect().getWidth()
        );

        newState.setMainBox(newMainBox);

        return newState;
    }

    /**
     * Set the main box.
     *
     * @param mainBox the main box
     */
    private void setMainBox(MoveableBox mainBox) {
        this.mainBox = mainBox;
    }

    /**
     * Get the main box
     *
     * @return the main box
     */
    public MoveableBox getMainBox() {
        return mainBox;
    }
}
