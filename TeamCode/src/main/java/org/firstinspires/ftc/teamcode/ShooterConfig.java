package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;

/**
 * Central shooter configuration and migrated legacy shooter tuning helpers.
 */
@Configurable
public final class ShooterConfig {
    private ShooterConfig() {}

    public static final double TWO_PI = 2.0 * Math.PI;

    // Turret geometry and conversion
    public static double TURRET_GEAR_RADIUS_CM = 8.5;
    public static double TURRET_MOTOR_GEAR_RADIUS_CM = 3.5;
    public static double TURRET_TICKS_PER_MOTOR_REV = 537.7;

    // Turret control
    public static double TURRET_KP = 1.0;
    public static double TURRET_KD = 0.02;
    public static double TURRET_MAX_POWER = 0.9;
    public static double TURRET_INPUT_DEADBAND = 0.12;
    public static double TURRET_AIM_TOLERANCE_RAD = Math.toRadians(1.4);
    public static double TURRET_MANUAL_RATE_RAD_PER_SEC = Math.toRadians(140.0);
    public static double TURRET_MIN_ANGLE_RAD = Math.toRadians(-170.0);
    public static double TURRET_MAX_ANGLE_RAD = Math.toRadians(170.0);

    // Turret sweep
    public static double TURRET_SWEEP_LEFT_RAD = Math.toRadians(-120.0);
    public static double TURRET_SWEEP_RIGHT_RAD = Math.toRadians(120.0);
    public static double TURRET_SWEEP_RATE_RAD_PER_SEC = Math.toRadians(60.0);
    public static double TURRET_SWEEP_ENDPOINT_EPS_RAD = Math.toRadians(2.0);
    public static double TURRET_SWEEP_PAUSE_SEC = 0.1;

    // Hood servo and angle calibration
    public static double HOOD_SERVO_MIN = 0.01;
    public static double HOOD_SERVO_MAX = 0.55;
    public static double HOOD_ANGLE_MIN_RAD = Math.toRadians(8.0);
    public static double HOOD_ANGLE_MAX_RAD = Math.toRadians(52.0);
    public static double HOOD_KP = 1.0;
    public static double HOOD_KD = 0.0;
    public static double HOOD_AIM_TOLERANCE_RAD = Math.toRadians(1.0);
    public static double HOOD_MANUAL_RATE_RAD_PER_SEC = Math.toRadians(40.0);

    // Flywheel velocity control
    public static double FLYWHEEL_TICKS_PER_REV = 28.0;
    public static double FLYWHEEL_KP = 80.0;
    public static double FLYWHEEL_KI = 0.0;
    public static double FLYWHEEL_KD = 0.0;
    public static double FLYWHEEL_KF_PER_RPM = 0.00542307692307692;
    public static double FLYWHEEL_MAX_RPM = 5800.0;
    public static double FLYWHEEL_MIN_RPM = -5800.0;
    public static double FLYWHEEL_RAMP_RPM_PER_SEC = 4200.0;
    public static double FLYWHEEL_AIM_TOLERANCE_RPM = 70.0;
    public static double FLYWHEEL_MANUAL_DELTA_RPM_PER_SEC = 1600.0;

    // Limelight tuning
    public static int LIMELIGHT_BLUE_PIPELINE = 0;
    public static int LIMELIGHT_RED_PIPELINE = 1;
    public static int LIMELIGHT_TURRET_PIPELINE = 1;
    public static int LIMELIGHT_POLL_RATE_HZ = 100;
    public static double LIMELIGHT_MAX_TX_ABS_DEG = 30.0;
    public static double LIMELIGHT_MAX_TY_ABS_DEG = 30.0;
    public static double LIMELIGHT_MIN_TA = 0.001;
    public static long LIMELIGHT_TARGET_TIMEOUT_MS = 350;
    public static boolean LIMELIGHT_ENABLE_FILTERING = true;
    public static double LIMELIGHT_TX_LOW_PASS_ALPHA = 0.25;
    public static double LIMELIGHT_TY_LOW_PASS_ALPHA = 0.25;
    public static double LIMELIGHT_TA_LOW_PASS_ALPHA = 0.25;

    // Odometry / velocity filtering
    public static double ODOMETRY_X_OFFSET_MM = 16.0;
    public static double ODOMETRY_Y_OFFSET_MM = 124.0;
    public static boolean ODOMETRY_ENABLE_VELOCITY_FILTERING = true;
    public static double ODOMETRY_VEL_LOW_PASS_ALPHA = 0.35;
    public static double ODOMETRY_HEADING_LOW_PASS_ALPHA = 0.25;

