package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

@Configurable
@TeleOp(name = "Turret Control", group = "Competition")
public class TurretControl extends OpMode {
    private final RobotHardwareConfig robot = new RobotHardwareConfig();
    private final ControlsConfig.ShooterInputReader shooterInputReader = new ControlsConfig.ShooterInputReader();
    private final ControlsConfig.ShooterInput shooterInput = new ControlsConfig.ShooterInput();
    private ShooterSubsystem shooterSubsystem;

    @Override
    public void init() {
        ControlsConfig.validateAndThrow(telemetry);
        shooterSubsystem = new ShooterSubsystem(robot);
        shooterSubsystem.init(hardwareMap, RobotHardwareConfig.LIMELIGHT_TURRET_PIPELINE);
        shooterSubsystem.setAutoAimMode();
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

        shooterSubsystem.setManualTurretInput(shooterInput.manualTurret);
        shooterSubsystem.setManualHoodInput(shooterInput.manualHood);
        shooterSubsystem.setManualFlywheelInput(shooterInput.manualFlywheel);
        shooterSubsystem.requestShoot(shooterInput.shootRequested);
        shooterSubsystem.update();

        ShooterSubsystem.TelemetrySnapshot snapshot = shooterSubsystem.getTelemetrySnapshot();
        telemetry.addData("State", snapshot.state);
        telemetry.addData("Mode", snapshot.controlMode);
        telemetry.addData("Vision", snapshot.visionValid);
        telemetry.addData("Sweep", snapshot.sweepEnabled);
        telemetry.addData("Turret Current (rad)", snapshot.turretCurrentRad);
        telemetry.addData("Turret Target (rad)", snapshot.turretTargetRad);
        telemetry.addData("Turret Error (rad)", snapshot.turretErrorRad);
        telemetry.addData("Turret Power", snapshot.turretPower);
        telemetry.addData("Ready", shooterSubsystem.isReadyToShoot());
        telemetry.update();
    }

    @Override
    public void stop() {
        shooterSubsystem.stop();
    }
}
