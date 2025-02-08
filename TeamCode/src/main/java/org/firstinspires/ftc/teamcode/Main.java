package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.util.Objects;

/**
 * Complex Arm Control OpMode with Improved Driver-Friendly Controls
 *
 * MAIN CONTROLS (gamepad1):
 *   Claw (main) functions on ABX:
 *     A: Toggle Claw Orientation (Down for pickup, Up for handoff)
 *     B: Toggle Claw Grab (Closed/Open)
 *     X: Toggle Claw Roll (Pickup angle/Neutral)
 *     Y (hold): Reset all claw settings to neutral (requires hold for 0.5 sec)
 *
 *   Deposit functions on the DPad:
 *     DPad Up: Toggle deposit claw orientation (aligned/not)
 *     DPad Down: Toggle deposit claw grab (closed/open)
 *     DPad Left/Right: Optional fine adjustments
 *
 *   Intake Extender (scissor mechanism) on Right Trigger:
 *     • rt < 10% → extender = 10%
 *     • 10% ≤ rt < 50% → smoothly scales to 50%
 *     • rt ≥ 50% → smoothly scales to 100%
 *
 *   Lift Control:
 *     Left Trigger controls lift-up (proportional).
 *     Left Bumper (with Left Trigger) reverses lift (moves down).
 *
 * DRIVE (Gamepad1):
 *   Tank drive with left joystick (left motors) and right joystick (right motors),
 *   using cubic scaling for smoother low-speed operation.
 */
@TeleOp(name = "Complex Arm Control", group = "Competition")
public class Main extends OpMode {

    // ------------------------------
    // Hardware Declarations (Drive, Lift & Mechanisms)
    // ------------------------------
    DcMotor frontLeft, frontRight, backLeft, backRight;
    DcMotor liftMotor;
    
    // Mechanism Servos (gamepad1)
    Servo clawRotate;      // Claw orientation servo (rotate to face ground/up)
    Servo clawGrab;        // Claw grabber servo (open/close)
    Servo clawRoll;        // Claw roll servo (adjust pickup angle)
    Servo depositRotate;   // Depositor claw orientation servo
    Servo depositGrab;     // Depositor claw grabber servo
    Servo intakeExtend1;   // Intake extender servo 1 (scissor mechanism)
    Servo intakeExtend2;   // Intake extender servo 2 (mirrors servo 1)
    
    // ------------------------------
    // Toggle Declarations for gamepad1 Buttons (ABX & D-Pad)
    // ------------------------------
    Toggle mechA = new Toggle();         // Claw Orientation toggle (A)
    Toggle mechB = new Toggle();         // Claw Grab toggle (B)
    Toggle mechX = new Toggle();         // Claw Roll toggle (X)
    // (Y is now processed with a hold timer for reset.)
    
    Toggle dpadUpToggle    = new Toggle(); // Depositor Claw Orientation (DPad Up)
    Toggle dpadDownToggle  = new Toggle(); // Depositor Claw Grab (DPad Down)
    Toggle dpadLeftToggle  = new Toggle(); // Optional fine adjustment (DPad Left)
    Toggle dpadRightToggle = new Toggle(); // Optional fine adjustment (DPad Right)
        
    // ------------------------------
    // Preset Positions for Servos
    // ------------------------------
    // Main Claw presets:
    final double CLAW_ORIENTATION_DOWN = 0.0;   // Claw faces down for pickup.
    final double CLAW_ORIENTATION_UP   = 0.90;     // Claw faces up for handoff.
    final double CLAW_GRAB_CLOSED      = 0.0;     // Claw grabber closed.
    final double CLAW_GRAB_OPEN        = 0.3;     // Claw grabber open.
    final double CLAW_ROLL_ANGLE       = 0;    // Claw roll for pickup angle.
    final double CLAW_ROLL_NEUTRAL     = 0.35;     // Neutral claw roll.
    
    // Intake Extender presets (scissor mechanism):
    final double INTAKE_EXTENDER_HALF = 0.25;   // 50% extension.
    final double INTAKE_EXTENDER_RANGE = 0.36;
    
    // Depositor presets:
    final double DEPOSIT_ORIENTATION_ALIGNED = 1.0;
    final double DEPOSIT_ORIENTATION_WALL = 0.1;
    final double DEPOSIT_ORIENTATION_RESET   = 0.4;
    final double DEPOSIT_GRAB_CLOSED         = 0.0;
    final double DEPOSIT_GRAB_OPEN           = 0.2;
    
