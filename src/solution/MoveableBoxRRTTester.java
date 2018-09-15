package solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MoveableBoxRRTTester {
    public static void main(String[] args) {
        // Create an RRT
        ArrayList<Box> initialStaticObstacles = new ArrayList<>(Arrays.asList(
                new Box(0.0, 0.0, 1, 0.1),
                new Box(0.2, 0.2, 0.4, 0.1),
                new Box(0.0, 0.4, 0.6, 0.1),
                new Box(0.2, 0.6, 0.8, 0.1),
                new Box(0.0, 0.8, 0.4, 0.1)
        ));

        ArrayList<MoveableBox> initialMoveableObstacles = new ArrayList<>(Arrays.asList(
                new MoveableBox(0.3, 0.31, 0.05),
                new MoveableBox(0.5, 0.51, 0.05),
                new MoveableBox(0.01, 0.6, 0.05)
        ));

        ArrayList<MoveableBox> goalBoxes = new ArrayList<>(Arrays.asList(
                new MoveableBox(0.9, 0.125, 0.05)
        ));

        Workspace.getInstance().setStaticObstacles(initialStaticObstacles);
        Workspace.getInstance().setMoveableObstacles(initialMoveableObstacles);
        Workspace.getInstance().setGoalBoxes(goalBoxes);

        Workspace.getInstance().setRobotWidth(0.05);

        GoalBoxRRT rrt = new GoalBoxRRT(
                new MoveableBox(0.9, 0.125, 0.05),
                new MoveableBox(0.0, 0.9, 0.05),
                new Robot(0.8, 0.2, 0, Workspace.getInstance().getRobotWidth())
        );

        // Create the visualizer
        Visualiser visualiser = new MoveableBoxVisualiser();
        Window window = new Window(visualiser);

        // Attach it to the RRT.
        rrt.attachVisualiser(visualiser);

        // Solve the rrt
        System.out.println(rrt.solve());

        ArrayList<RobotAction> actionPath = rrt.getRobotPath();

        Outputter outputter = new Outputter(actionPath, initialMoveableObstacles, goalBoxes);
        try {
            outputter.writeSolution("test.txt");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BoxLostException e) {
            e.printStackTrace();
            try {
                outputter.writeSolution("test.txt");
            } catch (IOException e2) {
                e2.printStackTrace();
            } catch (BoxLostException e2) {
                e2.printStackTrace();
            }
        }

        RobotActionVisualiser visualiser1 = new RobotActionVisualiser(actionPath);
        Window window1 = new Window(visualiser1);
    }
}
