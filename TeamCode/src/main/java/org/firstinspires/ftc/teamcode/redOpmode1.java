package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver.LayerHeight;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

@Configurable
@TeleOp(name = "Red Alliance Teleop", group = "Competition")
public class redOpmode1 extends OpMode {
    private final RobotHardwareConfig robot = new RobotHardwareConfig();
    private final ControlsConfig.ShooterInputReader shooterInputReader = new ControlsConfig.ShooterInputReader();
    private final ControlsConfig.ShooterInput shooterInput = new ControlsConfig.ShooterInput();

    private ShooterSubsystem shooterSubsystem;

    public DcMotorEx intake;
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backRight;
    public DcMotor backLeft;
    public Servo lbstop;
    public Servo rbstop;

    private GoBildaPrismDriver prism;
    private final PrismAnimations.Solid solidRed = new PrismAnimations.Solid(Color.RED);
    private final PrismAnimations.Solid solidPink = new PrismAnimations.Solid(Color.PINK);
    private final PrismAnimations.Solid solidBlue = new PrismAnimations.Solid(Color.BLUE);

    private double lastLoopTime = 0.0;
    private double rx;
    private double lastErrorYaw;

    @Override
    public void init() {
        ControlsConfig.validateAndThrow(telemetry);

        robot.initDriveTrain(hardwareMap);
        robot.initIntake(hardwareMap);
        robot.initBeamStopServos(hardwareMap);
        robot.initPrism(hardwareMap);

        frontLeft = robot.frontLeft;
        frontRight = robot.frontRight;
        backLeft = robot.backLeft;
        backRight = robot.backRight;
        intake = robot.intake;
        lbstop = robot.lbstop;
        rbstop = robot.rbstop;
        prism = robot.prism;

        shooterSubsystem = new ShooterSubsystem(robot);
        shooterSubsystem.init(hardwareMap, RobotHardwareConfig.LIMELIGHT_RED_TELEOP_PIPELINE);
        shooterSubsystem.setAutoAimMode();

        rbstop.setPosition(RobotHardwareConfig.BEAM_STOP_OPEN_POSITION);
        lbstop.setPosition(RobotHardwareConfig.BEAM_STOP_OPEN_POSITION);

        solidRed.setBrightness(100);
        solidRed.setStartIndex(0);
        solidRed.setStopIndex(36);

        solidPink.setBrightness(100);
        solidPink.setStartIndex(12);
        solidPink.setStopIndex(36);

        solidBlue.setBrightness(100);
        solidBlue.setStartIndex(0);
        solidBlue.setStopIndex(11);

        prism.insertAndUpdateAnimation(LayerHeight.LAYER_0, solidRed);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void start() {
        prism.clearAllAnimations();
        prism.insertAndUpdateAnimation(LayerHeight.LAYER_0, solidPink);
        prism.insertAndUpdateAnimation(LayerHeight.LAYER_1, solidBlue);
    }

    @Override
    public void loop() {
        shooterInputReader.read(gamepad1, gamepad2, shooterInput);

        if (shooterInput.setManualModePressed) {
            shooterSubsystem.setManualMode();
        }
        if (shooterInput.setAutoAimModePressed) {
            shooterSubsystem.setAutoAimMode();
        }
        if (shooterInput.sweepTogglePressed) {
            shooterSubsystem.toggleSweepMode();
        }

        if (shooterInput.hoodPresetLowPressed) {
            shooterSubsystem.setManualHoodTargetServo(ShooterConfig.HOOD_PRESET_LOW_SERVO);
        }
        if (shooterInput.hoodPresetHighPressed) {
            shooterSubsystem.setManualHoodTargetServo(ShooterConfig.HOOD_PRESET_HIGH_SERVO);
        }
        if (shooterInput.flywheelPresetLowPressed) {
            shooterSubsystem.setManualFlywheelTargetRpm(ShooterConfig.FLYWHEEL_PRESET_LOW_RPM);
        }
        if (shooterInput.flywheelPresetHighPressed) {
            shooterSubsystem.setManualFlywheelTargetRpm(ShooterConfig.FLYWHEEL_PRESET_HIGH_RPM);
        }

        shooterSubsystem.setManualTurretInput(shooterInput.manualTurret);
        shooterSubsystem.setManualHoodInput(shooterInput.manualHood);
        shooterSubsystem.setManualFlywheelInput(shooterInput.manualFlywheel);
        shooterSubsystem.requestShoot(shooterInput.shootRequested);
        shooterSubsystem.update();

        ShooterSubsystem.TelemetrySnapshot shooter = shooterSubsystem.getTelemetrySnapshot();

        // Intake and beam-stop remain explicit OpMode controls.
        if (gamepad2.left_trigger > 0.2) {
            lbstop.setPosition(RobotHardwareConfig.BEAM_STOP_OPEN_POSITION);
            rbstop.setPosition(RobotHardwareConfig.BEAM_STOP_OPEN_POSITION);
            intake.setVelocity(RobotHardwareConfig.intakeRpmToTicksPerSecond(RobotHardwareConfig.TELEOP_INTAKE_TARGET_RPM));
        } else if (gamepad2.y) {
            intake.setVelocity(RobotHardwareConfig.intakeRpmToTicksPerSecond(RobotHardwareConfig.TELEOP_INTAKE_REVERSE_RPM));
            lbstop.setPosition(RobotHardwareConfig.BEAM_STOP_OPEN_POSITION);
            rbstop.setPosition(RobotHardwareConfig.BEAM_STOP_OPEN_POSITION);
        } else {
            intake.setVelocity(0);
            if (shooter.shootRequested && shooter.readyToShoot) {
                lbstop.setPosition(RobotHardwareConfig.BEAM_STOP_OPEN_POSITION);
                rbstop.setPosition(RobotHardwareConfig.BEAM_STOP_OPEN_POSITION);
            } else {
                lbstop.setPosition(RobotHardwareConfig.BEAM_STOP_CLOSED_POSITION);
                rbstop.setPosition(RobotHardwareConfig.BEAM_STOP_CLOSED_POSITION);
            }
        }

        // Drive assist uses shooter vision yaw when left bumper is held.
        if (gamepad1.left_bumper && shooter.visionValid) {
            double errorYaw = RobotHardwareConfig.GOAL_ALIGN_TARGET_YAW_DEG - shooter.txDeg;
            rx = (errorYaw * RobotHardwareConfig.GOAL_ALIGN_KP)
                    - (RobotHardwareConfig.GOAL_ALIGN_KD * (errorYaw - lastErrorYaw) * RobotHardwareConfig.GOAL_ALIGN_KF);
            lastErrorYaw = errorYaw;
        } else {
            rx = gamepad1.right_stick_x;
        }

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;

        double frontLeftPower = y + x + (RobotHardwareConfig.DRIVE_TURN_MULTIPLIER * rx);
        double backLeftPower = y - x + (RobotHardwareConfig.DRIVE_TURN_MULTIPLIER * rx);
        double frontRightPower = y - x - (RobotHardwareConfig.DRIVE_TURN_MULTIPLIER * rx);
        double backRightPower = y + x - (RobotHardwareConfig.DRIVE_TURN_MULTIPLIER * rx);

        double max = Math.max(
                Math.max(Math.abs(frontLeftPower), Math.abs(backLeftPower)),
                Math.max(Math.abs(frontRightPower), Math.abs(backRightPower))
        );

        if (max > 1.0) {
            frontLeftPower /= max;
            backLeftPower /= max;
            frontRightPower /= max;
            backRightPower /= max;
        }

        frontLeft.setPower(frontLeftPower);
        backLeft.setPower(backLeftPower);
        frontRight.setPower(frontRightPower);
        backRight.setPower(backRightPower);

        double currentTime = getRuntime();
        double deltaTime = currentTime - lastLoopTime;
        lastLoopTime = currentTime;

        telemetry.addData("Shooter State", shooter.state);
        telemetry.addData("Shooter Mode", shooter.controlMode);
        telemetry.addData("Ready", shooter.readyToShoot);
        if (shooter.verboseEnabled) {
            telemetry.addData("Vision", shooter.visionValid);
            telemetry.addData("Sweep", shooter.sweepEnabled);
            telemetry.addData("Turret(rad)", "%.3f -> %.3f", shooter.turretCurrentRad, shooter.turretTargetRad);
            telemetry.addData("Hood(rad)", "%.3f", shooter.hoodTargetAngleRad);
            telemetry.addData("Flywheel RPM", "%.0f / %.0f", shooter.flywheelMeasuredRpm, shooter.flywheelTargetRpm);
            telemetry.addData("Range(m)", "%.2f", shooter.rangeMeters);
            telemetry.addData("TOF(s)", "%.3f", shooter.tofSec);
            telemetry.addData("tx/ty", "%.2f / %.2f", shooter.txDeg, shooter.tyDeg);
        }
        telemetry.addData("Loop dt", deltaTime);
        telemetry.update();
    }

    @Override
    public void stop() {
        shooterSubsystem.stop();
        prism.clearAllAnimations();
        prism.updateAllAnimations();
    }
}
