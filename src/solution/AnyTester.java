package solution;

public class AnyTester {
    public static void main(String[] args) {
        Robot r1 = new Robot(0.4367831838248862, 0.5704782444933671, 3.497214999612419, 0.04);
        Robot r2 = new Robot(0.43751826308273634, 0.5700657067667001, 3.474814782486634, 0.04);
        System.out.println(r2.distanceToOtherRobot(r1));
    }
}
