package solution;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class RobotActionVisualiser extends JComponent {
    private ArrayList<RobotAction> robotPath;

    protected AffineTransform transform;

    public RobotActionVisualiser(ArrayList<RobotAction> robotPath) {
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        this.robotPath = robotPath;
        repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        // Calculate the transform of the window
        transform = AffineTransform.getScaleInstance(getWidth(), -getHeight());
        transform.concatenate(AffineTransform.getTranslateInstance(0, -1));

        // Fill the background
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

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

        int lastX = -1;
        int lastY = -1;

        for (RobotAction action : robotPath) {
            if (action != null) {
                Shape transformedShape = transform.createTransformedShape(
                        action.getFinalRobot().getLine()
                );

                int nodeX = (int) transformedShape.getBounds().getCenterX();
                int nodeY = (int) transformedShape.getBounds().getCenterY();

                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(5));

                // Draw the goal node
                g2.draw(transformedShape);

                // Draw a line to the last node
                if (lastX != -1 && lastY != -1) {
                    g2.setColor(Color.GREEN);
                    g2.setStroke(new BasicStroke(1));
                    g2.drawLine(nodeX, nodeY, lastX, lastY);
                }

                lastX = nodeX;
                lastY = nodeY;
            }
        }
    }
}
