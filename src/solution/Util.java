package solution;

import static java.lang.Math.random;
import static java.lang.Math.round;

/**
 * Helper functions for the assignment
 */
public class Util {
    /**
     * Helper function for random number between two bounds
     * @param a lower bound
     * @param b upper bound
     * @return a random number
     */
    public static int randomBetween(int a, int b) {
        return a + round((float) random() * (float) (b - a));
    }

    /**
     * Helper function for a random number up to a number (from 0)
     * @param a the upper bound
     * @return a random number
     */
    public static int randomTo(int a) {
        return randomBetween(0, a);
    }
}
