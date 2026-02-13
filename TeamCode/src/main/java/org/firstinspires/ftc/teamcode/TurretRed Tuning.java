package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * A modular and configurable op mode for controlling a turret that can either
 * automatically align to an AprilTag using a Limelight camera or be steered
 * manually with a joystick.  All math is performed in radians and gear
 * dimensions are exposed as configurable parameters.  The turret is limited
 * to a configurable range of motion to prevent wire entanglement.  When a
 * target is present, automatic aiming always takes priority over manual input.
 */
@Configurable
@TeleOp(name = "Turret Control", group = "Competition")
public class TurretControl extends OpMode {
    // Hardware
    private DcMotorEx turretMotor;
    private Limelight3A limelight;

    /**
     * The radius of the large gear that the turret sits on, in centimeters.
     * This is used to derive the overall gear ratio.
     */
    @Configurable
    public static double gearRadiusTurretCm = 8.5;

    /**
     * The radius of the motor's pinion gear, in centimeters.  Changing this
     * affects how many motor encoder ticks correspond to one turret revolution.
     */
    @Configurable
    public static double gearRadiusMotorCm = 3.5;

    /**
     * Encoder counts per revolution of the turret motor.  For a Gobilda 5202
     * motor this is typically 537.7 ticks per revolution.  If you change
     * motors, update this accordingly.
     */
    @Configurable
    public static double ticksPerMotorRev = 537.7;

    /**
     * Proportional gain used to drive the turret toward the target angle.  The
     * output of this controller directly sets motor power.  Tune this so the
     * turret is responsive but doesn’t oscillate.
     */
    @Configurable
    public static double kP = 1.0;

    /**
     * Joystick deadband.  If the magnitude of the joystick vector is below
     * this value, manual inputs are ignored and the previous target is held.
     */
    @Configurable
    public static double deadband = 0.2;

    /**
     * Upper soft limit for the turret in degrees.  Converted to radians at
     * runtime.  The turret will not command angles beyond this limit.  Zero
     * degrees corresponds to facing directly forward, positive angles point
     * toward the robot’s left side (counterclockwise), and negative angles
     * point right (clockwise).
     */
    @Configurable
    public static double maxAngleDeg = 170.0;

    /**
     * Lower soft limit for the turret in degrees.  This should be negative
     * (e.g. -170).  See {@link #maxAngleDeg} for the coordinate system.
     */
    @Configurable
    public static double minAngleDeg = -170.0;

    // Derived constants (updated in init)
    private double ticksPerTurretRev;
    private double ticksPerRad;
    private double minAngleRad;
    private double maxAngleRad;

    // Target angle the turret should move to (in radians)
    private double targetAngleRad = 0.0;

    @Override
    public void init() {
        // Initialize hardware
        turretMotor = hardwareMap.get(DcMotorEx.class, "turret");
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // Reverse direction so positive power rotates the turret counterclockwise
        turretMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        // Poll Limelight frequently for the latest results
        limelight.setPollRateHz(100);
        // Use a pipeline configured for AprilTag detection (ensure this exists on the camera)
        limelight.pipelineSwitch(1);

        // Precompute derived constants.  These are based on the current gear radii and
        // motor encoder resolution.  See FTC Limelight programming guide for usage of
        // result.getTx() and result.isValid()【713889773590808†L123-L132】.
        ticksPerTurretRev = ticksPerMotorRev * (gearRadiusTurretCm / gearRadiusMotorCm);
        ticksPerRad = ticksPerTurretRev / (2.0 * Math.PI);
        minAngleRad = Math.toRadians(minAngleDeg);
        maxAngleRad = Math.toRadians(maxAngleDeg);
    }

    @Override
    public void start() {
        // Start streaming from the Limelight
        limelight.start();
    }

