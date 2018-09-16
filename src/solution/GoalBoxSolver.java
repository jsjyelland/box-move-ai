package solution;

import problem.Box;
import problem.ProblemSpec;
import problem.RobotConfig;
import problem.StaticObstacle;

import java.io.IOException;
import java.util.ArrayList;

public class GoalBoxSolver {
    /**
     * The final positions of the goal boxes
     */
    ArrayList<MoveableBox> goalBoxGoalPositions;

    /**
     * The starting position of the robot
     */
    Robot robotStartingPosition;

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
        ArrayList<MoveableBox> goalBoxes = new ArrayList<>();

        for (Box box : ps.getMovingBoxes()) {
            goalBoxes.add(new MoveableBox(box.getRect()));
        }

        // Load the goal box goal positions
        for (int i = 0; i < ps.getMovingBoxEndPositions().size(); i++) {
            goalBoxGoalPositions.add(new MoveableBox(
                    ps.getMovingBoxEndPositions().get(i),
                    goalBoxes.get(i).getRect().getWidth()));
        }

        // Load the moveable obstacles
        ArrayList<MoveableBox> moveableObstacles = new ArrayList<>();

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
    public ArrayList<RobotAction> solve() {
        for (int i = 0; i < Workspace.getInstance().getGoalBoxes().size(); i++) {
            GoalBoxRRT rrt = new GoalBoxRRT(Workspace.getInstance().getGoalBoxes().get(i),
                    goalBoxGoalPositions.get(i), robotStartingPosition
            );

            rrt.solve();
        }

        return new ArrayList<>();
    }
}
