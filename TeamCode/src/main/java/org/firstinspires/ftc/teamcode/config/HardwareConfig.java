package org.firstinspires.ftc.teamcode.config;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * HardwareConfig stores the names and initialization logic for all robot hardware.
 * Update the constants here to match your Control Hub configuration.
 */
public class HardwareConfig {
    // Motor names as defined in the Robot Controller configuration file
    public static final String LEFT_FRONT_MOTOR = "leftFrontMotor"; // TODO: update with actual config name
    public static final String LEFT_REAR_MOTOR  = "leftRearMotor";  // TODO
    public static final String RIGHT_FRONT_MOTOR = "rightFrontMotor"; // TODO
    public static final String RIGHT_REAR_MOTOR  = "rightRearMotor";  // TODO

    public static final String INTAKE_MOTOR = "intakeMotor"; // TODO
    public static final String SHOOTER_MOTOR = "shooterMotor"; // TODO
    // Sensor names
    public static final String HUSKYLENS_I2C = "huskyLens"; // TODO
    public static final String ODOMETRY_X = "odometryX"; // TODO
    public static final String ODOMETRY_Y = "odometryY"; // TODO
    public static final String ODOMETRY_Z = "odometryZ"; // optional third pod if used

    private HardwareMap hardwareMap;

    public HardwareConfig(HardwareMap hardwareMap) {
        this.hardwareMap = hardwareMap;
    }

    /**
     * Initialize all hardware devices here.
     * You should obtain references from hardwareMap and configure each device.
     */
    public void init() {
        // TODO: hardware initialization
    }
}
