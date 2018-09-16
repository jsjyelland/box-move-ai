package solution;

import java.util.ArrayList;

public abstract class State {
    /**
     * Check if the state is valid.
     *
     * @param obstacles the obstacles to avoid
     *
     * @return whether the state is valid or not
     */
    public abstract boolean isValid(ArrayList<Box> obstacles);

    /**
     * Validate the state
     *
     * @throws InvalidStateException if the state is invalid
     */
    public void validate(ArrayList<Box> obstacles) throws InvalidStateException {
        if (!isValid(obstacles)) {
            throw new InvalidStateException();
        }
    }

    /**
     * Compute the distance to another state
     *
     * @param other the other state to compute the distance to
     *
     * @return the distance between the states
     */
    public abstract double distanceTo(State other);

    /**
     * Create a new state that is at most delta distance along the line between this state and a new
     * one.
     *
     * @param other the other state
     * @param delta the distance to move along the line between the two states
     *
     * @return the new state
     *
     * @throws InvalidStateException if other does not conform to the subclass type
     */
    public abstract State stepTowards(State other, double delta) throws InvalidStateException;
}
