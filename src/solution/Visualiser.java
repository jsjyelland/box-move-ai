package solution;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * A visualisation of the solution
 * @param <T> the class of state
 * @param <U> the class of action
 */
public abstract class Visualiser<T extends State, U> extends JComponent {
    /**
     * The tree
     */
    protected TreeNode<T, U> tree;

    /**
     * The solution node (a leaf of the tree)
     */
    protected TreeNode<T, U> solutionNode;

    /**
     * Transform to convert the [0, 1] * [0, 1] size of the workspace to the window size
     */
    protected AffineTransform transform;

    /**
     * Create a visualiser
     */
    public Visualiser() {
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        this.tree = null;
        this.solutionNode = null;
        repaint();
    }

    /**
     * Paint a tree
     *
     * @param tree the tree to paint
     */
    public void paintTree(TreeNode<T, U> tree) {
        this.tree = tree;
        repaint();
    }

    /**
     * Redraw the panel
     *
     * @param graphics graphics component to draw in
     */
    @Override
    public void paintComponent(Graphics graphics) {
        // Calculate the transform of the window
        transform = AffineTransform.getScaleInstance(getWidth(), -getHeight());
        transform.concatenate(AffineTransform.getTranslateInstance(0, -1));

        // Fill the background
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw the tree
        if (tree != null) {
            paintTreeNode(tree, g2);
        }

        // Draw the solution
        if (solutionNode != null) {
            paintSolutionNode(solutionNode, g2);
        }
    }

    /**
     * Draw a tree node. Helper function to begin recursion
     *
     * @param node the node to draw
     * @param g2 graphics to paint into
     */
    protected abstract void paintTreeNode(TreeNode<T, U> node, Graphics2D g2);

    /**
     * Recursive tree node drawing function. Draws a node and any of its children. Also draws a line
     * to its parent
     *
     * @param node the node to draw
     * @param g2 graphics to paint into
     * @param lastX parent node's x position
     * @param lastY parent node's y position
     * @param drawLine whether to draw a line to the parent or not
     */
    protected abstract void paintTreeNode(TreeNode<T, U> node, Graphics2D g2, int lastX, int lastY,
            boolean drawLine);

    /**
     * Draw a solution path. Helper function to begin recursion. Draws a line from the solution leaf
     * node to the root
     *
     * @param node the solution leaf node
     * @param g2 graphics to paint into
     */
    protected abstract void paintSolutionNode(TreeNode<T, U> node, Graphics2D g2);

    /**
     * Recursive solution path drawing function. Draws a line from the current node to its parent
     *
     * @param node current node to draw
     * @param g2 graphics to paint into
     * @param lastX x position of the last node
     * @param lastY y position of the last node
     * @param drawLine whether to draw a line to the last node or not
     */
    protected abstract void paintSolutionNode(TreeNode<T, U> node, Graphics2D g2, int lastX,
            int lastY, boolean drawLine);

    /**
     * Draw a solution to the root of the tree.
     *
     * @param solutionNode the leaf node to start from
     */
    public void paintSolution(TreeNode<T, U> solutionNode) {
        this.solutionNode = solutionNode;
        repaint();
    }
}