    // ------------------------------
    // Y Button Reset Hold Timer
    // ------------------------------
    private double yHoldStartTime = 0;
    private final double Y_RESET_HOLD_THRESHOLD = 0.5; // seconds
    
    // Example voltage threshold (adjust as needed)
    final double SAFE_VOLTAGE_THRESHOLD = 11.0;
    
    // Add these as class member variables near the beginning of your class:
    private double lastLeftPower = 0.0;
    private double lastRightPower = 0.0;
    private double lastLoopTime = 0.0;

    private double lastIntakeCommandedPosition = INTAKE_EXTENDER_HALF;  // starting from half-extended
    private double intakeExtenderPosition = INTAKE_EXTENDER_RANGE;
    private double intakeWatchdogStartTime = 0.0;
    private final double INTAKE_MOVEMENT_TIMEOUT = 2.0; // seconds allowed for movement

    // Ramping constants (change according to your testing)
    private final double RAMP_RATE = 1.0;        // maximum motor power change per second
    private final double SERVO_RAMP_RATE = 0.5;    // maximum servo position change per second
    private final double SERVO_TOLERANCE = 0.01;   // tolerance for servo target accuracy

    // Lift Motor Encoder Hold Constants
    private final double HOLD_POWER = 0.5;        // Maximum power for movement
    private final double HOLD_DEADZONE = 0.05;    // Deadzone for lift hold
    private final double HOLD_RAMP_RATE = 0.2;    // Ramp rate for lift hold
    private final double HOLD_HOLD_POWER = 0.15;  // Reduced from 0.2 to 0.15 for gentler holding
    
    // Claw Roll Logic
    public boolean claw_rolling = false;
    public double claw_roll_degrees = 0;

    public String deposit_position = "reset";

    public static class Toggle {
        public boolean toggled = false;
        public boolean previous = false;
        
        /**
         * Update the toggle state; on a rising edge, flip the state.
         */
        public void update(boolean current) {
            if (current && !previous) {
                toggled = !toggled;
            }
            previous = current;
        }
        
        @NonNull
        @Override
        public String toString() {
            return String.valueOf(toggled);
        }
    }

    // Odometry Computer: GoBilda Pinpoint device configured in the robot configuration as "odo"
    GoBildaPinpointDriver odo;
    
    @Override
    public void init() {
        // ------------------------------
        // Initialize Drive and Lift Motors (Gamepad1 & gamepad1)
        // ------------------------------
        frontLeft  = hardwareMap.get(DcMotor.class, "front_left");
        frontRight = hardwareMap.get(DcMotor.class, "front_right");
        backLeft   = hardwareMap.get(DcMotor.class, "back_left");
        backRight  = hardwareMap.get(DcMotor.class, "back_right");
        liftMotor  = hardwareMap.get(DcMotor.class, "lift_motor");
        
        // Set motor directions (adjust for your robot's configuration)
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        liftMotor.setDirection(DcMotor.Direction.REVERSE);

        // ------------------------------
        // Initialize Mechanism Servos (gamepad1)
        // ------------------------------
        clawRotate    = hardwareMap.get(Servo.class, "claw_rotate");
        clawGrab      = hardwareMap.get(Servo.class, "claw_grab");
        clawRoll      = hardwareMap.get(Servo.class, "claw_roll");
        depositRotate = hardwareMap.get(Servo.class, "deposit_rotate");
        depositGrab   = hardwareMap.get(Servo.class, "deposit_grab");
        intakeExtend1 = hardwareMap.get(Servo.class, "intake_extend1");
        intakeExtend2 = hardwareMap.get(Servo.class, "intake_extend2");
        
        // Ensure the two intake extenders track together.
        // The second servo is mounted reversed, so its direction is reversed.
        intakeExtend1.setDirection(Servo.Direction.REVERSE);
        intakeExtend2.setDirection(Servo.Direction.FORWARD);

        clawRoll.setDirection(Servo.Direction.FORWARD);

        // ------------------------------
        // Initialize Lift Motor for Encoder-Based Hold
        // ------------------------------
        // Set the lift motor to brake when no power is applied,
        // so that gravity is countered when holding.
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        // Enable encoder tracking.
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // ------------------------------
        // Initialize the GoBilda® Pinpoint Odometry Computer
        // ------------------------------
        // Configure the device from the hardware map; device name should be "odo"
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
        // Set pod offsets if needed (example values; adjust for your robot)
        odo.setOffsets(-84.0, -168.0);
        // Reset the odometry positions and recalibrate the IMU.
        odo.resetPosAndIMU();
        
        // DO NOT set initial servo positions; allow the physical starting positions to persist.
        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }
    
