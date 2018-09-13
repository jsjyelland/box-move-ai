package solution;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import static java.lang.Math.*;

/**
 * A state object, containing the location and orientation of the robot.
 */
public class RobotState extends State {
    /**
     * The robot
     */
    private Robot robot;

    /**
     * Construct a new robot state
     *
     * @param robot the robot
     * @param staticObstacles the static obstacles to check collision with
     */
    public RobotState(Robot robot, ArrayList<Box> staticObstacles) {
        super(staticObstacles);
        this.robot = robot;
    }

    /**
     * Moving from one state to another.
     *
     * @param dx x distance to move robot by
     * @param dy y distance to move robot by
     * @param newTheta the new theta value for the robot to move to
     *
     * @return a new node containing the new state and the action to get to this state
     *
     * @throws InvalidStateException if the new state is invalid
     */
    public TreeNode<RobotState, RobotAction> action(double dx, double dy, double newTheta)
            throws InvalidStateException {
        // Clone this state
        RobotState newState = clone();
        newState.robot.move(dx, dy, 0);

        // Create a parallelogram to represent the movement of the robot
        Path2D union = new Path2D.Double();
        union.moveTo(robot.getX1(), robot.getY1());
        union.lineTo(robot.getX2(), robot.getY2());
        union.lineTo(newState.robot.getX2(), newState.robot.getY2());
        union.lineTo(newState.robot.getX1(), newState.robot.getY1());
        union.closePath();
        Area unionArea = new Area(union);

        // Should the robot rotate before moving?
        boolean rotateFirst = true;

        newState.robot.move(0, 0, newTheta - newState.robot.getTheta());

        // Create circles to encapsulate the rotation
        Ellipse2D oldCircle = robot.getCircleBounds();
        Ellipse2D newCircle = newState.robot.getCircleBounds();

        // Check the movement given the static obstacles
        for (Box box : staticObstacles) {
            if (box instanceof MoveableBox) {
                // This is the box we're pushing. Need to allow the robot to be on the edge
                MoveableBox moveableBox = (MoveableBox) box;
                if (moveableBox.pointStrictlyInside(robot.getP1()) ||
                            moveableBox.pointStrictlyInside(robot.getP2())) {
                    throw new InvalidStateException();
                }
            } else {
                // Regular static obstacles
                if (unionArea.intersects(box.getRect())) {
                    throw new InvalidStateException();
                }
            }

            if (oldCircle.intersects(box.getRect()) && rotateFirst) {
                // Can't rotate first. Try rotating second.
                rotateFirst = false;
            }

            if (newCircle.intersects(box.getRect()) && !rotateFirst) {
                // Can't rotate first or second. Invalid state.
                throw new InvalidStateException();
            }
        }

        // Create and return a new node with this new state and action
        return new TreeNode<>(newState, new RobotAction(robot, newState.robot, rotateFirst));
    }

    /**
     * Check if the state is valid. The state is valid if the robot doesn't collide with any of the
     * static obstacles and is inside the workspace.
     *
     * @return whether the state is valid or not
     */
    @Override
    public boolean isValid() {
        // Check if the robot is valid
        return robot.isValid(staticObstacles);
    }

    /**
     * Clone the state
     *
     * @return the cloned state
     */
    @Override
    public RobotState clone() {
        return new RobotState(robot.clone(), new ArrayList<>(staticObstacles));
    }

    /**
     * Calculate the distance of a change in x, y, and theta. This represents maximum distance the
     * ends of the robot will have to move
     *
     * @param dx change in x
     * @param dy change in y
     * @param newTheta new value of theta
     *
     * @return the distance
     */
    private double distanceWithNewTheta(double dx, double dy, double newTheta) {
        double deltaCos = cos(newTheta) - cos(robot.getTheta());
        double deltaSin = sin(newTheta) - sin(robot.getTheta());
        return sqrt(max(pow(dx - robot.getWidth() * deltaCos, 2) + pow(dy - robot.getWidth() * deltaSin, 2),
                pow(dx + robot.getWidth() * deltaCos, 2) + pow(dy + robot.getWidth() * deltaSin, 2)));
    }

    /**
     * Calculate the distance to another state. Is the euclidean distance on a 3D graph with axes x,
     * y, theta.
     *
     * @param other the other state to calculate the distance to
     *
     * @return the distance between states if other is a RobotState. -1 otherwise.
     */
    @Override
    public double distanceTo(State other) {
        if (other instanceof RobotState) {
            RobotState robotState = (RobotState) other;

            return distanceWithNewTheta(
                    robot.getPos().getX() - robotState.getRobot().getPos().getX(),
                    robot.getPos().getY() - robotState.getRobot().getPos().getY(),
                    robotState.getRobot().getTheta()
            );
        } else {
            return -1;
        }
    }

    /**
     * Create a new state that is at most delta distance along the line between this state and a new
     * one. Distance means the euclidean distance on a 3D graph with axes x, y, theta.
     *
     * @param other the other state
     * @param delta the distance to move along the line between the two states
     *
     * @return the new state
     *
     * @throws InvalidStateException if other is not a RobotState
     */
    @Override
    public RobotState stepTowards(State other, double delta) throws InvalidStateException {
        // Make sure other is a RobotState
        if (!(other instanceof RobotState)) {
            throw new InvalidStateException();
        }

        RobotState robotState = (RobotState) other;

        // Distance to the other state
        double distance = distanceTo(other);

        // No need to do anything
        if (distance <= delta) {
            return robotState;
        }

        RobotState newState = clone();

        // Move along the line between this state and other by amount delta
        Robot newRobot = new Robot(
                robot.getX() + (delta / distance) * (robotState.getRobot().getX() - robot.getX()),
                robot.getY() + (delta / distance) * (robotState.getRobot().getY() - robot.getY()),
                robot.getTheta() + (delta / distance) * (robotState.getRobot().getTheta() -
                                                                 robot.getTheta()),
                robot.getWidth()
        );

        newState.setRobot(newRobot);

        return newState;
    }

    /**
     * Configure a state, given the nearest node in the search tree. Sets the static obstacles from
     * this node. The node must have a state class of RobotState
     *
     * @param nearestNode the nearest node in the search tree
     * @param <T> the class of state
     * @param <U> the class of action
     */
    @Override
    public <T extends State, U> void configure(TreeNode<T, U> nearestNode) {
        if (nearestNode.getState() instanceof RobotState) {
            RobotState state = (RobotState) nearestNode.getState();
            setStaticObstacles(state.getStaticObstacles());
        }
    }

    /**
     * Get the robot
     *
     * @return the robot
     */
    public Robot getRobot() {
        return robot;
    }

    /**
     * Set the robot
     *
     * @param robot the new robot
     */
    public void setRobot(Robot robot) {
        this.robot = robot;
    }
}
