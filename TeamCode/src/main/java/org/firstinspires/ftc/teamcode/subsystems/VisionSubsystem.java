package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.HuskyLensSensor;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;

/**
 * Handles vision processing using the HuskyLens sensor and AprilTag detection.
 */
public class VisionSubsystem implements Subsystem {
    private HuskyLensSensor huskyLens;

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        I2cDeviceSynch i2cDevice = hardwareMap.get(I2cDeviceSynch.class, "huskyLens"); // TODO: use HardwareConfig.HUSKYLENS_I2C
        huskyLens = new HuskyLensSensor(i2cDevice);
        huskyLens.init();
    }

    @Override
    public void start() {
        // Start any needed background tasks
    }

    @Override
    public void update() {
        // Read detections and update shared pose/target info
        // TODO: integrate with localization and path planning
        Object detections = huskyLens.readDetections();
        // Process detections
    }

    @Override
    public void stop() {
        // Cleanup if necessary
    }
}
