package solution;

public class Main {
    public static void main(String[] args) {
        // Create an RRT
        RRT rrt = new RRT(new Box[] {
                new Box(0.0, 0.0, 1, 0.1),
                new Box(0.2, 0.2, 0.9, 0.1),
                new Box(0.0, 0.4, 0.9, 0.1),
                new Box(0.2, 0.6, 0.9, 0.1),
                new Box(0.0, 0.8, 0.9, 0.1)
        }, new State(0.9, 0.125, 0.05), new State(0.0, 0.9, 0.05));

        // Loop until a solution is found
        while (!rrt.expand()) {}

        System.out.println("Solution found");
    }
}