    @Override
    public void loop() {
        double currentTime = getRuntime();
        double deltaTime = currentTime - lastLoopTime;
        lastLoopTime = currentTime;

        // ------------------------------
        // Update Odometry
        // ------------------------------
        odo.update();
        Pose2D pose = odo.getPosition();
        telemetry.addData("Pose", "{X: %.2f mm, Y: %.2f mm, H: %.2f°}",
                pose.getX(DistanceUnit.MM), pose.getY(DistanceUnit.MM), pose.getHeading(AngleUnit.DEGREES));
        
        // ------------------------------
        // Safety Mode (monitor battery)
        // ------------------------------
        VoltageSensor batterySensor = hardwareMap.voltageSensor.iterator().next();
        double batteryVoltage = batterySensor.getVoltage();
        boolean safetyMode = batteryVoltage < SAFE_VOLTAGE_THRESHOLD;
        double safetyScaleFactor = safetyMode ? 0.6 : 1.0;
        
        // ------------------------------
        // Drive Controls with Tank Drive and Strafing
        // ------------------------------
        double leftPower = gamepad1.left_stick_y;
        double rightPower = gamepad1.right_stick_y;
        double strafePower = 0;

        // Add strafing with bumpers
        if (gamepad1.left_bumper) {
            // Strafe left
            strafePower = 0.8;  // Adjust power as needed
        } else if (gamepad1.right_bumper) {
            // Strafe right
            strafePower = -0.8;   // Adjust power as needed
        }

        // Apply safety scaling and set drive motor powers:
        // For strafing: left and right sides move in opposite directions
        frontLeft.setPower((leftPower + strafePower) * safetyScaleFactor);
        backLeft.setPower((leftPower - strafePower) * safetyScaleFactor);
        frontRight.setPower((rightPower - strafePower) * safetyScaleFactor);
        backRight.setPower((rightPower + strafePower) * safetyScaleFactor);
        
        // ------------------------------
        // Update Toggle States for Mechanisms (gamepad1)
        // ------------------------------
        mechA.update(gamepad1.a);            // Claw Orientation Toggle (A)
        mechB.update(gamepad1.b);            // Claw Grab Toggle (B)
        mechX.update(gamepad1.x);            // Claw Roll Toggle (X)
        // Note: The Y button is processed separately with a hold timer.
        
        dpadUpToggle.update(gamepad1.dpad_up);       // Deposit Claw Orientation (DPad Up)
        dpadDownToggle.update(gamepad1.dpad_down);     // Deposit Claw Grab (DPad Down)
        dpadLeftToggle.update(gamepad1.dpad_left);     // Optional Fine Adjustment (DPad Left)
        dpadRightToggle.update(gamepad1.dpad_right);   // Optional Fine Adjustment (DPad Right)

        // ------------------------------
        // Process Y Button Reset (Requires a Hold)
        // ------------------------------
        boolean yReset = false;
        if (gamepad1.y) {
            if (yHoldStartTime == 0) {
                yHoldStartTime = getRuntime();
            } else if (getRuntime() - yHoldStartTime >= Y_RESET_HOLD_THRESHOLD) {
                yReset = true;
            }
        } else {
            yHoldStartTime = 0;
        }

        // ------------------------------
        // Main Claw Mechanism Actions
        // ------------------------------
        if (yReset) {
            // Y held for long enough: Reset all claw settings to neutral.
            clawRotate.setPosition(CLAW_ORIENTATION_UP);
            clawGrab.setPosition(CLAW_GRAB_OPEN);
            clawRoll.setPosition(CLAW_ROLL_NEUTRAL);
            // Also reset toggle states.
            mechA.toggled = false;
            mechB.toggled = false;
            mechX.toggled = false;
        } else {
            // Process ABX toggles normally when Y is not held.
            if (mechA.toggled) {
                clawRotate.setPosition(CLAW_ORIENTATION_DOWN);
            } else {
                clawRotate.setPosition(CLAW_ORIENTATION_UP);
            }

            if (mechX.toggled) {
                clawGrab.setPosition(CLAW_GRAB_CLOSED);
            } else {
                clawGrab.setPosition(CLAW_GRAB_OPEN);
            }

            // Allow the driver to manually roll the claw using the right joystick x axis and then snap back to a position when b is pressed
            // Apply deadzone to prevent jittering
            double joystickX = gamepad2.right_stick_x;
            final double DEADZONE = 0.05;
            
            if (Math.abs(joystickX) > DEADZONE) {
                claw_rolling = true;
            } else if (gamepad1.b) {
                claw_rolling = false;
            }

            if (!claw_rolling) {
                // When not manually rolling, use the toggle positions
                clawRoll.setDirection(Servo.Direction.FORWARD);
                if (mechB.toggled) {
                    clawRoll.setPosition(CLAW_ROLL_ANGLE);
                } else {
                    clawRoll.setPosition(CLAW_ROLL_NEUTRAL);
                }
            } else {
                // Manual rolling mode
                // Get current position and add joystick input
                double currentPosition = clawRoll.getPosition();
                // Scale the joystick input to make the movement more controllable
                double scaledInput = joystickX * 0.05; // Adjust this multiplier to control sensitivity
                double newPosition = currentPosition + scaledInput;
                
                // Clamp the position between 0 and 1 (servo valid range)
                newPosition = Math.min(Math.max(newPosition, 0), 1);
                
                // Set servo direction based on joystick direction
                if (joystickX > 0) {
                    clawRoll.setDirection(Servo.Direction.FORWARD);
                } else {
                    clawRoll.setDirection(Servo.Direction.REVERSE);
                }
                
                clawRoll.setPosition(newPosition);
            }

        }
        // ------------------------------
        // Intake Extender Control (Right Trigger on Gamepad1)
        // ------------------------------
        double rawRt = gamepad1.right_trigger;
        double triggerDeadzone = 0.05;
        double filteredRT = Math.abs(rawRt) < triggerDeadzone ? 0 : rawRt;
        
        // Map the trigger input to a target extender position within the allowed range [0.1, INTAKE_EXTENDER_RANGE]:
        double targetExtenderPosition = scaleExtenderInput(filteredRT);
        // Gradually ramp the actual commanded position:
        intakeExtenderPosition = rampServoValue(targetExtenderPosition, lastIntakeCommandedPosition, deltaTime);
        lastIntakeCommandedPosition = intakeExtenderPosition;
        
        intakeExtend1.setPosition(intakeExtenderPosition);
        intakeExtend2.setPosition(intakeExtenderPosition);
        
        // ------------------------------
        // Depositor Mechanism Controls (DPad on gamepad1)
        // ------------------------------
        // Update toggle states
        dpadUpToggle.update(gamepad1.dpad_up);
        dpadDownToggle.update(gamepad1.dpad_down); 
        dpadLeftToggle.update(gamepad1.dpad_left);

        // Track the current and previous positions of the deposit rotate servo
        double currentRotatePosition = depositRotate.getPosition();
        boolean isRotating = false;

        // ------------------------------
        // Deposit Position Control, use DPPad to select target position, then dpad down to open or close grabber on toggle
        if (gamepad1.dpad_up) {
            deposit_position = "reset";
        } else if (gamepad1.dpad_left) {
            deposit_position = "wall";
        } else if (gamepad1.dpad_right) {
            deposit_position = "aligned";
        }

        if (Objects.equals(deposit_position, "aligned")) {
            depositRotate.setPosition(DEPOSIT_ORIENTATION_ALIGNED);
        } else if (Objects.equals(deposit_position, "wall")) {
            depositRotate.setPosition(DEPOSIT_ORIENTATION_WALL);
        } else if (Objects.equals(deposit_position, "reset")) {
            depositRotate.setPosition(DEPOSIT_ORIENTATION_RESET);
        }

        if (dpadDownToggle.toggled) {
            depositGrab.setPosition(DEPOSIT_GRAB_CLOSED);
        } else {
            depositGrab.setPosition(DEPOSIT_GRAB_OPEN);
        }


        // ------------------------------


        // ------------------------------
        // Lift Control (Left Trigger for up, Y for down)
        // ------------------------------
        double rawLiftSpeed = 0;

        if (gamepad1.left_trigger > 0) {
            // Scale the trigger input for upward movement (0 to 1.0)
            rawLiftSpeed = gamepad1.left_trigger;
        } else if (gamepad1.y) {
            // Down movement at fixed speed when Y is pressed
            rawLiftSpeed = -0.5;  // Adjust this negative value if needed
        }

        // Define a small deadzone to decide when there's "no input"
        if (Math.abs(rawLiftSpeed) < HOLD_DEADZONE) {
            // No input -> Hold the current position
            if (liftMotor.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
                // Set the target position to the current position
                liftMotor.setTargetPosition(liftMotor.getCurrentPosition());
                liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                
                // Apply initial holding power
                liftMotor.setPower(HOLD_HOLD_POWER);
            }
            
            // Gradually increase holding power if position error exists
            int positionError = Math.abs(liftMotor.getTargetPosition() - liftMotor.getCurrentPosition());
            if (positionError > 10) {  // Increased tolerance from 5 to 10
                // More gradual power increase with position error
                double errorRatio = Math.min(positionError / 200.0, 1.0);  // Reduced from 100.0 to 200.0
                double adjustedPower = HOLD_HOLD_POWER + (errorRatio * 0.15);  // Max additional power of 0.15
                liftMotor.setPower(adjustedPower);
            } else {
                // If we're within tolerance, use the base holding power
                liftMotor.setPower(HOLD_HOLD_POWER);
            }
        } else {
            // Active operator input -> Switch to manual control
            if (liftMotor.getMode() != DcMotor.RunMode.RUN_USING_ENCODER) {
                liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            // Apply the commanded power directly
            liftMotor.setPower(rawLiftSpeed);
        }
        
        // ------------------------------
        // Telemetry for Driver Feedback
        // ------------------------------
        telemetry.addData("Battery Voltage", "%.2f V", batteryVoltage);
        telemetry.addData("Safety Mode", safetyMode);
        telemetry.addData("Left Power", "%.2f", leftPower);
        telemetry.addData("Right Power", "%.2f", rightPower);
        telemetry.addData("Raw RT", "%.2f", rawRt);
        telemetry.addData("Target Extender Pos", "%.2f", targetExtenderPosition);
        telemetry.addData("Commanded Extender Pos", "%.2f", intakeExtenderPosition);
        telemetry.addData("Delta Time", "%.2f", deltaTime);
        telemetry.addData("Claw Orientation (A)", mechA.toggled);
        telemetry.addData("Claw Grab (B)", mechB.toggled);
        telemetry.addData("Claw Roll (X)", mechX.toggled);
        telemetry.addData("Y Reset Held", yReset);
        telemetry.addData("Lift Power", rawLiftSpeed);
        telemetry.addData("Deposit Orientation (DPad Up)", dpadUpToggle.toggled);
        telemetry.addData("Deposit Grab (DPad Down)", dpadDownToggle.toggled);
        telemetry.update();
    }
    
    /**
     * Cubic scaling for drive input for refined control at low speeds.
     */
    private double scaleDriveInput(double input) {
        return input * input * input;
    }
    
    /**
     * Scale the right trigger input into the allowed intake extender range.
     * Allowed range: from 0.1 (10%) to INTAKE_EXTENDER_RANGE (INTAKE_EXTENDER_RANGE%).
     * For trigger values:
     *   - rt < 0.1: returns 0.1.
     *   - For 0.1 ≤ rt < 0.5: scales quadratically to the midpoint (0.11).
     *   - For rt ≥ 0.5: scales quadratically to INTAKE_EXTENDER_RANGE.
     */
    private double scaleExtenderInput(double rt) {
        // Define the minimum allowed value
        double min = 0.1;
        // Define the maximum allowed value via the configured variable
        double max = INTAKE_EXTENDER_RANGE;
        // Compute the midpoint between min and max
        double mid = (min + max) / 2.0;
        
        if (rt < 0.1) {
            return min;
        } else if (rt < 0.5) {
            double normalized = (rt - 0.1) / 0.4;  // normalized value in [0,1]
            double scaled = normalized * normalized; // quadratic curve
            return min + scaled * (mid - min);       // scales from min to mid
        } else {
            double normalized = (rt - 0.5) / 0.5;    // normalized value in [0,1]
            double scaled = normalized * normalized; // quadratic curve
            return mid + scaled * (max - mid);         // scales from mid to max
        }
    }

    // Helper function to apply a deadzone to an input (such as joystick or trigger)
    private double applyDeadzone(double input, double deadzone) {
        return Math.abs(input) < deadzone ? 0 : input;
    }

    // Gradual ramping (slew rate limiter) for motor power
    private double rampMotorPower(double target, double current, double rampRate, double deltaTime) {
        double maxDelta = rampRate * deltaTime;
        double delta = target - current;
        if (Math.abs(delta) > maxDelta) {
            delta = Math.signum(delta) * maxDelta;
        }
        return current + delta;
    }

    // Gradual ramping (slew rate limiter) for servo values
    private double rampServoValue(double target, double current, double deltaTime) {
        double maxDelta = 0.5 * deltaTime;
        double delta = target - current;
        if (Math.abs(delta) > maxDelta) {
            delta = Math.signum(delta) * maxDelta;
        }
        return current + delta;
    }
}
