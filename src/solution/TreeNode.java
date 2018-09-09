package solution;

import java.util.ArrayList;

/**
 * A node in a tree. Holds a value
 * @param <T> The class of the value object the tree node holds
 */
public class TreeNode<T> {
    /**
     * List of children connected to this node
     */
    private ArrayList<TreeNode<T>> children;

    /**
     * The parent in the tree
     */
    private TreeNode<T> parent;

    /**
     * The value held by the node
     */
    private T value;

    /**
     * Construct a tree with a value
     * @param value the value to hold
     */
    TreeNode(T value) {
        children = new ArrayList<>();
        this.value = value;
        parent = null;
    }

    /**
     * Add a child to this node
     * @param t the node to add to this one
     */
    void addChild(TreeNode<T> t) {
        t.setParent(this);
        children.add(t);
    }

    /**
     * Set the parent of this node
     * @param parent the parent to set
     */
    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    /**
     * Get the parent of this node
     * @return the parent of this node
     */
    public TreeNode<T> getParent() {
        return parent;
    }

    /**
     * Get the value of this node
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * A string representation of the tree, assuming this node is a root.
     * @return the string representation
     */
    @Override
    public String toString() {
        return print("", true, true);
    }

    /**
     * Convert this node into a string. Helper function for toString().
     * Will return the string representation of the value, as well as list all it's
     * children underneath.
     * @param prefix a prefix string to put before all outputted lines.
     * Allows for indenting if this node is a child
     * @param isTail if this is the last child node
     * @param isStart if this is the root
     * @return the string representation of this node
     */
    private String print(String prefix, boolean isTail, boolean isStart) {
        String out = (isStart ? "" : prefix + (isTail ? "└── " : "├── ")) + value + "\n";

        int counter = 0;
        for (TreeNode<T> child : children) {
            if (counter < children.size() - 1) {
                out += child.print((isStart ? "" : prefix + (isTail ? "    " : "│   ")), false, false);
            } else {
                out += child.print((isStart ? "" : prefix + (isTail ?"    " : "│   ")), true, false);
            }

            counter++;
        }

        return out;
    }

    /**
     * Get the children of the node
     * @return a list of the children of the node
     */
    public ArrayList<TreeNode<T>> getChildren() {
        return children;
    }

    /**
     * Remove the node from its parent.
     * Also removes this node from the parent's children.
     */
    public void removeFromParent() {
        if (parent != null) {
            parent.removeChild(this);
            parent = null;
        }
    }

    /**
     * Remove a child
     * @param child the child to remove
     */
    public void removeChild(TreeNode<T> child) {
        children.remove(child);
    }
}
