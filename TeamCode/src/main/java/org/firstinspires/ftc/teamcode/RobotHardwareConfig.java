package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;

/**
 * Centralized robot hardware mapping, tunable constants, and shared math helpers.
 */
@Configurable
public class RobotHardwareConfig {
    // Hardware names
    public static final String TURRET_MOTOR_NAME = "turret";
    public static final String LIMELIGHT_NAME = "limelight";
    public static final String FRONT_LEFT_MOTOR_NAME = "frontleft";
    public static final String FRONT_RIGHT_MOTOR_NAME = "frontright";
    public static final String BACK_LEFT_MOTOR_NAME = "backleft";
    public static final String BACK_RIGHT_MOTOR_NAME = "backright";
    public static final String INTAKE_MOTOR_NAME = "intake";
    public static final String TOP_FLYWHEEL_MOTOR_NAME = "topflywheel";
    public static final String BOTTOM_FLYWHEEL_MOTOR_NAME = "bottomflywheel";
    public static final String SINGLE_FLYWHEEL_MOTOR_NAME = "flywheel";
    public static final String LEFT_BEAM_STOP_SERVO_NAME = "lbstop";
    public static final String RIGHT_BEAM_STOP_SERVO_NAME = "rbstop";
    public static final String LEFT_HOOD_SERVO_NAME = "lhoodtilt";
    public static final String RIGHT_HOOD_SERVO_NAME = "rhoodtilt";
    public static final String PRISM_NAME = "prism";
    public static final String ODOMETRY_NAME = "odo";

    // Turret constants
    public static double TURRET_GEAR_RADIUS_CM = 8.5;
    public static double TURRET_MOTOR_GEAR_RADIUS_CM = 3.5;
    public static double TURRET_TICKS_PER_MOTOR_REV = 537.7;
    public static double TURRET_KP = 1.0;
    public static double TURRET_JOYSTICK_DEADBAND = 0.2;
    public static double TURRET_MAX_ANGLE_DEG = 170.0;
    public static double TURRET_MIN_ANGLE_DEG = -170.0;

    // Limelight constants
    public static int LIMELIGHT_POLL_RATE_HZ = 100;
    public static int LIMELIGHT_TURRET_PIPELINE = 1;
    public static int LIMELIGHT_BLUE_TELEOP_PIPELINE = 0;
    public static int LIMELIGHT_RED_TELEOP_PIPELINE = 1;

    // Shared shooter / intake constants
    public static double FLYWHEEL_TICKS_PER_REV = 28.0;
    public static double INTAKE_TICKS_PER_REV = 145.1;
    public static double SHOOTER_TRIGGER_DEADZONE = 0.2;
    public static double SHOOTER_HOOD_DOWN = 0.02;
    public static double SHOOTER_MIN_RPM = 0.0;
    public static double SHOOTER_MAX_RPM = 5800.0;
    public static double SHOOTER_MIN_TILT = 0.01;
    public static double SHOOTER_MAX_TILT = 0.55;
    public static double SHOOTER_RPM_AT_1M = 2300.0;
    public static double SHOOTER_RPM_AT_2M = 2650.0;
    public static double SHOOTER_TILT_AT_1M = 0.34;
    public static double SHOOTER_TILT_AT_2M = 0.55;
    public static double SHOOTER_DISTANCE_SCALE_METERS = 1.7;
    public static double SHOOTER_INCHES_PER_METER = 39.3701;
    public static double TELEOP_INTAKE_TARGET_RPM = 450.0;
    public static double TELEOP_INTAKE_REVERSE_RPM = -500.0;

    // Drive auto-align constants used by teleop modes
    public static double GOAL_ALIGN_TARGET_YAW_DEG = 0.8;
    public static double GOAL_ALIGN_KP = 0.024;
    public static double GOAL_ALIGN_KD = 0.01;
    public static double GOAL_ALIGN_KF = 1.2;
    public static double DRIVE_TURN_MULTIPLIER = 0.8;
    public static long TAG_TIMEOUT_MS = 1000;

