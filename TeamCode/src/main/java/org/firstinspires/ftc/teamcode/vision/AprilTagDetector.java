package org.firstinspires.ftc.teamcode.vision;

/**
 * Stub for AprilTag detection logic if using a camera other than HuskyLens.
 * The HuskyLens handles tag detection internally; this class is provided for completeness.
 */
public class AprilTagDetector {
    public static class Detection {
        public int id;
        public double x;
        public double y;
        public double z;
        public double yaw;
        public double pitch;
        public double roll;
    }

    public void init() {
        // TODO: initialize tag detection pipeline if needed
    }

    public Detection[] getDetections() {
        // TODO: return detected AprilTags
        return new Detection[0];
    }
}
