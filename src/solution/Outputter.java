package solution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Math.ceil;

/**
 * Saves a problem solution to an output file
 */
public class Outputter {
    /**
     * The path the robot takes
     */
    private ArrayList<RobotAction> robotPath;

    /**
     * The initial configuration of the moveable obstacles
     */
    private ArrayList<MoveableBox> initialMoveableObstacles;

    /**
     * The initial configuration of the goal boxes
     */
    private ArrayList<MoveableBox> initialGoalBoxes;

    /**
     * Construct an outputter with a robot path
     *
     * @param robotPath the robot path
     * @param initialMoveableObstacles the initial movable obstacles
     * @param initialGoalBoxes the initial goal boxes
     */
    public Outputter(ArrayList<RobotAction> robotPath,
            ArrayList<MoveableBox> initialMoveableObstacles,
            ArrayList<MoveableBox> initialGoalBoxes) {
        this.robotPath = robotPath;
        this.initialMoveableObstacles = initialMoveableObstacles;
        this.initialGoalBoxes = initialGoalBoxes;
    }

    /**
     * Write the solution to a file
     *
     * @param filename the file to write to
     *
     * @throws IOException if there was an IO exception when writing
     * @throws BoxLostException if a box was lost while pushing
     */
    public void writeSolution(String filename) throws IOException, BoxLostException {
        // Ensure the file exists
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }

        // Instantiate the writers
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);

        // Output the solution

        // Break the solution up into primitive steps
        ArrayList<RobotAction> robotPathPrimitive = new ArrayList<>();

        ArrayList<MoveableBox> allBoxes = new ArrayList<>(initialGoalBoxes);
        allBoxes.addAll(initialMoveableObstacles);

        ArrayList<MoveableBox> allBoxesInitialDeepClone = new ArrayList<>();
        ArrayList<MoveableBox> allBoxesDeepCloneForWholeActions = new ArrayList<>();
        for (MoveableBox box : allBoxes) {
            allBoxesInitialDeepClone.add(box.clone());
            allBoxesDeepCloneForWholeActions.add(box.clone());
        }

        ArrayList<ArrayList<MoveableBox>> allBoxesList = new ArrayList<>();

        for (RobotAction action : robotPath) {
            double actionSize = action.getInitialRobot().distanceToOtherRobot(
                    action.getFinalRobot()
            );

            double numSteps = ceil(actionSize / 0.0005);

            // Step along the line, adding a robot action to robotPathPrimitive each time
            for (int i = 0; i < numSteps; i++) {
                // Calculate the new initial and final robot positions
                Robot newInitialRobot = action.getInitialRobot().clone();
                Robot newFinalRobot = action.getInitialRobot().clone();

                newInitialRobot.move(i / numSteps * action.getDx(), i / numSteps * action.getDy(),
                        i / numSteps * action.getDtheta()
                );

                newFinalRobot.move((i + 1) / numSteps * action.getDx(),
                        (i + 1) / numSteps * action.getDy(), (i + 1) / numSteps * action.getDtheta()
                );

                // Create the new action object
                RobotAction newAction = new RobotAction(newInitialRobot, newFinalRobot);

                // Append it to the ArrayList
                robotPathPrimitive.add(newAction);

                // Clone allBoxes
                ArrayList<MoveableBox> allBoxesDeepClone = new ArrayList<>();
                for (MoveableBox box : allBoxesDeepCloneForWholeActions) {
                    allBoxesDeepClone.add(box.clone());
                }

                // Modify allBoxesDeepCopy to correctly move a box if necessary then add to
                // AllBoxesList
                if (action.getBoxPushing() != null) {
                    int boxPushedIndex = allBoxesDeepCloneForWholeActions.indexOf(
                            action.getBoxPushing());
                    try {
                        MoveableBox boxPushed = allBoxesDeepClone.get(boxPushedIndex);
                        boxPushed.move((i + 1) / numSteps * action.getDx(),
                                (i + 1) / numSteps * action.getDy()
                        );
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println(allBoxesDeepCloneForWholeActions);
                        System.out.println(action.getBoxPushing());

                        // This is pretty ridiculous but whatever
                        throw new BoxLostException();
                    }
                }

                allBoxesList.add(allBoxesDeepClone);
            }

            if (action.getBoxPushing() != null) {
                int boxPushedIndex = allBoxesDeepCloneForWholeActions.indexOf(action.getBoxPushing());

                try {
                    MoveableBox boxPushed = allBoxesDeepCloneForWholeActions.get(boxPushedIndex);
                    boxPushed.move(action.getDx(), action.getDy());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(allBoxesDeepCloneForWholeActions);
                    System.out.println(action.getBoxPushing());

                    // This is pretty ridiculous but whatever
                    throw new BoxLostException();
                }
            }

        }

        // First line: number of steps (+ 1)
        bw.write(Integer.toString(robotPathPrimitive.size() + 1));
        bw.newLine();

        // Second line: initial configuration
        RobotAction initialAction = robotPathPrimitive.get(0);
        bw.write(initialAction.getInitialRobot().getX() + " " +
                         initialAction.getInitialRobot().getY() + " " +
                         initialAction.getInitialRobot().getTheta()
        );

        for (MoveableBox box : allBoxesInitialDeepClone) {
            bw.write(" " + box.getRect().getCenterX() + " " + box.getRect().getCenterY());
        }

        bw.newLine();

        // Remaining lines: configurations of robot and boxes
        for (int i = 0; i < robotPathPrimitive.size(); i++) {

            RobotAction primitiveAction = robotPathPrimitive.get(i);

            ArrayList<MoveableBox> allBoxesSingle = allBoxesList.get(i);

            // Robot configuration
            bw.write(primitiveAction.getFinalRobot().getX() + " " +
                             primitiveAction.getFinalRobot().getY() + " " +
                             primitiveAction.getFinalRobot().getTheta() + " "
            );

            // Box configuration
            for (MoveableBox box : allBoxesSingle) {
                bw.write(" " + box.getRect().getCenterX() + " " + box.getRect().getCenterY());
            }

            bw.newLine();
        }

        // Complete the write
        bw.close();
        fw.close();

        System.out.println("Output written! Success");
    }
}
