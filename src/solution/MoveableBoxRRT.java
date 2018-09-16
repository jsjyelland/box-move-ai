package solution;

import java.util.ArrayList;

import static java.lang.Math.random;

/**
 * An RRT for moving any moveable box
 */
public abstract class MoveableBoxRRT extends RRT<MoveableBoxState, MoveableBoxAction> {
    /**
     * The initial box
     */
    protected MoveableBox initialBox;

    /**
     * The path to move the robot
     */
    protected ArrayList<RobotAction> robotPath;

    /**
     * The robot's starting position
     */
    protected Robot robotStartingPosition;

    /**
     * Construct a MoveableBoxRRT
     *
     * @param initialBox the box to move
     * @param robotStartingPosition the robot's starting position
     */
    public MoveableBoxRRT(MoveableBox initialBox, Robot robotStartingPosition) {
        this.initialBox = initialBox;
        this.robotStartingPosition = robotStartingPosition;

        // Make an initial tree
        tree = new TreeNode<>(new MoveableBoxState(initialBox), null);

        // Add the root to nodes
        nodes.add(tree);
    }

    /**
     * Check if a solution is valid
     *
     * @param newestNode the most recent node added to the tree
     *
     * @return whether the solution is valid or not
     */
    @Override
    protected boolean checkSolution(TreeNode<MoveableBoxState, MoveableBoxAction> newestNode) {
        return checkMoveableBoxPath(newestNode) && finishSolution();
    }

    /**
     * Perform any actions after finding a solution to the RRT
     *
     * @return whether this was successful or not
     */
    protected abstract boolean finishSolution();

    /**
     * Check if the path for a moveable box is valid
     *
     * @param newestNode the latest node added to the tree
     *
     * @return whether the path is valid or not
     */
    protected abstract boolean checkMoveableBoxPath(
            TreeNode<MoveableBoxState, MoveableBoxAction> newestNode);

    /**
     * Attempt to connect a node to a state using only horizontal or vertical lines. Will only
     * attempt two movements (e.g. up then right).
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
    protected TreeNode<MoveableBoxState, MoveableBoxAction> connectNodeToState(
            TreeNode<MoveableBoxState, MoveableBoxAction> node, MoveableBoxState state,
            boolean addChild) throws InvalidStateException {
        double nodeX = node.getState().getMainBox().getRect().getX();
        double nodeY = node.getState().getMainBox().getRect().getY();
        double stateX = state.getMainBox().getRect().getX();
        double stateY = state.getMainBox().getRect().getY();

        double dx = stateX - nodeX;
        double dy = stateY - nodeY;

        if (!(dx == 0 && dy == 0)) {
            if (dx == 0 || dy == 0) {
                // Only requires one movement to get to child

                // Check if the action is valid. Will throw an
                // InvalidStateException if not.
                TreeNode<MoveableBoxState, MoveableBoxAction> newNode = node.getState().action(
                        dx, dy
                );

                // Add the new node to the tree
                if (addChild) {
                    addChildNode(node, newNode);
                }

                return newNode;
            } else {
                // Requires two movements to get to child. Call connectNodes
                // again with the parent being a new node in between the current
                // parent and child. Try both routes.

                try {
                    // First attempt. Corner node with (stateX, nodeY).
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<MoveableBoxState, MoveableBoxAction> cornerNode = connectNodeToState(
                            node, new MoveableBoxState(new MoveableBox(stateX, nodeY,
                                    state.getMainBox().getRect().getWidth()
                            )),
                            false
                    );

                    // Now connect this corner node to the state.
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<MoveableBoxState, MoveableBoxAction> endNode = connectNodeToState(
                            cornerNode, state, true
                    );

                    // Add the corner node as a child of the parent node
                    addChildNode(node, cornerNode);

                    return endNode;
                } catch (InvalidStateException e) {
                    // Second attempt. Corner node with (nodeX, stateY).
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<MoveableBoxState, MoveableBoxAction> cornerNode = connectNodeToState(
                            node, new MoveableBoxState(new MoveableBox(nodeX, stateY,
                                    state.getMainBox().getRect().getWidth()
                            )),
                            false
                    );

                    // Now connect this corner node to the state.
                    // Will throw an InvalidStateException if it fails.
                    TreeNode<MoveableBoxState, MoveableBoxAction> endNode = connectNodeToState(
                            cornerNode, state, true
                    );

                    // Add the corner node as a child of the parent node
                    addChildNode(node, cornerNode);

                    return endNode;
                }
            }
        }

        // They're already in the same place. Just return the node
        return node;
    }

    /**
     * Move moveable obstacles out of the way of the solution path
     *
     * @param previousRobotPosition the starting position of the robot
     *
     * @return a list of the robot actions required to move the obstacles. Returns null if no
     * solution has been found
     */
    protected ArrayList<RobotAction> moveMoveableObstacles(Robot previousRobotPosition)
            throws NoPathException {
        // Make sure a solution has been found
        if (solutionNode == null) {
            return new ArrayList<>();
        }

        // Loop through the solution path
        TreeNode<MoveableBoxState, MoveableBoxAction> currentNode = solutionNode;

        ArrayList<RobotAction> robotPaths = new ArrayList<>();

        while (currentNode.getParent() != null) {
            // Move the moveable obstacles out of the way, and attach a visualiser if this RRT
            // has one attached.
            ArrayList<RobotAction> obstacleRobotPath = currentNode.getAction().moveBoxesOutOfPath(
                    getSolutionLeaves(), previousRobotPosition, visualiserAttached()
            );

            if (obstacleRobotPath.size() > 0) {
                // Set the previous robot position to the end of the latest path
                previousRobotPosition = obstacleRobotPath.get(
                        obstacleRobotPath.size() - 1
                ).getFinalRobot();

                robotPaths.addAll(obstacleRobotPath);
            }

            // Move up the tree
            currentNode = currentNode.getParent();
        }

        return robotPaths;
    }

