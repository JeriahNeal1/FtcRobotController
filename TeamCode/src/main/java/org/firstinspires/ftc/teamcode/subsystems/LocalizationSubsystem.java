package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Configuration;

public class LocalizationSubsystem implements Subsystem {
    // TODO: Measure and replace with calibrated pod ticks-per-inch.
    private static final double ODOMETRY_TICKS_PER_INCH = 1.0;

    public static class PoseEstimate {
        public double xInches;
        public double yInches;
        public double headingRadians;
    }

    private final PoseEstimate poseEstimate = new PoseEstimate();
    private Telemetry telemetry;

    private DcMotorEx odometryXPod;
    private DcMotorEx odometryYPod;
    private DcMotorEx odometryZPod;

    private boolean hasTwoPods;
    private boolean hasThirdPod;

    private int lastXTicks;
    private int lastYTicks;
    private int lastZTicks;

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        // Current robot state: two odometry pods are installed (X and Y), third pod is planned later.
        // IMU availability is not confirmed in this revision, so heading remains a placeholder.
        try {
            odometryXPod = hardwareMap.get(DcMotorEx.class, Configuration.ODOMETRY_X);
            odometryYPod = hardwareMap.get(DcMotorEx.class, Configuration.ODOMETRY_Y);
            hasTwoPods = true;
        } catch (IllegalArgumentException ignored) {
            hasTwoPods = false;
        }

        try {
            odometryZPod = hardwareMap.get(DcMotorEx.class, Configuration.ODOMETRY_Z);
            hasThirdPod = true;
        } catch (IllegalArgumentException ignored) {
            hasThirdPod = false;
        }

        if (hasTwoPods) {
            configurePod(odometryXPod);
            configurePod(odometryYPod);
            lastXTicks = odometryXPod.getCurrentPosition();
            lastYTicks = odometryYPod.getCurrentPosition();
        }

        if (hasThirdPod) {
            configurePod(odometryZPod);
            lastZTicks = odometryZPod.getCurrentPosition();
        }
    }

    private void configurePod(DcMotorEx pod) {
        pod.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        pod.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void start() {
        // No warm-up required.
    }

    @Override
    public void update() {
        if (!hasTwoPods) {
            return;
        }

        int currentX = odometryXPod.getCurrentPosition();
        int currentY = odometryYPod.getCurrentPosition();

        int deltaXTicks = currentX - lastXTicks;
        int deltaYTicks = currentY - lastYTicks;

        lastXTicks = currentX;
        lastYTicks = currentY;

        poseEstimate.xInches += deltaXTicks / ODOMETRY_TICKS_PER_INCH;
        poseEstimate.yInches += deltaYTicks / ODOMETRY_TICKS_PER_INCH;

        if (hasThirdPod) {
            int currentZ = odometryZPod.getCurrentPosition();
            int deltaZTicks = currentZ - lastZTicks;
            lastZTicks = currentZ;
            // TODO: Convert third-pod delta ticks into heading and fuse with IMU when available.
            poseEstimate.headingRadians += deltaZTicks * 0.0;
        } else {
            // TODO: Compute heading from IMU once sensor availability is confirmed and integrated.
            poseEstimate.headingRadians += 0.0;
        }
    }

    public PoseEstimate getPoseEstimate() {
        PoseEstimate snapshot = new PoseEstimate();
        snapshot.xInches = poseEstimate.xInches;
        snapshot.yInches = poseEstimate.yInches;
        snapshot.headingRadians = poseEstimate.headingRadians;
        return snapshot;
    }

    public String getStatus() {
        if (hasTwoPods) {
            return "2 pods installed; 3rd supported";
        }
        return "Odometry pods not mapped; placeholder mode";
    }

    @Override
    public void stop() {
        if (telemetry != null) {
            telemetry.addData("LocalizationSubsystem", "Stopped");
        }
    }
}
