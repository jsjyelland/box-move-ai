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
    private MoveableBox initialBox;

    /**
     * The path to move the robot
     */
    protected ArrayList<RobotAction> robotPath;

    /**
     * The width of the robot
     */
    private double robotWidth;

    /**
     * Construct a MoveableBoxRRT
     *
     * @param staticObstacles the static obstacles
     * @param moveableObstacles the moveable obstacles
     * @param initialBox the box to move
     * @param robotWidth the width of the robot
     */
    public MoveableBoxRRT(ArrayList<Box> staticObstacles, ArrayList<MoveableBox> moveableObstacles,
            MoveableBox initialBox, double robotWidth) {
        this.initialBox = initialBox;
        this.robotWidth = robotWidth;

        // Make an initial tree
        tree = new TreeNode<>(new MoveableBoxState(initialBox, staticObstacles, moveableObstacles),
                null
        );

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
        if (checkMoveableBoxPath(newestNode)) {
            try {
                // Compute a path for the robot to move
                solveRobotPath(robotWidth);

                // Add in all the paths required to move moveable obstacles at the beginning
                ArrayList<RobotAction> robotPaths = moveMoveableObstacles();

                if (robotPaths != null && robotPaths.size() > 0) {
                    // Move the robot from the end of the moveable obstacle paths to the beginning
                    // of the path for the main box
                    RobotRRT rrt = new RobotRRT(
                            getTopLevelSolution().getState().getStaticObstacles(),
                            robotPaths.get(robotPaths.size() - 1).getFinalRobot(),
                            robotPath.get(0).getInitialRobot(),
                            robotPath.get(0).getInitialBoxPushing()
                    );

                    if (rrt.solve()) {
                        robotPath.addAll(0, rrt.getSolution().actionPathFromRoot());
                        robotPath.addAll(0, robotPaths);
                    } else {
                        throw new NoPathException();
                    }
                }

                return true;
            } catch (NoPathException e) {
                // Robot can't do it, no solution
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Check if the path for a moveable box is valid
     *
     * @param newestNode
     *
     * @return
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
                            node, new MoveableBoxState(
                                    new MoveableBox(stateX, nodeY,
                                            state.getMainBox().getRect().getWidth()
                                    ),
                                    node.getState().getStaticObstacles(),
                                    node.getState().getMoveableObstacles()
                            ),
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
                            node, new MoveableBoxState(
                                    new MoveableBox(nodeX, stateY,
                                            state.getMainBox().getRect().getWidth()
                                    ),
                                    node.getState().getStaticObstacles(),
                                    node.getState().getMoveableObstacles()
                            ), false);

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
     * @return a list of the robot actions required to move the obstacles. Returns null if no
     * solution has been found
     */
    public ArrayList<RobotAction> moveMoveableObstacles() throws NoPathException {
        // Make sure a solution has been found
        if (solutionNode == null) {
            return null;
        }

        // Loop through the solution path
        TreeNode<MoveableBoxState, MoveableBoxAction> currentNode = solutionNode;

        ArrayList<RobotAction> robotPaths = new ArrayList<>();

        Robot previousRobotPosition = null;

        while (currentNode.getParent() != null) {
            // Move the moveable obstacles out of the way, and attach a visualiser if this RRT
            // has one attached.
            ArrayList<RobotAction> obstacleRobotPath = currentNode.getAction().moveBoxesOutOfPath(
                    getSolutionLeaves(), visualiserAttached(), robotWidth
            );

            if (obstacleRobotPath.size() > 0) {
                if (previousRobotPosition != null) {
                    // Move the robot from the end of the last moveable obstacle path to the
                    // beginning of the next
                    RobotRRT rrt = new RobotRRT(
                            getTopLevelSolution().getState().getStaticObstacles(),
                            previousRobotPosition,
                            obstacleRobotPath.get(0).getInitialRobot(),
                            obstacleRobotPath.get(0).getInitialBoxPushing()
                    );

                    if (rrt.solve()) {
                        // Add the path to get to the start of the box moving path, and the bo
                        // moving path
                        robotPaths.addAll(rrt.getSolution().actionPathFromRoot());
                        robotPaths.addAll(obstacleRobotPath);
                    } else {
                        throw new NoPathException();
                    }
                }

                // Set the previous robot position to the end of the latest path
                previousRobotPosition = obstacleRobotPath.get(
                        obstacleRobotPath.size() - 1
                ).getFinalRobot();
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
                new MoveableBox(random(), random(), initialBox.getRect().getWidth()), null, null
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
     * Gets the top level solution node
     *
     * @return the top level solution node
     */
    protected TreeNode<MoveableBoxState, MoveableBoxAction> getTopLevelSolution() {
        return getSolutionLeaves().get(getSolutionLeaves().size() - 1);
    }

    /**
     * Gets the deepest solution node
     *
     * @return the deepest solution node
     */
    protected TreeNode<MoveableBoxState, MoveableBoxAction> getDeepestSolution() {
        return getSolutionLeaves().get(0);
    }

    /**
     * Compute a robot path for the solution.
     *
     * @param robotWidth the width of the robot
     *
     * @throws NoPathException if a path could not be computed
     */
    protected void solveRobotPath(double robotWidth)
            throws NoPathException {
        robotPath = new ArrayList<>();

        Robot previousRobotPosition = null;

        // Compute all the paths for each action
        for (MoveableBoxAction action : getTopLevelSolution().actionPathFromRoot()) {
            // Compute the path
            action.solveRobotPath(getTopLevelSolution().getState().getStaticObstacles(),
                    previousRobotPosition, robotWidth
            );

            // Add the path
            robotPath.addAll(action.getRobotPath());

            previousRobotPosition = robotPath.get(robotPath.size() - 1).getFinalRobot();
        }
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
     * Get the initial box
     *
     * @return the initial box
     */
    public MoveableBox getInitialBox() {
        return initialBox;
    }
}
