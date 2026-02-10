package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.teamcode.hardware.HuskyLensSensor;

/**
 * Processes raw HuskyLens sensor data into usable targets, such as tag IDs and bounding boxes.
 */
public class HuskyLensProcessor {
    private final HuskyLensSensor sensor;

    public HuskyLensProcessor(HuskyLensSensor sensor) {
        this.sensor = sensor;
    }

    public void update() {
        // TODO: read sensor data and update targets
    }

    public Object[] getTargets() {
        // TODO: return processed targets
        return new Object[0];
    }
}
