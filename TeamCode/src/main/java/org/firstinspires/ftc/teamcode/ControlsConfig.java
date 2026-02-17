package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Centralized shooter control bindings and validation.
 */
@Configurable
public final class ControlsConfig {
    private ControlsConfig() {}

    public enum ControlSource {
        GAMEPAD1,
        GAMEPAD2
    }

    public enum Axis {
        LEFT_STICK_X,
        LEFT_STICK_Y,
        RIGHT_STICK_X,
        RIGHT_STICK_Y,
        LEFT_TRIGGER,
        RIGHT_TRIGGER
    }

    public enum Button {
        A,
        B,
        X,
        Y,
        LEFT_BUMPER,
        RIGHT_BUMPER,
        DPAD_UP,
        DPAD_DOWN,
        DPAD_LEFT,
        DPAD_RIGHT,
        LEFT_STICK_BUTTON,
        RIGHT_STICK_BUTTON,
        START,
        BACK
    }

    public static ControlSource SHOOTER_CONTROL_SOURCE = ControlSource.GAMEPAD2;

    // Hard requirement: sweep toggle must be gamepad1.y.
    public static final Button SWEEP_TOGGLE_BUTTON = Button.Y;

    public static Axis MANUAL_TURRET_AXIS = Axis.RIGHT_STICK_X;
    public static Axis MANUAL_HOOD_AXIS = Axis.LEFT_STICK_Y;
    public static Axis MANUAL_FLYWHEEL_AXIS = Axis.RIGHT_TRIGGER;

    public static boolean INVERT_MANUAL_TURRET_AXIS = false;
    public static boolean INVERT_MANUAL_HOOD_AXIS = true;
    public static boolean INVERT_MANUAL_FLYWHEEL_AXIS = false;

    public static Button SET_MANUAL_MODE_BUTTON = Button.RIGHT_BUMPER;
    public static Button SET_AUTO_AIM_MODE_BUTTON = Button.LEFT_BUMPER;
    public static Button SHOOT_REQUEST_BUTTON = Button.A;

    public static Button HOOD_PRESET_LOW_BUTTON = Button.DPAD_DOWN;
    public static Button HOOD_PRESET_HIGH_BUTTON = Button.DPAD_UP;
    public static Button FLYWHEEL_PRESET_LOW_BUTTON = Button.X;
    public static Button FLYWHEEL_PRESET_HIGH_BUTTON = Button.B;

