package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.I2cDeviceSynch;

/**
 * HuskyLensSensor provides a basic interface for communicating with the HuskyLens via I2C.
 * Implementations should translate HuskyLens data into usable objects (e.g. tag IDs and positions).
 */
public class HuskyLensSensor {
    private final I2cDeviceSynch i2cDevice;

    public HuskyLensSensor(I2cDeviceSynch i2cDevice) {
        this.i2cDevice = i2cDevice;
    }

    public void init() {
        // TODO: initialize HuskyLens device (set mode, etc.)
    }

    /**
     * Read detected tag or object data from the sensor.
     * @return placeholder for detected data
     */
    public Object readDetections() {
        // TODO: read detection data from HuskyLens
        return null;
    }
}
