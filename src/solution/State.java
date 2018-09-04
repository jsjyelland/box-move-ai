package solution;

public class State {
    public MoveableBox goalBox;
//    public MoveableBox[] goalBoxes;
//    public MoveableBox[] movingObstacles;

    /**
     * New state with a moveable box
     * @param x box x
     * @param y box y
     * @param w box width
     */
    public State(double x, double y, double w) {
        this(new MoveableBox(x, y, w));
    }

    /**
     * New state with a moveable box object
     * @param goalBox the moveable box
     */
    public State(MoveableBox goalBox) {
        this.goalBox = goalBox;
    }

    /**
     * Moving from one state to another
     * @param direction direction the goalBox is moving
     * @param distance distance the goalBox is moving
     * @param staticObstacles the static obstacles
     * @return whether the action is valid
     */
    public boolean action(MoveDirection direction, double distance, Box[] staticObstacles) {
        MoveableBox oldBox = goalBox.clone();

        switch(direction) {
            case up:
                goalBox.move(0, distance);
                break;

            case down:
                goalBox.move(0, -distance);
                break;

            case left:
                goalBox.move(-distance, 0);
                break;

            case right:
                goalBox.move(distance, 0);
                break;
        }

        Box union = goalBox.union(oldBox);

        for (Box box : staticObstacles) {
            if (union.intersects(box)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Clone the state
     * @return the cloned state
     */
    public State clone() {
        return new State(goalBox.clone());
    }
}
