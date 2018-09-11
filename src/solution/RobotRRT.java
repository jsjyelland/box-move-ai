package solution;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.random;
import static solution.Util.TO_RADIANS;
import static solution.Util.randomTo;

/**
 * An rapidly exploring random tree for the robot
 */
public class RobotRRT {
    /**
     * Max distance a node can randomly expand doing RRT
     */
    private static double MAX_DISTANCE = 0.5;

    /**
     * The tree of states
     */
    private TreeNodeSingle<RobotState> tree;

    /**
     * List of all the nodes
     */
    private ArrayList<TreeNodeSingle<RobotState>> nodes;

    /**
     * The solution node for this RRT.
     */
    protected TreeNodeSingle<RobotState> solutionNode = null;

    /**
     * The initial robot
     */
    private Robot initialRobot;

    /**
     * The goal robot
     */
    private Robot goalRobot;

    /**
     * Construct an RRT
     *
     * @param staticObstacles the static obstacles
     * @param initialRobot the initial robot
     */
    public RobotRRT(ArrayList<Box> staticObstacles, Robot initialRobot, Robot goalRobot) {
        this.initialRobot = initialRobot;
        this.goalRobot = goalRobot;

        // Make an initial tree
        tree = new TreeNodeSingle<>(new RobotState(initialRobot, staticObstacles));

        // List of all the nodes
        nodes = new ArrayList<>();
        nodes.add(tree);
    }

    /**
     * Expand the tree one step. This function has to be implemented by the subclasses
     *
     * @return if a solution is found or not
     */
    public boolean expand() {
        // Sample a random node in free space
        while (true) {
            try {
                double randX = random();
                double randY = random();
                double randTheta = randomTo(360) / TO_RADIANS;


                RobotState newRandomState = new RobotState(
                        new Robot(randX, randY, randTheta, initialRobot.getWidth()), null
                );

                // Get the nearest node to the new one
                TreeNodeSingle<RobotState> node = nearestNode(newRandomState);

                newRandomState.setStaticObstacles(node.getState().getStaticObstacles());

                // Make sure this is valid
                newRandomState.validate();

                // Step towards the new random state up to MAX_DISTANCE
                RobotState newState = node.getState().stepTowards(newRandomState, MAX_DISTANCE);

                // Make sure this is valid still
                newState.validate();

                // Make the new node
                TreeNodeSingle<RobotState> newNode = connectNodeToState(node, newState, true);

                // Try connecting this new state to the goal
                return checkSolution(newNode);
            } catch (InvalidStateException e) {
                // If this happens, try again. Means the new state is in collision
            }
        }
    }

    /**
     * Check to see if the current tree has a solution
     *
     * @return if a solution is found or not
     */
    private boolean checkSolution(TreeNodeSingle<RobotState> newestNode) {
        try {
            // Try to connect to the goal
            solutionNode = connectNodeToState(newestNode, new RobotState(
                    goalRobot,
                    newestNode.getState().getStaticObstacles()
            ), true);

            return true;
        } catch (InvalidStateException e) {
            // Couldn't connect to the goal. Exit the loop
            return false;
        }
    }

    /**
     * Find the nearest node in the tree to a given state. Uses straight line distance
     *
     * @param state the state to find the node nearest to
     *
     * @return the nearest node
     */
    private TreeNodeSingle<RobotState> nearestNode(RobotState state) {
        TreeNodeSingle<RobotState> bestNode = nodes.get(0);
        double shortestDistance = state.distanceTo(bestNode.getState());

        for (TreeNodeSingle<RobotState> node : nodes) {
            double distance = node.getState().distanceTo(state);

            if (distance < shortestDistance) {
                shortestDistance = distance;
                bestNode = node;
            }
        }

        return bestNode;
    }

    /**
     * Add a child node to a parent, and also add to the list of nodes.
     *
     * @param parent the parent node
     * @param child the child node
     */
    private void addChildNode(TreeNodeSingle<RobotState> parent, TreeNodeSingle<RobotState> child) {
        parent.addChild(child);
        nodes.add(child);
    }

    private TreeNodeSingle<RobotState> connectNodeToState(TreeNodeSingle<RobotState> node, RobotState state,
                                                          boolean addChild) throws InvalidStateException {
        double nodeX = node.getState().getRobot().getPos().getX();
        double nodeY = node.getState().getRobot().getPos().getX();
        double nodeTheta = node.getState().getRobot().getTheta();

        double stateX = state.getRobot().getPos().getX();
        double stateY = state.getRobot().getPos().getX();
        double stateTheta = state.getRobot().getTheta();

        double dx = stateX - nodeX;
        double dy = stateY - nodeY;
        double dtheta = stateTheta - nodeTheta;

        if (!(dx == 0 && dy == 0 && dtheta == 0)) {
            // Check if the action is valid. Will throw an
            // InvalidStateException if not.
            TreeNodeSingle<RobotState> newNode = node.getState().action(dx, dy, dtheta);

            // Add the new node to the tree
            if (addChild) {
                addChildNode(node, newNode);
            }

            return newNode;
        }

        // They're already in the same place. Just return the node
        return node;
    }

    /**
     * Gets the solution node
     *
     * @return the solution node
     */
    public TreeNodeSingle<RobotState> getSolution() {
        return solutionNode;
    }

    /**
     * Gets the tree
     *
     * @return the tree
     */
    public TreeNodeSingle<RobotState> getTree() {
        return tree;
    }
}
