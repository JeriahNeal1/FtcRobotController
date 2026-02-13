package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Configuration;

public class ShooterSubsystem implements Subsystem {
    private static final double DEFAULT_SHOOTER_POWER = 1.0;

    private final Configuration.RobotHardware robotHardware;
    private DcMotorEx outtakeRoller;

    public ShooterSubsystem(Configuration.RobotHardware robotHardware) {
        this.robotHardware = robotHardware;
    }

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        if (robotHardware == null) {
            throw new IllegalStateException("ShooterSubsystem requires Configuration.initHardware(...) first.");
        }

        outtakeRoller = robotHardware.outtakeRoller;
        outtakeRoller.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeRoller.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outtakeRoller.setPower(0.0);

        // TODO: Add PIDF velocity control when a dedicated shooter encoder is available.
        // Current approach is open-loop because only one encoder source is reserved for drive distance.
    }

    public void startShooting() {
        startShooting(DEFAULT_SHOOTER_POWER);
    }

    public void startShooting(double power) {
        outtakeRoller.setPower(power);
    }

    public void stopShooting() {
        if (outtakeRoller != null) {
            outtakeRoller.setPower(0.0);
        }
    }

    @Override
    public void start() {
        stopShooting();
    }

    @Override
    public void update() {
        // Intentionally open-loop for this first iteration.
    }

    @Override
    public void stop() {
        stopShooting();
    }
}
