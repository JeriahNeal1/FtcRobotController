package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.MotorWrapper;
import org.firstinspires.ftc.teamcode.config.HardwareConfig;

/**
 * Controls the intake mechanism to pull balls into the robot.
 */
public class IntakeSubsystem implements Subsystem {
    private MotorWrapper intakeMotor;

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, HardwareConfig.INTAKE_MOTOR);
        intakeMotor = new MotorWrapper(motor);
        // TODO: configure intake motor (direction, run mode)
    }

    @Override
    public void start() {
        // Initialization before running
    }

    @Override
    public void update() {
        // Intake control logic
        // TODO: implement automatic or commanded intake control
    }

    @Override
    public void stop() {
        intakeMotor.setPower(0);
    }
}
