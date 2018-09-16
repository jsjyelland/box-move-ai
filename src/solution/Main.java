package solution;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        GoalBoxSolver solver;

        try {
            solver = new GoalBoxSolver(args[0]);
        } catch (IOException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid input file");
            e.printStackTrace();
            return;
        }

        long time = System.currentTimeMillis();

        // Loop until a solution is found
        while (true) {
            try {
                Workspace.save();

                ArrayList<RobotAction> robotPath = solver.solve();

                Outputter outputter = new Outputter(robotPath, solver.getMoveableObstacles(),
                        solver.getGoalBoxes()
                );

                outputter.writeSolution(args[1]);

                // Time taken to solve
                System.out.println(
                        "Time taken: " + (System.currentTimeMillis() - time) / 1000 + "s"
                );

                break;
            } catch (IOException | ArrayIndexOutOfBoundsException e) {
                System.out.println("Invalid output file");
                e.printStackTrace();
                break;
            } catch (NoPathException | BoxLostException e) {
                // We lost a box or couldn't find a path, try again
                e.printStackTrace();
                Workspace.undo();
            }
        }

        System.out.println("Solution found");
    }
}
