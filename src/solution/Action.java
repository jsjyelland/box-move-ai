package solution;

import java.util.ArrayList;

/**
 * Information about a transition between states
 */
public class Action {
    /**
     * A path to move a box out of the way
     */
    ArrayList<State> moveableBoxPath;

    /**
     * The box that needs moving out of the way
     */
    private MoveableBox boxToMove;

    public Action(MoveableBox boxToMove) {
        this.boxToMove = boxToMove;
    }

    /**
     * Move the moveable box out of the way
     */
    public void moveBoxOutOfPath() {
        // TODO
    }
}
