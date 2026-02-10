package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.config.HardwareConfig;
import org.firstinspires.ftc.teamcode.hardware.MotorWrapper;
import org.firstinspires.ftc.teamcode.config.RobotConstants;

/**
 * Controls the shooter mechanism responsible for launching balls at the goal.
 */
public class ShooterSubsystem implements Subsystem {
    private MotorWrapper shooterMotor;

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, HardwareConfig.SHOOTER_MOTOR);
        shooterMotor = new MotorWrapper(motor);
        // TODO: configure shooter motor (set run mode, PIDF coefficients if using velocity control)
    }

    @Override
    public void start() {
        // Prepare shooter subsystem before starting control loop
    }

    @Override
    public void update() {
        // Shooter control logic
        // TODO: spin up shooter and maintain target velocity when commanded
    }

    @Override
    public void stop() {
        shooterMotor.setPower(0);
    }
}
