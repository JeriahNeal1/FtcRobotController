package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;

/**
 * Represents a single odometry pod (encoder wheel) used for tracking robot movement.
 */
public class OdometryPod {
    private final DcMotorEx encoderMotor;

    public OdometryPod(DcMotorEx encoderMotor) {
        this.encoderMotor = encoderMotor;
    }

    public void reset() {
        encoderMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        encoderMotor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double getTicks() {
        return encoderMotor.getCurrentPosition();
    }
}
