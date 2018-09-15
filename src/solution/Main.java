package solution;

import problem.ProblemSpec;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // Load a problem
        ProblemSpec ps = new ProblemSpec();
        try {
            ps.loadProblem(args[0]);
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid input file.");
            e.printStackTrace();
            return;
        }

        ArrayList<Box> initialStaticObstacles = new ArrayList<>();
        ArrayList<MoveableBox> initialMoveableObstacles = new ArrayList<>();
        ArrayList<MoveableBox> initialGoalBoxes = new ArrayList<>();
        ArrayList<MoveableBox> finalGoalBoxes = new ArrayList<>();

        for (problem.StaticObstacle pObstacle: ps.getStaticObstacles()) {
            Box obstacle = new Box(pObstacle.getRect());
            initialStaticObstacles.add(obstacle);
        }

        for (problem.Box pMoveableObstacle: ps.getMovingObstacles()) {
            try {
                MoveableBox moveableObstacle = new MoveableBox(pMoveableObstacle.getRect());
                initialMoveableObstacles.add(moveableObstacle);
            } catch (BoxSizeException e) {
                System.out.println("A moveable box is not rectangular");
                e.printStackTrace();
            }
        }

        for (problem.Box pGoalBox: ps.getMovingBoxes()) {
            try {
                MoveableBox goalBox = new MoveableBox(pGoalBox.getRect());
                initialGoalBoxes.add(goalBox);
            } catch (BoxSizeException e) {
                System.out.println("A goal box is not rectangular");
                e.printStackTrace();
            }
        }

        for (Point2D pGoalBoxPoint: ps.getMovingBoxEndPositions()) {
            try {
                problem.Box pGoalBox = new problem.MovingBox(pGoalBoxPoint, ps.getRobotWidth());
                MoveableBox goalBox = new MoveableBox(pGoalBox.getRect());
                finalGoalBoxes.add(goalBox);
            } catch (BoxSizeException e) {
                System.out.println("If this happens, I really have no idea what's going on");
                e.printStackTrace();
            }
        }

        problem.RobotConfig pInitialRobotConfiguration = ps.getInitialRobotConfig();
        Robot initialRobotConfiguration = new Robot(pInitialRobotConfiguration.getPos(), pInitialRobotConfiguration.getOrientation(), ps.getRobotWidth());

        Workspace.getInstance().setStaticObstacles(initialStaticObstacles);
        Workspace.getInstance().setMoveableObstacles(initialMoveableObstacles);
        Workspace.getInstance().setGoalBoxes(initialGoalBoxes);

        Workspace.getInstance().setRobotWidth(ps.getRobotWidth());

        ArrayList<GoalBoxRRT> rrts = new ArrayList<>();


        for (int i = 0; i < initialGoalBoxes.size(); i++) {
            // TODO make RRT objects here
            GoalBoxRRT rrt = new GoalBoxRRT(Workspace.getInstance().getGoalBoxes().get(i), finalGoalBoxes.get(i), initialRobotConfiguration);
            rrts.add(rrt);
        }

        while (true) {
            ArrayList<RobotAction> completeActionPath = new ArrayList<>();
            for (GoalBoxRRT rrt: rrts) {
                // TODO perhaps look to not just solve them in order?
                rrt.solve();
                ArrayList<RobotAction> actionPath = rrt.getRobotPath();
                completeActionPath.addAll(actionPath);
            }

            Outputter outputter = new Outputter(completeActionPath, initialMoveableObstacles, initialGoalBoxes);
            try {
                outputter.writeSolution(args[1]);
                break;
            } catch (IOException | ArrayIndexOutOfBoundsException e) {
                System.out.println("Invalid output file");
                e.printStackTrace();
                break;
            } catch (BoxLostException e) {
                // We lost a box. try again
            }
        }


    }
}
