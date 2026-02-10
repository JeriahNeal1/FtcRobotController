package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/**
 * Wrapper for DcMotor to provide additional helper methods and consistent configuration.
 */
public class MotorWrapper {
    private final DcMotorEx motor;

    public MotorWrapper(DcMotorEx motor) {
        this.motor = motor;
    }

    public void setPower(double power) {
        motor.setPower(power);
    }

    public void setVelocity(double ticksPerSecond) {
        motor.setVelocity(ticksPerSecond);
    }

    public double getCurrentPosition() {
        return motor.getCurrentPosition();
    }

    // TODO: add any additional helper methods you need
}
