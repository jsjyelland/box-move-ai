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
        ArrayList<TreeNode<MoveableBoxState, MoveableBoxAction>> rrtSolutions = new ArrayList<>();
        ArrayList<GoalBoxRRT> rrtList = new ArrayList<>();

        for (int i = 0; i < Workspace.getInstance().getGoalBoxes().size(); i++) {
            GoalBoxRRT rrt = new GoalBoxRRT(Workspace.getInstance().getGoalBoxes().get(i),
                    goalBoxGoalPositions.get(i)
            );

            rrtList.add(rrt);

            if (rrt.solve()) {
                rrtSolutions.add(rrt.getSolution());
            } else {
                throw new NoPathException();
            }
        }

        // TODO order the list of rrts based on which ones need to be solved first

        Robot previousRobotPosition = robotStartingPosition;

        ArrayList<RobotAction> robotPath = new ArrayList<>();

        for (GoalBoxRRT rrt : rrtList) {
            robotPath.addAll(rrt.solveMoveableObstacles(rrtSolutions, previousRobotPosition));

            if (robotPath.size() > 0) {
                previousRobotPosition = robotPath.get(robotPath.size() - 1).getFinalRobot();
            }
        }

        for (GoalBoxRRT rrt : rrtList) {
            robotPath.addAll(rrt.solveRobotPath(previousRobotPosition));

            if (robotPath.size() > 0) {
                previousRobotPosition = robotPath.get(robotPath.size() - 1).getFinalRobot();
            }
        }

        return robotPath;
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
