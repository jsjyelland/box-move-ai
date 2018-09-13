package solution;

import java.util.ArrayList;
import java.util.Arrays;

public class MoveableBoxRRTTester {
    public static void main(String[] args) {
        // Create an RRT
        ArrayList<Box> initialStaticObstacles = new ArrayList<>(Arrays.asList(
                new Box(0.0, 0.0, 1, 0.1),
                new Box(0.1, 0.2, 0.9, 0.1),
                new Box(0.0, 0.4, 0.9, 0.1),
                new Box(0.2, 0.6, 0.9, 0.1),
                new Box(0.0, 0.8, 0.9, 0.1)
        ));

        ArrayList<MoveableBox> initialMoveableObstacles = new ArrayList<>(Arrays.asList(
                new MoveableBox(0.3, 0.31, 0.05),
                new MoveableBox(0.5, 0.51, 0.05),
                new MoveableBox(0.01, 0.6, 0.05)
        ));

        GoalBoxRRT rrt = new GoalBoxRRT(
                initialStaticObstacles,
                initialMoveableObstacles,
                new MoveableBox(0.9, 0.125, 0.05),
                new MoveableBox(0.0, 0.9, 0.05),
                0.05
        );

        // Create the visualizer
        Visualiser visualiser = new MoveableBoxVisualiser();
        Window window = new Window(visualiser);

        // Attach it to the RRT.
        rrt.attachVisualiser(visualiser);

        // Solve the rrt
        System.out.println(rrt.solve());

        ArrayList<RobotAction> actionPath = rrt.getRobotPath();

        RobotActionVisualiser visualiser1 = new RobotActionVisualiser(actionPath);
        Window window1 = new Window(visualiser1);
    }
}
