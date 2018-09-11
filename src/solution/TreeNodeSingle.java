package solution;

import java.util.ArrayList;

/**
 * A node in a tree. Holds an action and a state
 *
 * @param <T> The class of the state object the tree node holds
 */
public class TreeNodeSingle<T> {
    /**
     * List of children connected to this node
     */
    private ArrayList<TreeNodeSingle<T>> children;

    /**
     * The parent in the tree
     */
    private TreeNodeSingle<T> parent;

    /**
     * The state held by the node
     */
    private T state;

    /**
     * Construct a tree with a state and an action
     *
     * @param state the state to hold
     */
    TreeNodeSingle(T state) {
        children = new ArrayList<>();
        this.state = state;
        parent = null;
    }

    /**
     * Add a child to this node
     *
     * @param t the node to add to this one
     */
    void addChild(TreeNodeSingle<T> t) {
        t.setParent(this);
        children.add(t);
    }

    /**
     * Set the parent of this node
     *
     * @param parent the parent to set
     */
    public void setParent(TreeNodeSingle<T> parent) {
        this.parent = parent;
    }

    /**
     * Get the parent of this node
     *
     * @return the parent of this node
     */
    public TreeNodeSingle<T> getParent() {
        return parent;
    }

    /**
     * Get the state of this node
     *
     * @return the state
     */
    public T getState() {
        return state;
    }

    /**
     * A string representation of the tree, assuming this node is a root.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return print("", true, true);
    }

    /**
     * Convert this node into a string. Helper function for toString(). Will return the string
     * representation of the value, as well as list all it's children underneath.
     *
     * @param prefix a prefix string to put before all outputted lines. Allows for indenting if this
     * node is a child
     * @param isTail if this is the last child node
     * @param isStart if this is the root
     *
     * @return the string representation of this node
     */
    private String print(String prefix, boolean isTail, boolean isStart) {
        String out = (isStart ? "" : prefix + (isTail ? "└── " : "├── ")) + state + "\n";

        int counter = 0;
        for (TreeNodeSingle<T> child : children) {
            if (counter < children.size() - 1) {
                out += child.print((isStart ? "" : prefix + (isTail ? "    " : "│   ")),
                        false, false
                );
            } else {
                out += child.print((isStart ? "" : prefix + (isTail ? "    " : "│   ")),
                        true, false
                );
            }

            counter++;
        }

        return out;
    }

    /**
     * Get the children of the node
     *
     * @return a list of the children of the node
     */
    public ArrayList<TreeNodeSingle<T>> getChildren() {
        return children;
    }

    /**
     * Remove the node from its parent. Also removes this node from the parent's children.
     */
    public void removeFromParent() {
        if (parent != null) {
            parent.removeChild(this);
            parent = null;
        }
    }

    /**
     * Remove a child
     *
     * @param child the child to remove
     */
    public void removeChild(TreeNodeSingle<T> child) {
        children.remove(child);
    }
}
