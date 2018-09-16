package solution;

/**
 * Indicates no path could be found
 */
public class NoPathException extends Exception {
    public NoPathException(String s, NoRRTOrderException e) {
        super(s, e);
    }
    public NoPathException(String s) {
        super(s);
    }
}
