package solution;

import java.util.ArrayList;
import java.util.Arrays;

public class RobotRRTTester {
    public static void main(String[] args) {
        // Create an RRT
        ArrayList<Box> initialStaticObstacles = new ArrayList<>(Arrays.asList(
                new Box(0.1, 0.2, 0.9, 0.1),
                new Box(0.0, 0.4, 0.9, 0.1),
                new Box(0.2, 0.6, 0.9, 0.1)
        ));

        RobotRRT rrt;

        Workspace.getInstance().setStaticObstacles(initialStaticObstacles);

        try {
            rrt = new RobotRRT(
                    new Robot(0.9, 0.05, 0, 0.04),
                    new Robot(0.05, 0.95, 0, 0.04),
                    null
            );
        } catch (NoPathException e) {
            return;
        }

        // Create the visualizer
        RobotVisualiser visualiser = new RobotVisualiser();
        Window window = new Window(visualiser);

        // Attach it to the RRT.
        rrt.attachVisualiser(visualiser);

        // Solve the rrt
        if (rrt.solve()) {
            System.out.println("Solution found");
        }
    }
}
