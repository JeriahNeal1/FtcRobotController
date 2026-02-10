package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Servo;

/**
 * Wrapper for Servo to standardize configuration and expose helper methods.
 */
public class ServoWrapper {
    private final Servo servo;

    public ServoWrapper(Servo servo) {
        this.servo = servo;
    }

    public void setPosition(double position) {
        servo.setPosition(position);
    }

    public double getPosition() {
        return servo.getPosition();
    }

    // TODO: Add any servo-specific helper methods
}
