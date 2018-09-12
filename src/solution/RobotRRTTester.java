package solution;

import java.util.ArrayList;
import java.util.Arrays;

public class RobotRRTTester {
    public static void main(String[] args) {
        // Create an RRT
//        ArrayList<Box> initialStaticObstacles = new ArrayList<>(Arrays.asList(
//                new Box(0.0, 0.0, 1, 0.1),
//                new Box(0.1, 0.2, 0.9, 0.1),
//                new Box(0.0, 0.4, 0.9, 0.1),
//                new Box(0.2, 0.6, 0.9, 0.1),
//                new Box(0.0, 0.8, 0.9, 0.1)
//        ));

        RobotRRT rrt = new RobotRRT(
                new ArrayList<>(),
                new Robot(0.3, 0.3, 0, 0.05),
                new Robot(0.5, 0.5, 0, 0.05)
        );

        // Create the visualizer
        RobotVisualiser visualiser = new RobotVisualiser();
        Window window = new Window(visualiser);

        // Loop until a solution is found
        while (true) {
            if (rrt.expand()) {
                visualiser.paintSolution(rrt.getSolution());
                System.out.println("Solution found");
                break;
            }

            visualiser.paintTree(rrt.getTree());
        }
    }
}
