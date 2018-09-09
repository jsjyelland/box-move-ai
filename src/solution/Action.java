package solution;

import java.util.ArrayList;

/**
 * Information about a transition between states
 */
public class Action {
    /**
     * The solution node of a path to move a box out of the way
     */
    TreeNode<State, Action> moveableBoxSolutionNode;

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
