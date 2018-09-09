package solution;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Create an RRT
        try {
            Box[] initialStaticObstacles = new Box[] {
                    new Box(0.0, 0.0, 1, 0.1),
                    new Box(0.2, 0.2, 0.9, 0.1),
                    new Box(0.0, 0.4, 0.9, 0.1),
                    new Box(0.2, 0.6, 0.9, 0.1),
                    new Box(0.0, 0.8, 0.9, 0.1)
            };
            RRT rrt = new RRT(initialStaticObstacles, new MoveableBox[0],
                    new MoveableBox(0.9, 0.125, 0.05),
                    new MoveableBox(0.0, 0.9, 0.05));

            // Create the visualizer
            Visualiser visualiser = new Visualiser(initialStaticObstacles);
            Window window = new Window(visualiser);

            // Loop until a solution is found
            while (true) {
                boolean expanded = rrt.expand();
                visualiser.paintTree(rrt.getTree());
                if (expanded) {
                    visualiser.paintSolution(rrt.getSolution());
                    System.out.println("Solution found");
                    break;
                }

                // Wait because this is too fast to watch
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        } catch (InvalidStateException e) {
            System.out.println(e);
        }
    }
}
