package solution;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Solve a problem
 */
public class Main {
    /**
     * Solves a problem specified by an input file and saves the solution to an output file
     *
     * @param args the input arguments. args[0] is the problem filename and args[1] is the output
     * filename
     */
    public static void main(String[] args) {
        GoalBoxSolver solver;

        // Load the problem file
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
                // Save a copy of the workspace
                Workspace.save();

                // Solve the problem
                ArrayList<RobotAction> robotPath = solver.solve();

                // Write to the output file
                Outputter outputter = new Outputter(robotPath, solver.getMoveableObstacles(),
                        solver.getGoalBoxes()
                );

                outputter.writeSolution(args[1]);

                System.out.println("Solution found");

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
    }
}
