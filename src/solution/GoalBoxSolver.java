package solution;

import problem.Box;
import problem.ProblemSpec;
import problem.RobotConfig;
import problem.StaticObstacle;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

public class GoalBoxSolver {
    /**
     * The start positions of the goal boxes
     */
    private ArrayList<MoveableBox> goalBoxes;

    /**
     * The final positions of the goal boxes
     */
    private ArrayList<MoveableBox> goalBoxGoalPositions;

    /**
     * The start positions of the moveable obstacles
     */
    private ArrayList<MoveableBox> moveableObstacles;

    /**
     * The starting position of the robot
     */
    private Robot robotStartingPosition;

    /**
     * Construct a goal box solver with a problem file
     *
     * @param filename the filename to load the problem from
     *
     * @throws IOException if the file could not be loaded
     */
    public GoalBoxSolver(String filename) throws IOException {
        // Load the file
        ProblemSpec ps = new ProblemSpec();
        ps.loadProblem(filename);

        // Load the goal boxes
        goalBoxes = new ArrayList<>();

        for (Box box : ps.getMovingBoxes()) {
            goalBoxes.add(new MoveableBox(box.getRect()));
        }

        // Load the goal box goal positions
        goalBoxGoalPositions = new ArrayList<>();

        for (Point2D point : ps.getMovingBoxEndPositions()) {
            goalBoxGoalPositions.add(new MoveableBox(point, ps.getRobotWidth()));
        }

        // Load the moveable obstacles
        moveableObstacles = new ArrayList<>();

        for (Box box : ps.getMovingObstacles()) {
            moveableObstacles.add(new MoveableBox(box.getRect()));
        }

        // Load the static obstacles
        ArrayList<solution.Box> staticObstacles = new ArrayList<>();

        for (StaticObstacle obstacle : ps.getStaticObstacles()) {
            staticObstacles.add(new solution.Box(obstacle.getRect()));
        }

        // Set the workspace variables
        Workspace.getInstance().setRobotWidth(ps.getRobotWidth());
        Workspace.getInstance().setGoalBoxes(goalBoxes);
        Workspace.getInstance().setMoveableObstacles(moveableObstacles);
        Workspace.getInstance().setStaticObstacles(staticObstacles);

        // Load the robot starting position
        RobotConfig robotConfig = ps.getInitialRobotConfig();

        robotStartingPosition = new Robot(robotConfig.getPos(), robotConfig.getOrientation(),
                ps.getRobotWidth()
        );
    }

    /**
     * Solve the problem
     *
     * @return the robot path
     */
    public ArrayList<RobotAction> solve() throws NoPathException {
        ArrayList<GoalBoxRRT> rrtList = new ArrayList<>();
        ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> rrtSolutions = new ArrayList<>();

        for (int i = 0; i < Workspace.getInstance().getGoalBoxes().size(); i++) {
            GoalBoxRRT rrt = new GoalBoxRRT(Workspace.getInstance().getGoalBoxes().get(i),
                    goalBoxGoalPositions.get(i)
            );

            rrtList.add(rrt);

//            MoveableBoxVisualiser visualiser = new MoveableBoxVisualiser();
//            Window window = new Window(visualiser);
//
//            rrt.attachVisualiser(visualiser);

            if (rrt.solve()) {
                rrtSolutions.add(rrt.getSolution());
            } else {
                throw new NoPathException("Couldn't find a path for a GoalBox.");
            }
        }

        // Order the list of RRTs based on which ones need to be solved first
        ArrayList<GoalBoxRRT> orderedRRTList;

        try {
            orderedRRTList = calculateRRTOrder(rrtList);
        } catch (NoRRTOrderException e) {
            // No order could be found, solving has failed
            throw new NoPathException("No GoalBox ordering has no collision.", e);
        }

        Robot previousRobotPosition = robotStartingPosition;

        ArrayList<RobotAction> robotPath = new ArrayList<>();

        // Move moveable obstacles
        for (GoalBoxRRT rrt : orderedRRTList) {
            robotPath.addAll(rrt.solveMoveableObstacles(rrtSolutions, previousRobotPosition));

            if (robotPath.size() > 0) {
                previousRobotPosition = robotPath.get(robotPath.size() - 1).getFinalRobot();
            }
        }

        // Solve the robot path
        for (GoalBoxRRT rrt : orderedRRTList) {
            robotPath.addAll(rrt.solveRobotPath(previousRobotPosition));

            if (robotPath.size() > 0) {
                previousRobotPosition = robotPath.get(robotPath.size() - 1).getFinalRobot();
            }
        }

        return robotPath;
    }

    /**
     * Calculate the order of the RRT list so that each one is solvable without colliding with the
     * previous
     *
     * @param rrtList the unordered list of RRTs
     *
     * @return the ordered list of RRTs
     *
     * @throws NoRRTOrderException if no order could be found
     */
    private ArrayList<GoalBoxRRT> calculateRRTOrder(ArrayList<GoalBoxRRT> rrtList)
            throws NoRRTOrderException {
        if (rrtList.size() == 1) {
            return rrtList;
        }

        for (GoalBoxRRT rrt : rrtList) {
            // Remove rrt from the list
            ArrayList<GoalBoxRRT> remaining = new ArrayList<>(rrtList);
            remaining.remove(rrt);

            ArrayList<GoalBoxRRT> order;

            // Pick an order for the remaining items
            order = calculateRRTOrder(remaining);

            // Add the item to the ordered list
            order.add(rrt);

            if (checkRRTOrder(order)) {
                // This order is valid
                return order;
            }
        }

        // No way to order this list
        throw new NoRRTOrderException();
    }

    /**
     * Check if an ordering of RRTs is valid
     *
     * @param rrtList the list of RRTs
     *
     * @return whether the order is valid or not
     */
    private boolean checkRRTOrder(ArrayList<GoalBoxRRT> rrtList) {
        for (int i = 0; i < rrtList.size(); i++) {
            for (int j = 0; j < rrtList.size(); j++) {
                if (j < i) {
                    // Check to make sure #i doesn't intersect #j's final position
                    for (MoveableBoxAction action :
                            rrtList.get(i).getSolution().actionPathFromRoot()) {
                        if (action.getMovementBox().intersects(rrtList.get(j).getGoalBox())) {
                            return false;
                        }
                    }
                } else if (j > i) {
                    // Check to make sure #i doesn't intersect #j's starting position
                    for (MoveableBoxAction action :
                            rrtList.get(i).getSolution().actionPathFromRoot()) {
                        if (action.getMovementBox().intersects(rrtList.get(j).getInitialBox())) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Get the moveable obstacle start positions
     *
     * @return the moveable obstacle start positions
     */
    public ArrayList<MoveableBox> getMoveableObstacles() {
        return moveableObstacles;
    }

    /**
     * Get the goal box start positions
     *
     * @return the goal box start positions
     */
    public ArrayList<MoveableBox> getGoalBoxes() {
        return goalBoxes;
    }
}
