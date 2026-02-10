package org.firstinspires.ftc.teamcode.localization;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.hardware.OdometryPod;
import org.firstinspires.ftc.teamcode.config.HardwareConfig;
import org.firstinspires.ftc.teamcode.config.RobotConstants;

/**
 * Computes the robot's pose using encoder odometry pods. Optionally integrate IMU and vision for improved accuracy.
 */
public class OdometryLocalizer {
    private final OdometryPod podX;
    private final OdometryPod podY;
    private final OdometryPod podZ; // optional
    private double lastX;
    private double lastY;
    private double lastZ;
    private Pose2d pose;

    public OdometryLocalizer(HardwareMap hardwareMap) {
        DcMotorEx motorX = hardwareMap.get(DcMotorEx.class, HardwareConfig.ODOMETRY_X);
        DcMotorEx motorY = hardwareMap.get(DcMotorEx.class, HardwareConfig.ODOMETRY_Y);
        podX = new OdometryPod(motorX);
        podY = new OdometryPod(motorY);
        // Z can be null if not used
        DcMotorEx motorZ = null;
        try {
            motorZ = hardwareMap.get(DcMotorEx.class, HardwareConfig.ODOMETRY_Z);
        } catch (Exception e) {
            motorZ = null;
        }
        podZ = motorZ != null ? new OdometryPod(motorZ) : null;
        pose = new Pose2d(0, 0, 0);
    }

    public void reset() {
        podX.reset();
        podY.reset();
        if (podZ != null) podZ.reset();
        lastX = lastY = lastZ = 0;
        pose = new Pose2d(0, 0, 0);
    }

    public Pose2d update() {
        double xTicks = podX.getTicks();
        double yTicks = podY.getTicks();
        double zTicks = podZ != null ? podZ.getTicks() : 0;
        double dx = (xTicks - lastX) * Math.PI * RobotConstants.ODOMETRY_WHEEL_DIAMETER_INCHES / RobotConstants.ODOMETRY_TICKS_PER_REV;
        double dy = (yTicks - lastY) * Math.PI * RobotConstants.ODOMETRY_WHEEL_DIAMETER_INCHES / RobotConstants.ODOMETRY_TICKS_PER_REV;
        double dHeading = 0;
        if (podZ != null) {
            dHeading = (zTicks - lastZ) * Math.PI * RobotConstants.ODOMETRY_WHEEL_DIAMETER_INCHES / RobotConstants.ODOMETRY_TICKS_PER_REV;
        }
        lastX = xTicks;
        lastY = yTicks;
        lastZ = zTicks;
        pose.x += dx;
        pose.y += dy;
        pose.heading += dHeading;
        return pose;
    }
}
