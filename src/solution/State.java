package solution;

enum Direction {
    up,
    down,
    left,
    right
}


public class State {
    public MoveableBox goalBox;
//    public MoveableBox[] goalBoxes;
//    public MoveableBox[] movingObstacles;

    public State(double x, double y, double w) {
        this(new MoveableBox(x, y, w));
    }

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
    public boolean action(Direction direction, double distance, Box[] staticObstacles) {
//        State oldState = this.clone();
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

        Box union = goalBox.createUnion(oldBox);

        for (Box box : staticObstacles) {
            if (union.intersects(box)) {
                return false;
            }
        }

        return true;

    }




    public State clone() {
        return new State(goalBox);
    }
}
