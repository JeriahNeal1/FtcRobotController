package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.localization.OdometryLocalizer;
import org.firstinspires.ftc.teamcode.localization.Pose2d;

/**
 * Responsible for estimating the robot's pose on the field using odometry and vision.
 */
public class LocalizationSubsystem implements Subsystem {
    private OdometryLocalizer odometryLocalizer;
    private Pose2d currentPose;

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        odometryLocalizer = new OdometryLocalizer(hardwareMap);
        currentPose = new Pose2d(0, 0, 0);
    }

    @Override
    public void start() {
        odometryLocalizer.reset();
    }

    @Override
    public void update() {
        currentPose = odometryLocalizer.update();
        // TODO: fuse vision data here if available
    }

    @Override
    public void stop() {
        // nothing specific
    }

    public Pose2d getPose() {
        return currentPose;
    }
}