    // Ballistics model
    public static double BALLISTIC_GRAVITY_MPS2 = 9.81;
    public static double BALLISTIC_CAMERA_HEIGHT_M = 0.28;
    public static double BALLISTIC_TARGET_HEIGHT_M = 1.05;
    public static double BALLISTIC_CAMERA_PITCH_RAD = Math.toRadians(21.0);
    public static double BALLISTIC_CAMERA_TO_SHOOTER_FORWARD_M = 0.08;
    public static double BALLISTIC_CAMERA_TO_SHOOTER_LATERAL_M = 0.0;
    public static double BALLISTIC_CAMERA_TO_SHOOTER_VERTICAL_M = 0.04;
    public static double BALLISTIC_WHEEL_RADIUS_M = 0.048;
    public static double BALLISTIC_SLIP_FACTOR = 0.85;
    public static boolean BALLISTIC_PREFER_HIGH_ARC = false;
    public static double BALLISTIC_MIN_RANGE_M = 0.5;
    public static double BALLISTIC_MAX_RANGE_M = 6.0;
    public static boolean BALLISTIC_ENABLE_MOTION_COMP = true;
    public static double BALLISTIC_LATERAL_LEAD_COEF = 1.0;
    public static boolean BALLISTIC_ENABLE_FORWARD_PITCH_COMP = true;
    public static double BALLISTIC_FORWARD_PITCH_COEF = 0.05;
    public static double BALLISTIC_TURRET_LEAD_SIGN = -1.0;
    public static double BALLISTIC_HOOD_BLEND = 1.0;

    // Exit velocity empirical correction polynomial on range (meters): a0 + a1*d + a2*d^2
    public static double BALLISTIC_EXIT_VEL_CORR_A0 = 0.0;
    public static double BALLISTIC_EXIT_VEL_CORR_A1 = 0.0;
    public static double BALLISTIC_EXIT_VEL_CORR_A2 = 0.0;

    // Legacy mapping migration (from existing RobotHardwareConfig baseline)
    public static double SHOOTER_HOOD_DOWN_SERVO = 0.02;
    public static double LEGACY_SHOOTER_MIN_RPM = 0.0;
    public static double LEGACY_SHOOTER_MAX_RPM = 5800.0;
    public static double LEGACY_HOOD_MIN_SERVO = 0.01;
    public static double LEGACY_HOOD_MAX_SERVO = 0.55;
    public static double LEGACY_RPM_AT_1M = 2300.0;
    public static double LEGACY_RPM_AT_2M = 2650.0;
    public static double LEGACY_HOOD_SERVO_AT_1M = 0.34;
    public static double LEGACY_HOOD_SERVO_AT_2M = 0.55;
    public static double LEGACY_DISTANCE_SCALE_METERS = 1.7;
    public static double INCHES_PER_METER = 39.3701;

    // Auto/manual behavior
    public static boolean AUTO_AIM_ENABLED_DEFAULT = true;
    public static boolean MANUAL_INPUT_FORCES_MANUAL = true;
    public static double MANUAL_OVERRIDE_THRESHOLD = 0.16;
    public static double MANUAL_DEFAULT_FLYWHEEL_RPM = 0.0;
    public static double MANUAL_DEFAULT_HOOD_ANGLE_RAD = Math.toRadians(10.0);
    public static double MANUAL_DEFAULT_TURRET_ANGLE_RAD = 0.0;

    // Flywheel presets (RPM)
    public static double FLYWHEEL_PRESET_LOW_RPM = 2300.0;
    public static double FLYWHEEL_PRESET_HIGH_RPM = 2650.0;

    // Hood presets are specified as servo positions for continuity with old tuning.
    public static double HOOD_PRESET_LOW_SERVO = 0.34;
    public static double HOOD_PRESET_HIGH_SERVO = 0.55;

    // Telemetry
    public static boolean SHOOTER_VERBOSE_TELEMETRY = true;

    // Cached derived constants to avoid repeated recomputation in-loop.
    private static double cachedGearRadiusCm = Double.NaN;
    private static double cachedMotorGearRadiusCm = Double.NaN;
    private static double cachedTicksPerMotorRev = Double.NaN;
    private static double cachedTurretTicksPerRev = 0.0;
    private static double cachedTurretTicksPerRad = 0.0;

    public static void refreshDerivedConstantsIfNeeded() {
        if (cachedGearRadiusCm == TURRET_GEAR_RADIUS_CM
                && cachedMotorGearRadiusCm == TURRET_MOTOR_GEAR_RADIUS_CM
                && cachedTicksPerMotorRev == TURRET_TICKS_PER_MOTOR_REV) {
            return;
        }

        cachedGearRadiusCm = TURRET_GEAR_RADIUS_CM;
        cachedMotorGearRadiusCm = TURRET_MOTOR_GEAR_RADIUS_CM;
        cachedTicksPerMotorRev = TURRET_TICKS_PER_MOTOR_REV;

        cachedTurretTicksPerRev = TURRET_TICKS_PER_MOTOR_REV * (TURRET_GEAR_RADIUS_CM / TURRET_MOTOR_GEAR_RADIUS_CM);
        cachedTurretTicksPerRad = cachedTurretTicksPerRev / TWO_PI;
    }

