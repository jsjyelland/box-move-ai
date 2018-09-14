package solution;

import static java.lang.Math.PI;
import static java.lang.Math.min;

public class Utility {

    public static double mod(double x, double m) {
        double r = x % m;
        return r < 0 ? r + m : r;
    }

    public static double angleBetween(double angleCounterClockwise, double angleClockwise) {
        return mod(angleClockwise - angleCounterClockwise, 2 * PI);
    }

    public static double smallestAngleBetween(double angle1, double angle2) {
        double ang = angleBetween(angle1, angle2);
        return min(ang, 2 * PI - ang);
    }
}