    // Servo default positions
    public static double BEAM_STOP_OPEN_POSITION = 0.0;
    public static double BEAM_STOP_CLOSED_POSITION = 0.15;

    // Odometry constants
    public static double ODO_X_OFFSET_MM = 16.0;
    public static double ODO_Y_OFFSET_MM = 124.0;

    // Hardware instances
    public DcMotorEx turretMotor;
    public Limelight3A limelight;
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;
    public DcMotorEx intake;
    public DcMotorEx topFlywheel;
    public DcMotorEx bottomFlywheel;
    public DcMotor flywheel;
    public Servo lbstop;
    public Servo rbstop;
    public Servo lhoodtilt;
    public Servo rhoodtilt;
    public GoBildaPrismDriver prism;
    public GoBildaPinpointDriver odo;

    public void initTurret(HardwareMap hardwareMap, boolean resetEncoder) {
        turretMotor = hardwareMap.get(DcMotorEx.class, TURRET_MOTOR_NAME);
        if (resetEncoder) {
            turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    public void initLimelight(HardwareMap hardwareMap, int pipeline) {
        limelight = hardwareMap.get(Limelight3A.class, LIMELIGHT_NAME);
        limelight.setPollRateHz(LIMELIGHT_POLL_RATE_HZ);
        limelight.pipelineSwitch(pipeline);
    }

    public void startLimelight() {
        if (limelight != null) {
            limelight.start();
        }
    }

    public void initDriveTrain(HardwareMap hardwareMap) {
        frontLeft = hardwareMap.get(DcMotor.class, FRONT_LEFT_MOTOR_NAME);
        frontRight = hardwareMap.get(DcMotor.class, FRONT_RIGHT_MOTOR_NAME);
        backLeft = hardwareMap.get(DcMotor.class, BACK_LEFT_MOTOR_NAME);
        backRight = hardwareMap.get(DcMotor.class, BACK_RIGHT_MOTOR_NAME);

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
    }

    public void initIntake(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotorEx.class, INTAKE_MOTOR_NAME);
        intake.setDirection(DcMotor.Direction.REVERSE);
    }

    public void initBeamStopServos(HardwareMap hardwareMap) {
        lbstop = hardwareMap.get(Servo.class, LEFT_BEAM_STOP_SERVO_NAME);
        rbstop = hardwareMap.get(Servo.class, RIGHT_BEAM_STOP_SERVO_NAME);
        rbstop.setDirection(Servo.Direction.REVERSE);
    }

    public void initHoodServos(HardwareMap hardwareMap) {
        lhoodtilt = hardwareMap.get(Servo.class, LEFT_HOOD_SERVO_NAME);
        rhoodtilt = hardwareMap.get(Servo.class, RIGHT_HOOD_SERVO_NAME);
        lhoodtilt.setDirection(Servo.Direction.REVERSE);
    }

    public void initTopBottomFlywheels(HardwareMap hardwareMap, boolean resetEncoders) {
        topFlywheel = hardwareMap.get(DcMotorEx.class, TOP_FLYWHEEL_MOTOR_NAME);
        bottomFlywheel = hardwareMap.get(DcMotorEx.class, BOTTOM_FLYWHEEL_MOTOR_NAME);

        if (resetEncoders) {
            topFlywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            bottomFlywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        topFlywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bottomFlywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        topFlywheel.setDirection(DcMotor.Direction.REVERSE);
    }

    public void initSingleFlywheel(HardwareMap hardwareMap) {
        flywheel = hardwareMap.get(DcMotor.class, SINGLE_FLYWHEEL_MOTOR_NAME);
    }

    public DualPidMotor createFlywheelController(HardwareMap hardwareMap) {
        return new DualPidMotor(hardwareMap, TOP_FLYWHEEL_MOTOR_NAME, BOTTOM_FLYWHEEL_MOTOR_NAME);
    }

    public void initPrism(HardwareMap hardwareMap) {
        prism = hardwareMap.get(GoBildaPrismDriver.class, PRISM_NAME);
    }

    public void initOdometry(HardwareMap hardwareMap) {
        odo = hardwareMap.get(GoBildaPinpointDriver.class, ODOMETRY_NAME);
        odo.setOffsets(ODO_X_OFFSET_MM, ODO_Y_OFFSET_MM, DistanceUnit.MM);
        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odo.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.REVERSED,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );
        odo.resetPosAndIMU();
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double wrapAngleRad(double angleRad) {
        double twoPi = 2.0 * Math.PI;
        angleRad = ((angleRad + Math.PI) % twoPi + twoPi) % twoPi;
        return angleRad - Math.PI;
    }

    public static double turretTicksPerRevolution() {
        return TURRET_TICKS_PER_MOTOR_REV * (TURRET_GEAR_RADIUS_CM / TURRET_MOTOR_GEAR_RADIUS_CM);
    }

    public static double turretTicksPerRadian() {
        return turretTicksPerRevolution() / (2.0 * Math.PI);
    }

    public static double turretTicksToRadians(double ticks) {
        return ticks / turretTicksPerRadian();
    }

    public static double turretRadiansToTicks(double angleRad) {
        return angleRad * turretTicksPerRadian();
    }

    public static double turretMinAngleRad() {
        return Math.toRadians(TURRET_MIN_ANGLE_DEG);
    }

    public static double turretMaxAngleRad() {
        return Math.toRadians(TURRET_MAX_ANGLE_DEG);
    }

    public static double rpmToTicksPerSecond(double rpm, double ticksPerRev) {
        return (rpm * ticksPerRev) / 60.0;
    }

    public static double flywheelRpmToTicksPerSecond(double rpm) {
        return rpmToTicksPerSecond(rpm, FLYWHEEL_TICKS_PER_REV);
    }

    public static double intakeRpmToTicksPerSecond(double rpm) {
        return rpmToTicksPerSecond(rpm, INTAKE_TICKS_PER_REV);
    }

    public static double metersToInches(double meters) {
        return meters * SHOOTER_INCHES_PER_METER;
    }

    public static double estimateDistanceInchesFromTagArea(double tagArea) {
        if (tagArea <= 0.0) {
            return 0.0;
        }
        double distanceMeters = SHOOTER_DISTANCE_SCALE_METERS / Math.sqrt(tagArea);
        return metersToInches(distanceMeters);
    }

    public static double shooterRpmSlope() {
        double oneMeterInches = SHOOTER_INCHES_PER_METER;
        double twoMetersInches = 2.0 * SHOOTER_INCHES_PER_METER;
        return (SHOOTER_RPM_AT_2M - SHOOTER_RPM_AT_1M) / (twoMetersInches - oneMeterInches);
    }

    public static double shooterRpmIntercept() {
        return SHOOTER_RPM_AT_1M - (shooterRpmSlope() * SHOOTER_INCHES_PER_METER);
    }

    public static double shooterTiltSlope() {
        double oneMeterInches = SHOOTER_INCHES_PER_METER;
        double twoMetersInches = 2.0 * SHOOTER_INCHES_PER_METER;
        return (SHOOTER_TILT_AT_2M - SHOOTER_TILT_AT_1M) / (twoMetersInches - oneMeterInches);
    }

    public static double shooterTiltIntercept() {
        return SHOOTER_TILT_AT_1M - (shooterTiltSlope() * SHOOTER_INCHES_PER_METER);
    }

    public static double estimateShooterRpm(double distanceInches) {
        return clamp(
                (shooterRpmSlope() * distanceInches) + shooterRpmIntercept(),
                SHOOTER_MIN_RPM,
                SHOOTER_MAX_RPM
        );
    }

    public static double estimateHoodTilt(double distanceInches) {
        return clamp(
                (shooterTiltSlope() * distanceInches) + shooterTiltIntercept(),
                SHOOTER_MIN_TILT,
                SHOOTER_MAX_TILT
        );
    }
}
