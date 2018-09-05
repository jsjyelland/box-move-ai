package solution;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.round;
import static java.lang.Math.random;

public class Main {
    /**
     * Max distance a node can randomly expand doing RRT
     */
    public static double MAX_DISTANCE = 0.1;

    public static void main(String[] args) {
        Box[] staticObstacles = new Box[1];
        staticObstacles[0] = new Box(0.5, 0.5, 0.1, 0.1);
        State initialState = new State(0.2, 0.2, 0.01);
        State goalState = new State(0.8, 0.8, 0.01);

        // Create a visualiser for the tree
        Visualiser visualiser = new Visualiser(staticObstacles);
        Window window = new Window(visualiser);

        // Make an initial tree
        TreeNode<State> tree = new TreeNode<>(initialState);
        // List of all the nodes
        ArrayList<TreeNode<State>> nodes = new ArrayList<>();
        nodes.add(tree);

        // Loop until a solution is found
        while (true) {
            // Pick a random node to expand
            int randIndex = randomTo(nodes.size() - 1);
            TreeNode<State> randomNode = nodes.get(randIndex);
            State state = randomNode.getValue();

            // Find an action that is valid
            State newState;
            while (true) {
                try {
                    // Move in a random direction with a random length
                    newState = state.action(
                            MoveDirection.values()[randomTo(MoveDirection.values().length - 1)],
                            random() * MAX_DISTANCE,
                            staticObstacles);
                    break;
                } catch (InvalidStateException e) {
                    // If this happens, try again. Means the state is in collision
                }
            }

            // Add the new state to the tree
            TreeNode<State> newNode = new TreeNode<>(newState);
            randomNode.addChild(newNode);
            nodes.add(newNode);

            // Visualise the new tree
            visualiser.paintTree(tree);

            // If the goal is close enough to the new node, try to connect to it
            if (newState.goalBox.distanceTo(goalState.goalBox) < MAX_DISTANCE) {
                // Try all the directions
                for (MoveDirection direction : MoveDirection.values()) {
                    State
                }
            }

            // Wait because this is too fast to watch
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Helper function for random number between two bounds
     * @param a lower bound
     * @param b upper bound
     * @return a random number
     */
    public static int randomBetween(int a, int b) {
        return a + round((float) random() * (float) (b - a));
    }

    /**
     * Helper function for a random number up to a number (from 0)
     * @param a the upper bound
     * @return a random number
     */
    public static int randomTo(int a) {
        return randomBetween(0, a);
    }
}
