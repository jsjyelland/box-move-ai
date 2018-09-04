package solution;

import java.awt.geom.Rectangle2D;

public class MoveableBox extends Box {
    public MoveableBox(double x, double y, double w) {
        super(x, y, w, w);
    }

    public MoveableBox(Rectangle2D rect) {
        super(rect);
    }


    public void move(double dx, double dy) {
        rect.setRect(rect.getX() + dx, rect.getY() + dy, rect.getWidth(), rect.getHeight());
    }

    public MoveableBox clone() {
        return new MoveableBox(rect);
    }
}
