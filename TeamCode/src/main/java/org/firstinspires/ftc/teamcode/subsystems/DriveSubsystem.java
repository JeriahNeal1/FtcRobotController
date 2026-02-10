package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.MotorWrapper;
import org.firstinspires.ftc.teamcode.config.HardwareConfig;

/**
 * DriveSubsystem controls the holonomic drive system of the robot.
 */
public class DriveSubsystem implements Subsystem {
    private MotorWrapper leftFront;
    private MotorWrapper leftRear;
    private MotorWrapper rightFront;
    private MotorWrapper rightRear;

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        // Initialize motors using HardwareConfig names
        DcMotorEx lf = hardwareMap.get(DcMotorEx.class, HardwareConfig.LEFT_FRONT_MOTOR);
        DcMotorEx lr = hardwareMap.get(DcMotorEx.class, HardwareConfig.LEFT_REAR_MOTOR);
        DcMotorEx rf = hardwareMap.get(DcMotorEx.class, HardwareConfig.RIGHT_FRONT_MOTOR);
        DcMotorEx rr = hardwareMap.get(DcMotorEx.class, HardwareConfig.RIGHT_REAR_MOTOR);
        leftFront = new MotorWrapper(lf);
        leftRear  = new MotorWrapper(lr);
        rightFront = new MotorWrapper(rf);
        rightRear  = new MotorWrapper(rr);
        // TODO: configure motor directions, zero power behavior, run modes, etc.
    }

    @Override
    public void start() {
        // TODO: reset encoders if necessary
    }

    @Override
    public void update() {
        // TODO: implement drive control logic (e.g. set motor powers based on pose controller)
    }

    @Override
    public void stop() {
        // TODO: stop motors
        leftFront.setPower(0);
        leftRear.setPower(0);
        rightFront.setPower(0);
        rightRear.setPower(0);
    }
}
