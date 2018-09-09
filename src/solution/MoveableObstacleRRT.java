package solution;

import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import static java.lang.Math.random;

/**
 * A rapidly exploring random tree interface
 */
public class MoveableObstacleRRT extends AbstractRRT{
    /**
     * Max distance a node can randomly expand doing RRT
     */
    private static double MAX_DISTANCE = 0.5;

    /**
     * Construct a MoveableObstacleRRT
     * @param staticObstacles the static obstacles
     * @param initialBox the initial box
     */
    public MoveableObstacleRRT(Box[] staticObstacles, MoveableBox[] moveableObstacles, MoveableBox initialBox)
            throws InvalidStateException {
        super(staticObstacles, moveableObstacles, initialBox);
        // TODO Probably need more here
    }

    /**
     * Expand the tree one step
     * @return if a solution is found or not
     */
    public boolean expand() {
        // Sample a random node in free space
        while (true) {
            try {
                double randX = random();
                double randY = random();

                State newRandomState = new State(
                        new MoveableBox(randX, randY, initialBox.getRect().getWidth()), null, null);

                // Get the nearest node to the new one
                TreeNode<State, Action> node = nearestNode(newRandomState);

                newRandomState.setStaticObstacles(node.getState().getStaticObstacles());
                newRandomState.setMoveableObstacles(node.getState().getMoveableObstacles());

                // Make sure this is valid
                newRandomState.validate();

                // Step towards the new random state up to MAX_DISTANCE
                State newState = node.getState().stepTowards(newRandomState, MAX_DISTANCE);

                // Make sure this is valid still
                newState.validate();

                // Add the new state to the tree
                TreeNode<State, Action> newNode = connectNodeToState(node, newState);

                // Try connecting this new state to the goal
                try {
                    // TODO, find if this new state is connected to any empty location, rather than a specific goal state
                    TreeNode<State, Action> goalNode = null; // this is temporary


                    solutionNode = goalNode;

                    if (true) { throw new InvalidStateException(); } // this is temporary

                    return true;
                } catch (InvalidStateException e) {
                    // Couldn't connect to the goal. Exit the loop
                    break;
                }
            } catch (InvalidStateException e) {
                // If this happens, try again. Means the new state is in collision
            }
        }

        // No solution was found
        return false;
    }

}
