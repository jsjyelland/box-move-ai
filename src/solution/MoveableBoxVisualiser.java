package solution;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.BasicStroke;

/**
 * Visualiser for a movable box path
 */
public class MoveableBoxVisualiser extends Visualiser<MoveableBoxState, MoveableBoxAction> {
    /**
     * Draw a tree node. Helper function to begin recursion
     *
     * @param node the node to draw
     * @param g2 graphics to paint into
     */
    @Override
    protected void paintTreeNode(TreeNode<MoveableBoxState, MoveableBoxAction> node,
            Graphics2D g2) {
        // Draw all the static obstacles
        g2.setColor(Color.BLACK);
        for (Box obstacle : Workspace.getInstance().getStaticObstacles()) {
            Shape shape = transform.createTransformedShape(obstacle.getRect());
            g2.fill(shape);
        }

        // Draw all the moveable obstacles
        g2.setColor(Color.GREEN);
        for (Box obstacle : Workspace.getInstance().getMoveableObstacles()) {
            Shape shape = transform.createTransformedShape(obstacle.getRect());
            g2.fill(shape);
        }

        paintTreeNode(node, g2, 0, 0, false);
    }

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
    @Override
    protected void paintTreeNode(TreeNode<MoveableBoxState, MoveableBoxAction> node,
            Graphics2D g2, int lastX, int lastY, boolean drawLine) {
        g2.setColor(drawLine ? Color.BLACK : Color.BLUE);
        g2.setStroke(new BasicStroke(drawLine ? 1 : 5));

        // Transform the shape
        Shape transformedShape = transform.createTransformedShape(
                node.getState().getMainBox().getRect()
        );

        int nodeX = (int) transformedShape.getBounds().getX();
        int nodeY = (int) transformedShape.getBounds().getY();

        // Draw the point
        g2.draw(transformedShape);

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
     * Draw a solution path. Helper function to begin recursion. Draws a line from the solution leaf
     * node to the root
     *
     * @param node the solution leaf node
     * @param g2 graphics to paint into
     */
    @Override
    protected void paintSolutionNode(TreeNode<MoveableBoxState, MoveableBoxAction> node,
            Graphics2D g2) {
        g2.setColor(Color.MAGENTA);

        // Draw all the static obstacles
        g2.setColor(Color.BLACK);
        for (Box obstacle : Workspace.getInstance().getStaticObstacles()) {
            Shape shape = transform.createTransformedShape(obstacle.getRect());
            g2.fill(shape);
        }

        // Draw all the moveable obstacles
        g2.setColor(Color.GREEN);
        for (Box obstacle : Workspace.getInstance().getMoveableObstacles()) {
            Shape shape = transform.createTransformedShape(obstacle.getRect());
            g2.fill(shape);
        }

        // Transform the final position
        Shape transformedShape = transform.createTransformedShape(
                node.getState().getMainBox().getRect()
        );

        // Draw the final position
        g2.fill(transformedShape);

        // Draw up the tree
        paintSolutionNode(node, g2, 0, 0, false);
    }

    /**
     * Recursive solution path drawing function. Draws a line from the current node to its parent
     *
     * @param node current node to draw
     * @param g2 graphics to paint into
     * @param lastX x position of the last node
     * @param lastY y position of the last node
     * @param drawLine whether to draw a line to the last node or not
     */
    @Override
    protected void paintSolutionNode(TreeNode<MoveableBoxState, MoveableBoxAction> node,
            Graphics2D g2, int lastX, int lastY, boolean drawLine) {
        // Transform the shape
        Shape transformedShape = transform.createTransformedShape(
                node.getState().getMainBox().getRect()
        );

        int nodeX = (int) transformedShape.getBounds().getX();
        int nodeY = (int) transformedShape.getBounds().getY();

        if (node.getParent() == null) {
            // No parent. fill the initial position
            g2.setColor(Color.YELLOW);
            g2.fill(transformedShape);
        }

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(5));

        // Draw the goal node
        g2.draw(transformedShape);

        // Draw a line to the last node
        if (drawLine) {
            g2.drawLine(nodeX, nodeY, lastX, lastY);
        }

        // Call this function on the parent
        if (node.getParent() != null) {
            paintSolutionNode(node.getParent(), g2, nodeX, nodeY, true);
        }
    }
}
