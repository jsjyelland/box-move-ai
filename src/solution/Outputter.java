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

                MoveableBox initialBox = null;
                MoveableBox finalBox = null;
                double dx = 0;
                double dy = 0;
                if (action.getInitialBoxPushing() != null) {
                    initialBox = action.getInitialBoxPushing().clone();
                    finalBox = action.getFinalBoxPushing().clone();
                    dx = finalBox.getRect().getX() - initialBox.getRect().getX();
                    dy = finalBox.getRect().getY() - initialBox.getRect().getY();
                }

                // Step along the line, adding a robot action to robotPathPrimitive each time
                for (double i = 0; i < numSteps; i++) {
//                    // If this is the last primitive in a whole robotStep, just force it to be the same as the final robot config,
//                    // to avoid rounding errors. A MAX_ERROR exists in the solution checker, so we should be ok.
//                    if (i == numSteps - 1) {
//                        Robot newInitialRobot = action.getInitialRobot().clone();
//                        Robot newFinalRobot = action.getInitialRobot().clone();
//                    }

                    // Calculate the new initial and final robot positions
                    Robot newInitialRobot = action.getInitialRobot().clone();
                    Robot newFinalRobot = action.getInitialRobot().clone();
                    newInitialRobot.move(i / numSteps * action.getDx(), i / numSteps * action.getDy(), i / numSteps * action.getDtheta());
                    newFinalRobot.move((i + 1) / numSteps * action.getDx(), (i + 1) / numSteps * action.getDy(), (i + 1) / numSteps * action.getDtheta());

                    // Create the new action object
                    RobotAction newAction = new RobotAction(newInitialRobot, newFinalRobot, action.shouldRotateFirst());
                    if (action.getInitialBoxPushing() != null) {
                        MoveableBox newInitialBox = initialBox.clone();
                        MoveableBox newFinalBox = finalBox.clone();
                        newInitialBox.move(i / numSteps * dx, i / numSteps * dy);
                        newFinalBox.move((i + 1) / numSteps * dx, (i + 1) / numSteps * dy);
                        newAction.setInitialBoxPushing(newInitialBox);
                        newAction.setFinalBoxPushing(newFinalBox);
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
        bw.write(robotPathPrimitive.size());
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

            RobotAction action = robotPathPrimitive.get(i);

            // Robot configuration
            bw.write(action.getFinalRobot().getX() + " " + action.getFinalRobot().getY() + " " + action.getFinalRobot().getTheta() + " ");

            // Move a box if necessary
            if (action.getInitialBoxPushing() != null) {
                System.out.println(action.getInitialBoxPushing());
                int boxPushedIndex = allBoxes.indexOf(action.getInitialBoxPushing());
                try {
                    allBoxes.set(boxPushedIndex, action.getFinalBoxPushing());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(allBoxes);
                    System.out.println(action.getInitialBoxPushing());
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