    @Override
    public void loop() {
        // Read joystick input.  The joystick axes are mapped such that pushing
        // forward on the stick yields a negative y value.  We flip y so that
        // forward corresponds to a positive value.
        double x = gamepad2.right_stick_x;
        double y = -gamepad2.right_stick_y;
        double stickMagnitude = Math.hypot(x, y);

        // Update current turret angle from the encoder, converting ticks to radians
        double currentTicks = turretMotor.getCurrentPosition();
        double turretAngleRad = currentTicks / ticksPerRad;

        // Attempt to acquire a Limelight result.  If a tag is detected, use its
        // horizontal offset (tx) to drive the turret toward the tag.  According
        // to the FTC Limelight API, {@link LLResult#getTx()} returns how far
        // left or right the target is from the crosshair in degrees【713889773590808†L123-L132】.
        LLResult result = limelight.getLatestResult();
        boolean targetSeen = result != null && result.isValid();
        if (targetSeen) {
            // Positive tx means the tag is to the right of the crosshair.  Our
            // coordinate system defines positive angles as counterclockwise (left),
            // so subtracting yaw brings the turret toward the tag.
            double yawDeg = result.getTx();
            double yawRad = Math.toRadians(yawDeg);
            // Update the target angle relative to the current angle
            targetAngleRad = turretAngleRad - yawRad;
        } else if (stickMagnitude > deadband) {
            // No tag detected, so manual control.  Compute the desired angle from
            // the joystick vector.  Math.atan2(x, y) returns an angle where zero
            // corresponds to straight ahead (y positive) and positive angles
            // correspond to a left turn (counterclockwise).
            targetAngleRad = Math.atan2(x, y);
        }

        // Clamp the target angle within the safe limits to protect the wire loom
        if (targetAngleRad > maxAngleRad) {
            targetAngleRad = maxAngleRad;
        } else if (targetAngleRad < minAngleRad) {
            targetAngleRad = minAngleRad;
        }

        // Compute the shortest permissible path to the target.  Normalize the
        // difference into the range [-pi, pi], but if the normalized target
        // would cross the soft limits, fall back to the direct difference.
        double diff = targetAngleRad - turretAngleRad;
        double diffWrapped = wrapAngle(diff);
        double predictedAngle = turretAngleRad + diffWrapped;
        if (predictedAngle < minAngleRad || predictedAngle > maxAngleRad) {
            // Taking the wrapped path would exceed our limits, so use the raw difference
            diffWrapped = diff;
        }

        // Soft stop protection: if the turret is already at a limit and the
        // command would drive it further into the limit, cancel the movement.
        if (turretAngleRad >= maxAngleRad && diffWrapped > 0.0) {
            diffWrapped = 0.0;
        }
        if (turretAngleRad <= minAngleRad && diffWrapped < 0.0) {
            diffWrapped = 0.0;
        }

        // Apply proportional control.  The output is clipped to the motor’s
        // allowable power range [-1, 1].  Because diff is in radians, adjust kP
        // accordingly when tuning.
        double output = kP * diffWrapped;
        output = Math.max(-1.0, Math.min(1.0, output));
        turretMotor.setPower(output);

        // Telemetry for debugging and tuning
        telemetry.addData("TargetSeen", targetSeen);
        telemetry.addData("JoystickMag", stickMagnitude);
        telemetry.addData("TurretAngleRad", turretAngleRad);
        telemetry.addData("TargetAngleRad", targetAngleRad);
        telemetry.addData("ErrorRad", diffWrapped);
        telemetry.addData("MotorOutput", output);
    }

    /**
     * Wrap an angle difference into the range [-π, π).  This helps choose the
     * shortest rotational distance between two angles.  The result retains
     * the sign of the input, so wrapAngle(3π/2) returns -π/2.
     *
     * @param angle a raw angle difference in radians
     * @return the equivalent angle in [-π, π)
     */
    private static double wrapAngle(double angle) {
        double twoPi = 2.0 * Math.PI;
        angle = ((angle + Math.PI) % twoPi + twoPi) % twoPi; // normalize to [0, 2π)
        return angle - Math.PI;
    }
}