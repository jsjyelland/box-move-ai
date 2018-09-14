package solution;

import java.util.ArrayList;

import static java.lang.Math.PI;
import static java.lang.Math.random;

/**
 * An rapidly exploring random tree for the robot
 */
public class RobotRRT extends RRT<RobotState, RobotAction> {
    /**
     * The initial robot
     */
    private Robot initialRobot;

    /**
     * The goal robot
     */
    private Robot goalRobot;

    /**
     * Construct a RobotRRT
     *
     * @param staticObstacles the static obstacles
     * @param initialRobot the initial robot
     * @param boxToPush the box the robot is pushing
     */
    public RobotRRT(ArrayList<Box> staticObstacles, Robot initialRobot, Robot goalRobot,
            Box boxToPush)
            throws NoPathException {
        if (!initialRobot.isValid(staticObstacles) || !goalRobot.isValid(staticObstacles)) {
            throw new NoPathException();
        }

        this.initialRobot = initialRobot;
        this.goalRobot = goalRobot;

        staticObstacles = new ArrayList<>(staticObstacles);
        if (boxToPush != null) {
            staticObstacles.add(boxToPush);
        }

        // Make an initial tree
        tree = new TreeNode<>(new RobotState(initialRobot, staticObstacles), null);

        // Add the root to nodes
        nodes.add(tree);
    }

    /**
     * Check to see if the current tree has a solution
     *
     * @return if a solution is found or not
     */
    @Override
    protected boolean checkSolution(TreeNode<RobotState, RobotAction> newestNode) {
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
     * Attempt to connect a node to a state
     *
     * @param node the parent node
     * @param state the child state
     * @param addChild whether to add the new node to the tree or not
     *
     * @return a new node containing the child state. Will return node if they are in the same
     * place.
     *
     * @throws InvalidStateException if this is not possible
     */
    @Override
    protected TreeNode<RobotState, RobotAction> connectNodeToState(
            TreeNode<RobotState, RobotAction> node, RobotState state, boolean addChild)
            throws InvalidStateException {
        double nodeX = node.getState().getRobot().getPos().getX();
        double nodeY = node.getState().getRobot().getPos().getY();
        double nodeTheta = node.getState().getRobot().getTheta();

        double stateX = state.getRobot().getPos().getX();
        double stateY = state.getRobot().getPos().getY();
        double stateTheta = state.getRobot().getTheta();

        double dx = stateX - nodeX;
        double dy = stateY - nodeY;
        double dtheta = stateTheta - nodeTheta;

        if (!(dx == 0 && dy == 0 && dtheta == 0)) {
            // Check if the action is valid. Will throw an
            // InvalidStateException if not.
            TreeNode<RobotState, RobotAction> newNode = node.getState().action(dx, dy, dtheta);

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
     * Generate a new random state
     *
     * @return a new random state
     */
    @Override
    protected RobotState newRandomState() {
        return new RobotState(
                new Robot(random(), random(), random() * 2 * PI, initialRobot.getWidth()), null
        );
    }
}
