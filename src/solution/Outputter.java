package solution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.ceil;

/**
 * Saves a problem solution to an output file
 */
public class Outputter {

    /**
     * The path the robot takes
     */
    ArrayList<RobotAction> robotPath;

    /**
     * The initial configuration of the moveable obstacles
     */
    ArrayList<MoveableBox> initialMoveableObstacles;

    /**
     * The initial configuration of the goal boxes
     */
    ArrayList<MoveableBox> initialGoalBoxes;


    public Outputter(ArrayList<RobotAction> robotPath, ArrayList<MoveableBox> initialMoveableObstacles, ArrayList<MoveableBox> initialGoalBoxes) {
        this.robotPath = robotPath;
        this.initialMoveableObstacles = initialMoveableObstacles;
        this.initialGoalBoxes = initialGoalBoxes;
    }

    public void writeSolution(String filename) throws IOException {
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
        for(RobotAction action: robotPath) {
            if (!action.shouldRotateFirst()) {
                double actionSize = action.getInitialRobot().distanceToOtherRobot(action.getFinalRobot());
                double numSteps = ceil(actionSize / 0.001);

                // Step along the line, adding a robot action to robotPathPrimitive each time
                for (double i = 0; i < numSteps; i++) {

                    // Calculate the new initial and final robot positions
                    Robot newInitialRobot = action.getInitialRobot().clone();
                    Robot newFinalRobot = action.getInitialRobot().clone();
                    newInitialRobot.move(i / numSteps * action.getDx(), i / numSteps * action.getDy(), i / numSteps * action.getDtheta());
                    newFinalRobot.move((i + 1) / numSteps * action.getDx(), (i + 1) / numSteps * action.getDy(), (i + 1) / numSteps * action.getDtheta());

                    // Create the new action object
                    RobotAction newAction = new RobotAction(newInitialRobot, newFinalRobot, action.shouldRotateFirst());

                    // Add the correct box, if the original action is pushing one
                    if (action.getBoxPushing() != null) {
                        MoveableBox newBoxPushing = action.getBoxPushing().clone();
                        newBoxPushing.move(i / numSteps * action.getDx(), i / numSteps * action.getDy());
                        newAction.setBoxPushing(newBoxPushing);
                    }

                    // Append it to the ArrayList
                    robotPathPrimitive.add(newAction);


                }
            } else {
                // This is disabled, so I can't be bothered throwing an error
                System.out.println("Robot is rotating first");
            }
        }

        // Initiate list of boxes in original and moved positions
        ArrayList<MoveableBox> allBoxes = new ArrayList<>(initialGoalBoxes);
        allBoxes.addAll(initialMoveableObstacles);


        // First line: number of steps
        bw.write(Integer.toString(robotPathPrimitive.size()));
        bw.newLine();

        // Second line: initial configuration
        RobotAction initialAction = robotPathPrimitive.get(0);
        bw.write(initialAction.getInitialRobot().getX() + " " + initialAction.getInitialRobot().getY() + " " + initialAction.getInitialRobot().getTheta());
        for (MoveableBox box: allBoxes) {
            bw.write(" " + box.getRect().getCenterX() + " " + box.getRect().getCenterY());
        }
        bw.newLine();

        // Remaining lines: configurations of robot and boxes
        for (int i = 0; i < robotPathPrimitive.size(); i++) {

            RobotAction primitiveAction = robotPathPrimitive.get(i);

            // Robot configuration
            bw.write(primitiveAction.getFinalRobot().getX() + " " + primitiveAction.getFinalRobot().getY() + " " + primitiveAction.getFinalRobot().getTheta() + " ");

            // Move a box if necessary
            if (primitiveAction.getBoxPushing() != null) {
                System.out.println(primitiveAction.getBoxPushing());
                int boxPushedIndex = allBoxes.indexOf(primitiveAction.getBoxPushing());
                try {
                    MoveableBox boxPushed = allBoxes.get(boxPushedIndex);
                    boxPushed.move(primitiveAction.getDx(), primitiveAction.getDy());
//                    allBoxes.set(boxPushedIndex, action.getFinalBoxPushing());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(allBoxes);
                    System.out.println(primitiveAction.getBoxPushing());
                }
            }

            // Box configuration
            for (MoveableBox box: allBoxes) {
                bw.write(" " + box.getRect().getCenterX() + " " + box.getRect().getCenterY());
            }

            bw.newLine();
        }

        // Complete the write
        bw.close();
        fw.close();
    }
}
