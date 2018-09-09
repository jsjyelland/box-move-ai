package solution;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A visualisation of the solution
 */
public class Visualiser extends JComponent {
    /**
     * The static obstacles
     */
    private Box[] staticObstacles;

    /**
     * The tree
     */
    private TreeNode<State> tree;

    /**
     * The solution node (a leaf of the tree)
     */
    private TreeNode<State> solutionNode;

    /**
     * Transform to convert the [0, 1] * [0, 1] size of the workspace to the window size
     */
    private AffineTransform transform;

    /**
     * Create a visualiser with static obstacles
     * @param staticObstacles the static obstacles in the workspace
     */
    public Visualiser(Box[] staticObstacles) {
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        this.staticObstacles = staticObstacles;
        this.tree = null;
        this.solutionNode = null;
        repaint();
    }

    /**
     * Paint a tree
     * @param tree the tree to paint
     */
    public void paintTree(TreeNode<State> tree) {
        this.tree = tree;
        repaint();
    }

    /**
     * Redraw the panel
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

        // Draw all the static obstacles
        g2.setColor(Color.BLACK);
        for (Box obstacle : staticObstacles) {
            Shape shape = transform.createTransformedShape(obstacle.getRect());
            g2.fill(shape);
        }

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
     * @param node the node to draw
     * @param g2 graphics to paint into
     */
    private void paintTreeNode(TreeNode<State> node, Graphics2D g2) {
        paintTreeNode(node, g2, 0, 0, false);
    }

    /**
     * Recursive tree node drawing function. Draws a node and any of its children. Also draws a line to its parent
     * @param node the node to draw
     * @param g2 graphics to paint into
     * @param lastX parent node's x position
     * @param lastY parent node's y position
     * @param drawLine whether to draw a line to the parent or not
     */
    private void paintTreeNode(TreeNode<State> node, Graphics2D g2, int lastX, int lastY, boolean drawLine) {
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));

        // Transform the shape
        Shape transformedShape = transform.createTransformedShape(node.getValue().goalBox.getRect());

        int nodeX = (int) transformedShape.getBounds().getX();
        int nodeY = (int) transformedShape.getBounds().getY();

        // Draw the point
        g2.fillRect(nodeX, nodeY, 5, 5);

        // Draw the line to the parent
        if (drawLine) {
            g2.drawLine(nodeX, nodeY, lastX, lastY);
        }

        // Call this function on any children of the node
        for (int i = 0; i < node.getChildren().size(); i++) {
            paintTreeNode(node.getChildren().get(i), g2, nodeX, nodeY, true);
        }
    }

    /**
     * Draw a solution path. Helper function to begin recursion. Draws a line from the solution leaf node to the root
     * @param node the solution leaf node
     * @param g2 graphics to paint into
     */
    private void paintSolutionNode(TreeNode<State> node, Graphics2D g2) {
        paintSolutionNode(node, g2, 0, 0, false);
    }

    /**
     * Recursive solution path drawing function. Draws a line from the current node to its parent
     * @param node current node to draw
     * @param g2 graphics to paint into
     * @param lastX x position of the last node
     * @param lastY y position of the last node
     * @param drawLine whether to draw a line to the last node or not
     */
    private void paintSolutionNode(TreeNode<State> node, Graphics2D g2, int lastX, int lastY, boolean drawLine) {
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(5));

        // Transform the shape
        Shape transformedShape = transform.createTransformedShape(node.getValue().goalBox.getRect());

        int nodeX = (int) transformedShape.getBounds().getX();
        int nodeY = (int) transformedShape.getBounds().getY();

        // Draw a line to the last node
        if (drawLine) {
            g2.drawLine(nodeX, nodeY, lastX, lastY);
        }

        // Call this function on the parent
        if (node.getParent() != null) {
            paintSolutionNode(node.getParent(), g2, nodeX, nodeY, true);
        }
    }

    public void paintSolution(TreeNode<State> solutionNode) {
        this.solutionNode = solutionNode;
        repaint();
    }
}