    /**
     * Generate a new random state
     *
     * @return a new random state
     */
    @Override
    protected MoveableBoxState newRandomState() {
        return new MoveableBoxState(
                new MoveableBox(random(), random(), initialBox.getRect().getWidth())
        );
    }

    /**
     * Gets a list of the leaf nodes of all solutions including parent RRTs. Array index 0 is the
     * deepest level. Increasing index means decreasing deepness.
     *
     * @return the list of solution leaf nodes
     */
    protected abstract ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> getSolutionLeaves();

    /**
     * Compute a robot path for the solution.
     *
     * @param previousRobotPosition the starting position of the robot
     *
     * @return the path for the robot
     *
     * @throws NoPathException if a path could not be computed
     */
    public ArrayList<RobotAction> solveRobotPath(Robot previousRobotPosition)
            throws NoPathException {
        pushBoxInWorkspace(initialBox);

        ArrayList<RobotAction> robotPaths = new ArrayList<>();

        // Compute all the paths for each action
        for (MoveableBoxAction action : solutionNode.actionPathFromRoot()) {
            // Compute the path
            action.solveRobotPath(previousRobotPosition);

            // Add the path
            robotPaths.addAll(action.getRobotPath());

            previousRobotPosition = robotPaths.get(robotPaths.size() - 1).getFinalRobot();
        }

        finishPushBoxInWorkspace(solutionNode.getState().getMainBox());

        RobotAction finalAction = robotPaths.get(robotPaths.size() - 1);
        robotPaths.add(new RobotAction(finalAction.getFinalRobot(), finalAction.getInitialRobot()));

        return robotPaths;
    }

    /**
     * Get the robot path to move any moveable boxes out of the way and then move the main box.
     *
     * @return the robot path
     */
    public ArrayList<RobotAction> getRobotPath() {
        return robotPath;
    }

    /**
     * Push the box in the workspace
     *
     * @param boxToPush the box to push
     */
    protected abstract void pushBoxInWorkspace(MoveableBox boxToPush);

    /**
     * Finish pushing the box in the workspace
     *
     * @param newPosition the new position of the box
     */
    protected abstract void finishPushBoxInWorkspace(MoveableBox newPosition);

    /**
     * Validate a state
     *
     * @param state the state to validate
     *
     * @throws InvalidStateException if the state is invalid
     */
    @Override
    protected void validateState(MoveableBoxState state) throws InvalidStateException {
        state.validate();
    }
}
