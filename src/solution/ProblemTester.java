package solution;

import problem.*;
import tester.Tester;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.random;

public class ProblemTester {
    /**
     * The start positions of the goal boxes
     */
    private ArrayList<MoveableBox> goalBoxes;

    /**
     * The final positions of the goal boxes
     */
    private ArrayList<MoveableBox> goalBoxGoalPositions;

    /**
     * The start positions of the moveable obstacles
     */
    private ArrayList<MoveableBox> moveableObstacles;

    /**
     * The positions of the static obstacles
     */
    private ArrayList<Box> staticObstacles;

    /**
     * The starting position of the robot
     */
    private Robot robotStartingPosition;


    public static void main(String[] args) {
        int count = 0;
        while (true) {
            // Generate a problem
            ProblemTester pt = new ProblemTester();
            try {
                pt.generateAndWrite(args[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Problem created");

            // Solve it

            Main.main(args);

            visualiser.Visualiser.main(args);

            // Test it
            ProblemSpec ps = new ProblemSpec();
            try {
                ps.loadProblem(args[0]);
            } catch (IOException e1) {
                System.out.println("FAILED: Invalid problem file");
                System.out.println(e1.getMessage());
                return;
            }
            try {
                ps.loadSolution(args[1]);
            } catch (IOException e1) {
                System.out.println("FAILED: Invalid solution file");
                System.out.println(e1.getMessage());
                return;
            }
            Tester tester = new Tester(ps);
            if (tester.testSolutionReturn()) {
                count ++;
                System.out.println(count + " random problems solved!");

            } else {
                System.out.println("Random solution failed! Problems passed: " + count);
                return;
            }
        }
    }

    public void generateAndWrite(String filename) throws IOException {
        do {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            // Instantiate the writers
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);

            generateProblem();

            // First line: robot configuration
            bw.write(robotStartingPosition.getWidth() + " " + robotStartingPosition.getX() + " " + robotStartingPosition.getY() + " " + robotStartingPosition.getTheta());
            bw.newLine();

            // Second line: number of boxes
            bw.write(goalBoxes.size() + " " + moveableObstacles.size() + " " + staticObstacles.size());
            bw.newLine();

            // Initial and goal positions for the robot
            for (int i = 0; i < goalBoxes.size(); i++) {
                bw.write(goalBoxes.get(i).getRect().getCenterX() + " " + goalBoxes.get(i).getRect().getCenterY() + " " + goalBoxGoalPositions.get(i).getRect().getCenterX() + " " + goalBoxGoalPositions.get(i).getRect().getCenterY());
                bw.newLine();
            }

            // Initial positions for moveable obstacles
            for (MoveableBox box : moveableObstacles) {
                bw.write(box.getRect().getCenterX() + " " + box.getRect().getCenterY() + " " + box.getRect().getWidth());
                bw.newLine();
            }

            // Positions of the static obstacles
            for (Box box : staticObstacles) {
                bw.write(box.getRect().getMinX() + " " + box.getRect().getMaxY() + " " + box.getRect().getMaxX() + " " + box.getRect().getMinY());
                bw.newLine();
            }

            bw.close();
            fw.close();
        } while (!verifyProblem(filename));
    }

    public boolean verifyProblem(ProblemSpec ps) {
        Tester tester = new Tester(ps);

        List<problem.Box> movingObjects = new ArrayList<>();
        movingObjects.addAll(ps.getMovingBoxes());
        movingObjects.addAll(ps.getMovingObstacles());
        RobotConfig robot = ps.getInitialRobotConfig();

        if (!tester.hasCollision(robot, movingObjects)) {
            return false;
        }

        // More to verify
        Rectangle2D border = new Rectangle2D.Double(0,0,1,1);
        for (StaticObstacle o1: ps.getStaticObstacles()) {
            if (!border.contains(o1.getRect())) {
//                System.out.println("Static box outside of border");
                return false;
            }
            for (StaticObstacle o2: ps.getStaticObstacles()) {
                if ((!o1.equals(o2)) && (o1.getRect().intersects(o2.getRect()))) {
//                    System.out.println("Static obstacle collided with static obstacle");
                    return false;
                }
            }
            for (MoveableBox goalBoxLocation : goalBoxGoalPositions) {
                if(o1.getRect().intersects(goalBoxLocation.getRect())) {
//                    System.out.println("Goal box location collied with static obstacle");
                    return false;
                }
            }
        }
        for (MoveableBox goalBoxLocation : goalBoxGoalPositions) {
            if(!border.contains(goalBoxLocation.getRect())) {
//                System.out.println("Goal box location outside of border");
                return false;
            }
            for (MoveableBox goalBoxLocation2 : goalBoxGoalPositions) {
                if ((!goalBoxLocation.equals(goalBoxLocation2)) && (goalBoxLocation.getRect().intersects(goalBoxLocation2.getRect()))) {
//                    System.out.println("Goal box end points collided");
                    return false;
                }
            }
        }

        return true;
    }

    public boolean verifyProblem(String filename) {
        // Verify the configuration is valid
        ProblemSpec ps = new ProblemSpec();
        try {
            ps.loadProblem(filename);
        } catch (IOException e1) {
            System.out.println("FAILED: Invalid problem file");
            System.out.println(e1.getMessage());
            return false;
        }
        return verifyProblem(ps);
    }

    public boolean verifyProblem(Robot initialRobotConfiguration, ArrayList<MoveableBox> gBoxes,
                                 ArrayList<MoveableBox> gBoxGoalPositions, ArrayList<MoveableBox> mObstacles, ArrayList<Box> sObstacles) {
        // Verify the configuration is valid
        ProblemSpec ps = new ProblemSpec();
        // Add objects to ps

        ps.initialRobotConfig = new RobotConfig(initialRobotConfiguration.getPos(), initialRobotConfiguration.getTheta());

        ps.movingBoxEndPositions = new ArrayList<>();
        ps.movingBoxes = new ArrayList<>();
        ps.staticObstacles = new ArrayList<>();
        ps.movingObstacles = new ArrayList<>();

        for (MoveableBox goalBox: gBoxes) {
            ps.movingBoxes.add(new MovingBox(new Point2D.Double(goalBox.getRect().getX(), goalBox.getRect().getY()), goalBox.getRect().getWidth()));
        }

        for (MoveableBox goalBoxGoalPosition: gBoxGoalPositions) {
            ps.movingBoxEndPositions.add(new Point2D.Double(goalBoxGoalPosition.getRect().getX(), goalBoxGoalPosition.getRect().getY()));
        }

        for (MoveableBox moveableObstacle: mObstacles) {
            ps.movingObstacles.add(new MovingObstacle(new Point2D.Double(moveableObstacle.getRect().getX(),
                    moveableObstacle.getRect().getY()), moveableObstacle.getRect().getWidth()));
        }

        for (Box staticObstacle: sObstacles) {
            ps.staticObstacles.add(new StaticObstacle(staticObstacle.getRect().getX(), staticObstacle.getRect().getY(),
                    staticObstacle.getRect().getWidth(), staticObstacle.getRect().getHeight()));
        }

        return verifyProblem(ps);
    }

    public void generateProblem() {

        // Reset previous configuration
        goalBoxes = new ArrayList<>();
        goalBoxGoalPositions = new ArrayList<>();
        moveableObstacles = new ArrayList<>();
        staticObstacles = new ArrayList<>();

        // Create random configuration
        // Width
        double width = random() * 0.1 + 0.05; // 0.05 - 0.15

        // Robot
        double newAngle;
        double chance = random();

        if (chance < 0.4) {
            newAngle = 0;
        } else if (chance < 0.8) {
            newAngle = PI / 2;
        } else {
            newAngle = random() * 2 * PI;
        }

        robotStartingPosition = new Robot(width + random() * (1 - (2 * width)), width + random() * (1 - (2 * width)), newAngle, width);

        // Box counts
//        int goalBoxCount = (int) (random() * 3 + 8); // 8 - 10 goalBoxes
//        int moveableObstacleCount = (int) (random() * 3 + 6); // 6 - 8 moveableObstacles
//        int staticObstacleCount = (int) (random() * 11) + 2; // 2 - 12 staticObstacles

        int goalBoxCount = 5;
        int moveableObstacleCount = 5;
        int staticObstacleCount = 10;

        ArrayList<Box> allBoxes = new ArrayList<>();

        // Goal boxes
        for (int i = 0; i < goalBoxCount; i++) {
            MoveableBox box = null;
            ArrayList<MoveableBox> goalBoxesNew;
            for (int j = 0; j < 100; j++) {
                box = new MoveableBox(random() * (1 - width), random() * (1 - width), width);
                goalBoxesNew = (ArrayList<MoveableBox>)goalBoxes.clone();
                goalBoxesNew.add(box);
                if (verifyProblem(robotStartingPosition, goalBoxesNew, goalBoxGoalPositions, moveableObstacles, staticObstacles)) {
                    break;
                }
            }
            goalBoxes.add(box);

            MoveableBox boxGoal = null;
            ArrayList<MoveableBox> goalBoxGoalPositionsNew;
            for (int j = 0; j < 100; j++) {
                boxGoal = new MoveableBox(random() * (1 - width), random() * (1 - width), width);
                goalBoxGoalPositionsNew = (ArrayList<MoveableBox>)goalBoxGoalPositions.clone();
                goalBoxGoalPositionsNew.add(boxGoal);
                if (verifyProblem(robotStartingPosition, goalBoxes, goalBoxGoalPositionsNew, moveableObstacles, staticObstacles)) {
                    break;
                }
            }
            goalBoxGoalPositions.add(boxGoal);

//            System.out.println("Goal Boxes added: " + i);
        }

        // Moveable obstacles
        for (int i = 0; i < moveableObstacleCount; i++) {
            double boxWidth = (random() * 0.5 + 1) * width; // w - 1.5w
            MoveableBox box = null;
            ArrayList<MoveableBox> moveableObstaclesNew;
            for (int j = 0; j < 100; j++) {
                box = new MoveableBox(random() * (1 - boxWidth), random() * (1 - boxWidth), boxWidth);
                moveableObstaclesNew = (ArrayList<MoveableBox>)moveableObstacles.clone();
                moveableObstaclesNew.add(box);
                if (verifyProblem(robotStartingPosition, goalBoxes, goalBoxGoalPositions, moveableObstaclesNew, staticObstacles)) {
                    break;
                }
            }
            moveableObstacles.add(box);

//            System.out.println("Moveable obstacles added: " + i);
        }

        // Static obstacles
        for (int i = 0; i < staticObstacleCount; i++) {
            double boxWidth = (random() * 0.15 + 0.05); // 0.05 - 0.2
            double boxHeight = (random() * 0.15 + 0.05); // 0.05 - 0.2
            Box box = null;
            ArrayList<Box> staticObstaclesNew;
            for (int j = 0; j < 100; j++) {
                box = new Box(random() * (1 - boxWidth), random() * (1 - boxHeight), boxWidth, boxHeight);
                staticObstaclesNew = (ArrayList<Box>)staticObstacles.clone();
                staticObstaclesNew.add(box);
                if (verifyProblem(robotStartingPosition, goalBoxes, goalBoxGoalPositions, moveableObstacles, staticObstaclesNew)) {
                    break;
                }
            }
            staticObstacles.add(box);

//            System.out.println("Static obstacles added: " + i);
        }
    }
}