    public static boolean validateAndThrow(Telemetry telemetry) {
        StringBuilder errors = new StringBuilder();

        requireButton(errors, "SET_MANUAL_MODE_BUTTON", SET_MANUAL_MODE_BUTTON);
        requireButton(errors, "SET_AUTO_AIM_MODE_BUTTON", SET_AUTO_AIM_MODE_BUTTON);
        requireButton(errors, "SHOOT_REQUEST_BUTTON", SHOOT_REQUEST_BUTTON);
        requireButton(errors, "HOOD_PRESET_LOW_BUTTON", HOOD_PRESET_LOW_BUTTON);
        requireButton(errors, "HOOD_PRESET_HIGH_BUTTON", HOOD_PRESET_HIGH_BUTTON);
        requireButton(errors, "FLYWHEEL_PRESET_LOW_BUTTON", FLYWHEEL_PRESET_LOW_BUTTON);
        requireButton(errors, "FLYWHEEL_PRESET_HIGH_BUTTON", FLYWHEEL_PRESET_HIGH_BUTTON);

        requireDifferent(errors, "SET_MANUAL_MODE_BUTTON", SET_MANUAL_MODE_BUTTON,
                "SET_AUTO_AIM_MODE_BUTTON", SET_AUTO_AIM_MODE_BUTTON);
        requireDifferent(errors, "HOOD_PRESET_LOW_BUTTON", HOOD_PRESET_LOW_BUTTON,
                "HOOD_PRESET_HIGH_BUTTON", HOOD_PRESET_HIGH_BUTTON);
        requireDifferent(errors, "FLYWHEEL_PRESET_LOW_BUTTON", FLYWHEEL_PRESET_LOW_BUTTON,
                "FLYWHEEL_PRESET_HIGH_BUTTON", FLYWHEEL_PRESET_HIGH_BUTTON);

        // Shooter actions share the same source, so enforce uniqueness across mode and shoot requests.
        requireDifferent(errors, "SET_MANUAL_MODE_BUTTON", SET_MANUAL_MODE_BUTTON,
                "SHOOT_REQUEST_BUTTON", SHOOT_REQUEST_BUTTON);
        requireDifferent(errors, "SET_AUTO_AIM_MODE_BUTTON", SET_AUTO_AIM_MODE_BUTTON,
                "SHOOT_REQUEST_BUTTON", SHOOT_REQUEST_BUTTON);

        // If shooter controls are on gamepad1, prevent conflicts with sweep toggle on gamepad1.y.
        if (SHOOTER_CONTROL_SOURCE == ControlSource.GAMEPAD1) {
            requireDifferent(errors, "SWEEP_TOGGLE_BUTTON(gamepad1)", SWEEP_TOGGLE_BUTTON,
                    "SET_MANUAL_MODE_BUTTON(gamepad1)", SET_MANUAL_MODE_BUTTON);
            requireDifferent(errors, "SWEEP_TOGGLE_BUTTON(gamepad1)", SWEEP_TOGGLE_BUTTON,
                    "SET_AUTO_AIM_MODE_BUTTON(gamepad1)", SET_AUTO_AIM_MODE_BUTTON);
            requireDifferent(errors, "SWEEP_TOGGLE_BUTTON(gamepad1)", SWEEP_TOGGLE_BUTTON,
                    "SHOOT_REQUEST_BUTTON(gamepad1)", SHOOT_REQUEST_BUTTON);
        }

        if (errors.length() > 0) {
            String message = "ControlsConfig validation failed: " + errors;
            if (telemetry != null) {
                telemetry.addData("ControlsConfig", message);
                telemetry.update();
            }
            throw new IllegalStateException(message);
        }

        return true;
    }

    private static void requireButton(StringBuilder errors, String label, Button button) {
        if (button == null) {
            appendError(errors, label + " is not assigned");
        }
    }

    private static void requireDifferent(StringBuilder errors,
                                         String leftLabel,
                                         Button left,
                                         String rightLabel,
                                         Button right) {
        if (left != null && right != null && left == right) {
            appendError(errors, leftLabel + " conflicts with " + rightLabel + " on button " + left);
        }
    }

    private static void appendError(StringBuilder errors, String message) {
        if (errors.length() > 0) {
            errors.append(" | ");
        }
        errors.append(message);
    }

    public static final class ShooterInput {
        public double manualTurret;
        public double manualHood;
        public double manualFlywheel;

        public boolean setManualModePressed;
        public boolean setAutoAimModePressed;
        public boolean sweepTogglePressed;
        public boolean shootRequested;

        public boolean hoodPresetLowPressed;
        public boolean hoodPresetHighPressed;
        public boolean flywheelPresetLowPressed;
        public boolean flywheelPresetHighPressed;

        public void clear() {
            manualTurret = 0.0;
            manualHood = 0.0;
            manualFlywheel = 0.0;
            setManualModePressed = false;
            setAutoAimModePressed = false;
            sweepTogglePressed = false;
            shootRequested = false;
            hoodPresetLowPressed = false;
            hoodPresetHighPressed = false;
            flywheelPresetLowPressed = false;
            flywheelPresetHighPressed = false;
        }
    }

    /**
     * Stateful input reader to provide debounced/toggle semantics.
     */
    public static final class ShooterInputReader {
        private boolean lastSweepToggle;
        private boolean lastSetManual;
        private boolean lastSetAuto;
        private boolean lastShoot;
        private boolean lastHoodLow;
        private boolean lastHoodHigh;
        private boolean lastFlywheelLow;
        private boolean lastFlywheelHigh;

