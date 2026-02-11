package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Centralized hardware configuration and shared setup logic.
 * Change hardware names, directions, or defaults here once,
 * and all OpModes that use this class automatically inherit changes.
 */
public final class Configuration {
    private Configuration() {}

    // -------------------------
    // Hardware Device Names (match the Robot Controller configuration)
    // -------------------------

    // Drive motors
    public static final String LEFT_FRONT_MOTOR = "left_front_motor";
    public static final String LEFT_BACK_MOTOR = "left_back_motor";
    public static final String RIGHT_FRONT_MOTOR = "right_front_motor";
    public static final String RIGHT_BACK_MOTOR = "right_back_motor";

    // Rollers
    public static final String INTAKE_ROLLER = "intake_roller";
    public static final String OUTTAKE_ROLLER = "outtake_roller";

    // HuskyLens
    public static final String HUSKYLENS = "huskylens";

    // Odometry pods
    public static final String ODOMETRY_X = "odoX";
    public static final String ODOMETRY_Y = "odoY";
    public static final String ODOMETRY_Z = "odoZ";

    // Single drive encoder source used now for coarse distance checks.
    public static final String PRIMARY_DRIVE_ENCODER_MOTOR = LEFT_FRONT_MOTOR;

    // -------------------------
    // Robot Hardware Container
    // -------------------------
    public static class RobotHardware {
        public DcMotorEx leftFrontMotor;
        public DcMotorEx leftBackMotor;
        public DcMotorEx rightFrontMotor;
        public DcMotorEx rightBackMotor;
        public DcMotorEx intakeRoller;
        public DcMotorEx outtakeRoller;
    }

    public static RobotHardware initHardware(HardwareMap hardwareMap) {
        RobotHardware robot = new RobotHardware();

        // Initialize drive motors.
        robot.leftFrontMotor = hardwareMap.get(DcMotorEx.class, LEFT_FRONT_MOTOR);
        robot.leftBackMotor = hardwareMap.get(DcMotorEx.class, LEFT_BACK_MOTOR);
        robot.rightFrontMotor = hardwareMap.get(DcMotorEx.class, RIGHT_FRONT_MOTOR);
        robot.rightBackMotor = hardwareMap.get(DcMotorEx.class, RIGHT_BACK_MOTOR);

        // Initialize rollers.
        robot.intakeRoller = hardwareMap.get(DcMotorEx.class, INTAKE_ROLLER);
        robot.outtakeRoller = hardwareMap.get(DcMotorEx.class, OUTTAKE_ROLLER);

        // Set motor directions (adjust as needed for your robot).
        robot.leftFrontMotor.setDirection(DcMotor.Direction.FORWARD);
        robot.leftBackMotor.setDirection(DcMotor.Direction.FORWARD);
        robot.rightFrontMotor.setDirection(DcMotor.Direction.REVERSE);
        robot.rightBackMotor.setDirection(DcMotor.Direction.REVERSE);

        return robot;
    }
}
