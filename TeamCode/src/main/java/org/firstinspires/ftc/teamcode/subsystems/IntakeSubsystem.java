package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Configuration;

public class IntakeSubsystem implements Subsystem {
    private static final double DEFAULT_INTAKE_POWER = 1.0;

    private final Configuration.RobotHardware robotHardware;
    private DcMotorEx intakeRoller;

    public IntakeSubsystem(Configuration.RobotHardware robotHardware) {
        this.robotHardware = robotHardware;
    }

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        if (robotHardware == null) {
            throw new IllegalStateException("IntakeSubsystem requires Configuration.initHardware(...) first.");
        }

        intakeRoller = robotHardware.intakeRoller;
        intakeRoller.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeRoller.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeRoller.setPower(0.0);
    }

    public void startIntake() {
        startIntake(DEFAULT_INTAKE_POWER);
    }

    public void startIntake(double power) {
        intakeRoller.setPower(power);
    }

    public void stopIntake() {
        if (intakeRoller != null) {
            intakeRoller.setPower(0.0);
        }
    }

    @Override
    public void start() {
        stopIntake();
    }

    @Override
    public void update() {
        // Intentionally open-loop for this first iteration.
    }

    @Override
    public void stop() {
        stopIntake();
    }
}