        public void read(Gamepad gamepad1, Gamepad gamepad2, ShooterInput out) {
            out.clear();

            Gamepad source = SHOOTER_CONTROL_SOURCE == ControlSource.GAMEPAD1 ? gamepad1 : gamepad2;

            out.manualTurret = axisValue(source, MANUAL_TURRET_AXIS);
            out.manualHood = axisValue(source, MANUAL_HOOD_AXIS);
            out.manualFlywheel = axisValue(source, MANUAL_FLYWHEEL_AXIS);

            if (INVERT_MANUAL_TURRET_AXIS) {
                out.manualTurret = -out.manualTurret;
            }
            if (INVERT_MANUAL_HOOD_AXIS) {
                out.manualHood = -out.manualHood;
            }
            if (INVERT_MANUAL_FLYWHEEL_AXIS) {
                out.manualFlywheel = -out.manualFlywheel;
            }

            boolean sweepRaw = gamepad1.y;
            out.sweepTogglePressed = sweepRaw && !lastSweepToggle;
            lastSweepToggle = sweepRaw;

            boolean setManualRaw = buttonValue(source, SET_MANUAL_MODE_BUTTON);
            out.setManualModePressed = setManualRaw && !lastSetManual;
            lastSetManual = setManualRaw;

            boolean setAutoRaw = buttonValue(source, SET_AUTO_AIM_MODE_BUTTON);
            out.setAutoAimModePressed = setAutoRaw && !lastSetAuto;
            lastSetAuto = setAutoRaw;

            boolean shootRaw = buttonValue(source, SHOOT_REQUEST_BUTTON);
            out.shootRequested = shootRaw;
            lastShoot = shootRaw;

            boolean hoodLowRaw = buttonValue(source, HOOD_PRESET_LOW_BUTTON);
            out.hoodPresetLowPressed = hoodLowRaw && !lastHoodLow;
            lastHoodLow = hoodLowRaw;

            boolean hoodHighRaw = buttonValue(source, HOOD_PRESET_HIGH_BUTTON);
            out.hoodPresetHighPressed = hoodHighRaw && !lastHoodHigh;
            lastHoodHigh = hoodHighRaw;

            boolean flywheelLowRaw = buttonValue(source, FLYWHEEL_PRESET_LOW_BUTTON);
            out.flywheelPresetLowPressed = flywheelLowRaw && !lastFlywheelLow;
            lastFlywheelLow = flywheelLowRaw;

            boolean flywheelHighRaw = buttonValue(source, FLYWHEEL_PRESET_HIGH_BUTTON);
            out.flywheelPresetHighPressed = flywheelHighRaw && !lastFlywheelHigh;
            lastFlywheelHigh = flywheelHighRaw;
        }

        private static double axisValue(Gamepad gamepad, Axis axis) {
            switch (axis) {
                case LEFT_STICK_X:
                    return gamepad.left_stick_x;
                case LEFT_STICK_Y:
                    return gamepad.left_stick_y;
                case RIGHT_STICK_X:
                    return gamepad.right_stick_x;
                case RIGHT_STICK_Y:
                    return gamepad.right_stick_y;
                case LEFT_TRIGGER:
                    return gamepad.left_trigger;
                case RIGHT_TRIGGER:
                    return gamepad.right_trigger;
                default:
                    return 0.0;
            }
        }

        private static boolean buttonValue(Gamepad gamepad, Button button) {
            switch (button) {
                case A:
                    return gamepad.a;
                case B:
                    return gamepad.b;
                case X:
                    return gamepad.x;
                case Y:
                    return gamepad.y;
                case LEFT_BUMPER:
                    return gamepad.left_bumper;
                case RIGHT_BUMPER:
                    return gamepad.right_bumper;
                case DPAD_UP:
                    return gamepad.dpad_up;
                case DPAD_DOWN:
                    return gamepad.dpad_down;
                case DPAD_LEFT:
                    return gamepad.dpad_left;
                case DPAD_RIGHT:
                    return gamepad.dpad_right;
                case LEFT_STICK_BUTTON:
                    return gamepad.left_stick_button;
                case RIGHT_STICK_BUTTON:
                    return gamepad.right_stick_button;
                case START:
                    return gamepad.start;
                case BACK:
                    return gamepad.back;
                default:
                    return false;
            }
        }
    }
}
