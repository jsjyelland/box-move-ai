package solution;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A singleton class to represent workspace information. This is the static obstacles, moveable
 * obstacles and goal boxes.
 */
public class Workspace {
    /**
     * The static obstacles in the workspace
     */
    private ArrayList<Box> staticObstacles;

    /**
     * The moveable obstacles in the workspace
     */
    private ArrayList<MoveableBox> moveableObstacles;

    /**
     * The goal boxes in the workspace
     */
    private ArrayList<MoveableBox> goalBoxes;

    /**
     * The boxes needing to be moved
     */
    private ArrayList<MoveableBox> boxesNeedingMoving;

    /**
     * The width of the robot
     */
    private double robotWidth;

    /**
     * Versions of the workspace
     */
    private static ArrayList<Workspace> storedVersions = new ArrayList<>(
            Arrays.asList(new Workspace())
    );

    /**
     * Private constructor
     */
    private Workspace() {
        staticObstacles = new ArrayList<>();
        moveableObstacles = new ArrayList<>();
        goalBoxes = new ArrayList<>();
        boxesNeedingMoving = new ArrayList<>();
    }

    /**
     * Private constructor with variables
     *
     * @param staticObstacles the static obstacles
     * @param moveableObstacles the movable obstacles
     * @param goalBoxes the goal boxes
     * @param boxesNeedingMoving the boxes needing to be moved
     * @param robotWidth the robot width
     */
    private Workspace(ArrayList<Box> staticObstacles, ArrayList<MoveableBox> moveableObstacles,
            ArrayList<MoveableBox> goalBoxes, ArrayList<MoveableBox> boxesNeedingMoving,
            double robotWidth) {
        this.staticObstacles = staticObstacles;
        this.moveableObstacles = moveableObstacles;
        this.goalBoxes = goalBoxes;
        this.boxesNeedingMoving = boxesNeedingMoving;
        this.robotWidth = robotWidth;
    }

    /**
     * Get the singleton instance. Gets the most recent version
     *
     * @return the singleton instance
     */
    public static Workspace getInstance() {
        return storedVersions.get(storedVersions.size() - 1);
    }

    /**
     * Create a new version that's a copy of this one
     */
    public static void save() {
        storedVersions.add(getInstance().clone());
    }

    /**
     * Revert to the last version
     */
    public static void undo() {
        // Remove the top version from the list, ensure there's always at least one version
        // (the original)
        if (storedVersions.size() > 1) {
            storedVersions.remove(storedVersions.size() - 1);
        }
    }

    /**
     * Set the last version to this, and remove this version
     */
    public static void overwriteLastAndRemove() {
        if (storedVersions.size() > 1) {
            Workspace version = getInstance().clone();
            undo();
            undo();
            storedVersions.add(version);
        }
    }

    /**
     * Set the goal boxes
     *
     * @param goalBoxes the goal boxes
     */
    public void setGoalBoxes(ArrayList<MoveableBox> goalBoxes) {
        this.goalBoxes = goalBoxes;
    }

    /**
     * Set the moveable obstacles
     *
     * @param moveableObstacles the moveable obstacles
     */
    public void setMoveableObstacles(ArrayList<MoveableBox> moveableObstacles) {
        this.moveableObstacles = moveableObstacles;
    }

    /**
     * Set the static obstacles
     *
     * @param staticObstacles the static obstacles
     */
    public void setStaticObstacles(ArrayList<Box> staticObstacles) {
        this.staticObstacles = staticObstacles;
    }

    /**
     * Get the static obstacles
     *
     * @return the static obstacles
     */
    public ArrayList<Box> getStaticObstacles() {
        return new ArrayList<>(staticObstacles);
    }

    /**
     * Get all the boxes in the workspace
     *
     * @return all the boxes
     */
    public ArrayList<Box> getAllObstacles() {
        ArrayList<Box> obstacleList = new ArrayList<>();
        obstacleList.addAll(staticObstacles);
        obstacleList.addAll(moveableObstacles);
        obstacleList.addAll(boxesNeedingMoving);
        obstacleList.addAll(goalBoxes);
        return obstacleList;
    }

    /**
     * Get the goal boxes
     *
     * @return the goal boxes
     */
    public ArrayList<MoveableBox> getGoalBoxes() {
        return new ArrayList<>(goalBoxes);
    }

    /**
     * Get the moveable obstacles
     *
     * @return the moveable obstacles
     */
    public ArrayList<MoveableBox> getMoveableObstacles() {
        return new ArrayList<>(moveableObstacles);
    }

    /**
     * Mark a box as needing to be moved. Will move the box from the list of moveable obstacles to
     * the list of boxes needing to be moved
     *
     * @param box the box needing to be moved
     */
    public void markBoxNeedsMoving(MoveableBox box) {
        if (moveableObstacles.contains(box)) {
            moveableObstacles.remove(box);
            boxesNeedingMoving.add(box);
        }
    }

    /**
     * Push a moveable obstacle
     *
     * @param box the box to push
     */
    public void pushBox(MoveableBox box) {
        if (boxesNeedingMoving.contains(box)) {
            boxesNeedingMoving.remove(box);
        }
    }

    /**
     * Finishing pushing a moveable obstacle
     * @param newPosition the new position of a moveable box
     */
    public void finishPush(Box newPosition) {
        staticObstacles.add(newPosition);
    }

    /**
     * Push a goal box
     *
     * @param box the box to push
     */
    public void pushGoalBox(MoveableBox box) {
        if (goalBoxes.contains(box)) {
            goalBoxes.remove(box);
        }
    }

    /**
     * Finishing pushing a moveable obstacle
     * @param newPosition the new position of a moveable box
     */
    public void finishPushGoalBox(MoveableBox newPosition) {
        goalBoxes.add(newPosition);
    }

    public boolean boxesToMove() {
        return boxesNeedingMoving != null && boxesNeedingMoving.size() != 0;
    }

    public ArrayList<MoveableBox> getBoxesNeedingMoving() {
        return new ArrayList<>(boxesNeedingMoving);
    }

    /**
     * Set the robot width
     *
     * @param robotWidth the robot width
     */
    public void setRobotWidth(double robotWidth) {
        this.robotWidth = robotWidth;
    }

    /**
     * Get the robot width
     *
     * @return the robot width
     */
    public double getRobotWidth() {
        return robotWidth;
    }

    /**
     * Clone the workspace
     *
     * @return the cloned workspace
     */
    @Override
    protected Workspace clone() {
        // Deep copy static obstacles
        ArrayList<Box> newStaticObstacles = new ArrayList<>();

        for (Box box : staticObstacles) {
            newStaticObstacles.add(box.clone());
        }

        // Deep copy moveable obstacles
        ArrayList<MoveableBox> newMoveableObstacles = new ArrayList<>();

        for (MoveableBox box : moveableObstacles) {
            newMoveableObstacles.add(box.clone());
        }

        // Deep copy goal boxes
        ArrayList<MoveableBox> newGoalBoxes = new ArrayList<>();

        for (MoveableBox box : goalBoxes) {
            newGoalBoxes.add(box.clone());
        }

        // Deep copy boxes needing moving
        ArrayList<MoveableBox> newBoxesNeedingMoving = new ArrayList<>();

        for (MoveableBox box : boxesNeedingMoving) {
            newBoxesNeedingMoving.add(box.clone());
        }

        return new Workspace(newStaticObstacles, newMoveableObstacles, newGoalBoxes,
                newBoxesNeedingMoving, robotWidth
        );
    }
}
