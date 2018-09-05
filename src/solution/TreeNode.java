package solution;

import java.util.ArrayList;

public class TreeNode<T> {
    private ArrayList<TreeNode<T>> children;
    private TreeNode<T> parent;
    private T value;

    TreeNode(T value) {
        children = new ArrayList<>();
        this.value = value;
        parent = null;
    }

    void addChild(TreeNode<T> t) {
        t.setParent(this);
        children.add(t);
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return print("", true, true);
    }

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

    public ArrayList<TreeNode<T>> getChildren() {
        return children;
    }

    public void removeFromParent() {
        if (parent != null) {
            parent.removeChild(this);
            parent = null;
        }
    }

    public void removeChild(TreeNode<T> child) {
        children.remove(child);
    }
}
