package solution;

public class Main {
    public static void main(String[] args) {
        // Create an RRT
        RRT rrt = new RRT(new Box[] {
                new Box(0.5, 0.5, 0.1, 0.1),
                new Box(0.4, 0.5, 0.1, 0.1),
                new Box(0.6, 0.5, 0.1, 0.1)
        }, new State(0.5, 0.1, 0.1), new State(0.5, 0.8, 0.1));

        // Loop until a solution is found
        while (!rrt.expand()) {}

        System.out.println("Solution found");
    }
}