    public static double turretTicksPerRev() {
        refreshDerivedConstantsIfNeeded();
        return cachedTurretTicksPerRev;
    }

    public static double turretTicksPerRad() {
        refreshDerivedConstantsIfNeeded();
        return cachedTurretTicksPerRad;
    }

    public static double turretTicksToRadians(double ticks) {
        return ticks / turretTicksPerRad();
    }

    public static double turretRadiansToTicks(double angleRad) {
        return angleRad * turretTicksPerRad();
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double wrapAngleRad(double angleRad) {
        double wrapped = (angleRad + Math.PI) % TWO_PI;
        if (wrapped < 0.0) {
            wrapped += TWO_PI;
        }
        return wrapped - Math.PI;
    }

    public static double rpmToTicksPerSecond(double rpm, double ticksPerRev) {
        return (rpm * ticksPerRev) / 60.0;
    }

    public static double flywheelRpmToTicksPerSecond(double rpm) {
        return rpmToTicksPerSecond(rpm, FLYWHEEL_TICKS_PER_REV);
    }

    public static double ticksPerSecondToFlywheelRpm(double ticksPerSecond) {
        return (ticksPerSecond * 60.0) / FLYWHEEL_TICKS_PER_REV;
    }

    public static double hoodAngleRadToServoPosition(double hoodAngleRad) {
        double normalized = (hoodAngleRad - HOOD_ANGLE_MIN_RAD) / Math.max(1e-9, (HOOD_ANGLE_MAX_RAD - HOOD_ANGLE_MIN_RAD));
        return clamp(HOOD_SERVO_MIN + normalized * (HOOD_SERVO_MAX - HOOD_SERVO_MIN), HOOD_SERVO_MIN, HOOD_SERVO_MAX);
    }

    public static double hoodServoPositionToAngleRad(double servoPosition) {
        double normalized = (servoPosition - HOOD_SERVO_MIN) / Math.max(1e-9, (HOOD_SERVO_MAX - HOOD_SERVO_MIN));
        return clamp(HOOD_ANGLE_MIN_RAD + normalized * (HOOD_ANGLE_MAX_RAD - HOOD_ANGLE_MIN_RAD), HOOD_ANGLE_MIN_RAD, HOOD_ANGLE_MAX_RAD);
    }

    public static double metersToInches(double meters) {
        return meters * INCHES_PER_METER;
    }

    public static double legacyEstimateDistanceInchesFromTagArea(double tagArea) {
        if (tagArea <= 0.0) {
            return 0.0;
        }
        double distanceMeters = LEGACY_DISTANCE_SCALE_METERS / Math.sqrt(tagArea);
        return metersToInches(distanceMeters);
    }

    public static double legacyShooterRpmSlope() {
        double oneMeterInches = INCHES_PER_METER;
        double twoMetersInches = 2.0 * INCHES_PER_METER;
        return (LEGACY_RPM_AT_2M - LEGACY_RPM_AT_1M) / (twoMetersInches - oneMeterInches);
    }

    public static double legacyShooterRpmIntercept() {
        return LEGACY_RPM_AT_1M - (legacyShooterRpmSlope() * INCHES_PER_METER);
    }

    public static double legacyHoodSlope() {
        double oneMeterInches = INCHES_PER_METER;
        double twoMetersInches = 2.0 * INCHES_PER_METER;
        return (LEGACY_HOOD_SERVO_AT_2M - LEGACY_HOOD_SERVO_AT_1M) / (twoMetersInches - oneMeterInches);
    }

    public static double legacyHoodIntercept() {
        return LEGACY_HOOD_SERVO_AT_1M - (legacyHoodSlope() * INCHES_PER_METER);
    }

    public static double estimateLegacyShooterRpm(double distanceInches) {
        return clamp(
                (legacyShooterRpmSlope() * distanceInches) + legacyShooterRpmIntercept(),
                LEGACY_SHOOTER_MIN_RPM,
                LEGACY_SHOOTER_MAX_RPM
        );
    }

    public static double estimateLegacyHoodServo(double distanceInches) {
        return clamp(
                (legacyHoodSlope() * distanceInches) + legacyHoodIntercept(),
                LEGACY_HOOD_MIN_SERVO,
                LEGACY_HOOD_MAX_SERVO
        );
    }

    public static double estimateLegacyHoodAngleRad(double distanceInches) {
        return hoodServoPositionToAngleRad(estimateLegacyHoodServo(distanceInches));
    }

    public static double evaluateExitVelocityCorrection(double rangeMeters) {
        return BALLISTIC_EXIT_VEL_CORR_A0
                + BALLISTIC_EXIT_VEL_CORR_A1 * rangeMeters
                + BALLISTIC_EXIT_VEL_CORR_A2 * rangeMeters * rangeMeters;
    }
}
