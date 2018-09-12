package solution;

import java.util.ArrayList;

public abstract class State {
    /**
     * A list of the static obstacles in the workspace
     */
    protected ArrayList<Box> staticObstacles;

    /**
     * Construct a state
     *
     * @param staticObstacles a list of static obstacles
     */
    public State(ArrayList<Box> staticObstacles) {
        this.staticObstacles = staticObstacles;
    }

    /**
     * Check if the state is valid.
     *
     * @return whether the state is valid or not
     */
    public abstract boolean isValid();

    /**
     * Validate the state
     *
     * @throws InvalidStateException if the state is invalid
     */
    public void validate() throws InvalidStateException {
        if (!isValid()) {
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
     * @throws InvalidStateException if the new state is invalid
     */
    public abstract State stepTowards(State other, double delta) throws InvalidStateException;

    /**
     * Add a static obstacle
     *
     * @param newObstacle the obstacle to add
     */
    public void addStaticObstacle(Box newObstacle) {
        staticObstacles.add(newObstacle);
    }

    /**
     * Get the static obstacles
     *
     * @return the static obstacles
     */
    public ArrayList<Box> getStaticObstacles() {
        return staticObstacles;
    }

    /**
     * Set the static obstacles
     *
     * @param staticObstacles the static obstacles
     */
    public void setStaticObstacles(ArrayList<Box> staticObstacles) {
        this.staticObstacles = staticObstacles;
    }

    /**
     * Configure a state, given the nearest node in the search tree
     *
     * @param nearestNode the nearest node in the search tree
     * @param <T> the class of state
     * @param <U> the class of action
     */
    public abstract <T extends State, U extends Action> void configure(TreeNode<T, U> nearestNode);
}
