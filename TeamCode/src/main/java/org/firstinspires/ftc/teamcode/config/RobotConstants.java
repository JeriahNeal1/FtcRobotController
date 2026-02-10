package org.firstinspires.ftc.teamcode.config;

/**
 * RobotConstants holds field measurements, robot dimensions, and tunable constants.
 * Modify these values as you calibrate your robot.
 */
public final class RobotConstants {
    // Wheel base dimensions (inches)
    public static final double TRACK_WIDTH = 15.0; // TODO: update with actual measurement
    public static final double WHEEL_BASE  = 15.0; // TODO

    // Encoder ticks per revolution and wheel diameter (for odometry calculations)
    public static final double ODOMETRY_TICKS_PER_REV = 8192; // TODO: update for REV Through-Bore
    public static final double ODOMETRY_WHEEL_DIAMETER_INCHES = 2.0; // TODO

    // Shooter speed constants
    public static final double SHOOTER_RPM = 3000.0; // TODO: tune to match performance

    private RobotConstants() {
        // Prevent instantiation
    }
}
