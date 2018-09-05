package solution;

import java.awt.geom.Rectangle2D;

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
     * @return the new state
     * @throws InvalidStateException if the new state is invalid
     */
    public State action(MoveDirection direction, double distance, Box[] staticObstacles) throws InvalidStateException {
        State newState = new State(goalBox.clone());

        switch(direction) {
            case up:
                newState.goalBox.move(0, distance);
                break;

            case down:
                newState.goalBox.move(0, -distance);
                break;

            case left:
                newState.goalBox.move(-distance, 0);
                break;

            case right:
                newState.goalBox.move(distance, 0);
                break;
        }

        Box union = goalBox.union(newState.goalBox);

        Rectangle2D boundingRectangle = new Rectangle2D.Double(0, 0, 1, 1);
        if (!boundingRectangle.contains(union.getRect())) {
            throw new InvalidStateException();
        }

        for (Box box : staticObstacles) {
            if (union.intersects(box)) {
                throw new InvalidStateException();
            }
        }

        return newState;
    }

    /**
     * Clone the state
     * @return the cloned state
     */
    public State clone() {
        return new State(goalBox.clone());
    }
}
