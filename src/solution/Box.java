package solution;

import java.awt.geom.Rectangle2D;

public class Box {
    protected Rectangle2D rect;

    public Box(double x, double y, double w, double h) {
        rect = new Rectangle2D.Double(x, y, w, h);
    }

    public Box(Rectangle2D rect) {
        this.rect = (Rectangle2D) rect.clone();
    }

    public boolean intersects(Box other) {
        return rect.intersects(other.rect);
    }
}
