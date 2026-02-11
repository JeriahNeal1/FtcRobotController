package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Configuration;

public class DriveSubsystem implements Subsystem {
    private final Configuration.RobotHardware robotHardware;

    private DcMotorEx leftFrontMotor;
    private DcMotorEx leftBackMotor;
    private DcMotorEx rightFrontMotor;
    private DcMotorEx rightBackMotor;
    private Telemetry telemetry;

    public DriveSubsystem(Configuration.RobotHardware robotHardware) {
        this.robotHardware = robotHardware;
    }

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        if (robotHardware == null) {
            throw new IllegalStateException("DriveSubsystem requires Configuration.initHardware(...) first.");
        }

        leftFrontMotor = robotHardware.leftFrontMotor;
        leftBackMotor = robotHardware.leftBackMotor;
        rightFrontMotor = robotHardware.rightFrontMotor;
        rightBackMotor = robotHardware.rightBackMotor;

        configureDriveMotor(leftFrontMotor);
        configureDriveMotor(leftBackMotor);
        configureDriveMotor(rightFrontMotor);
        configureDriveMotor(rightBackMotor);
    }

    private void configureDriveMotor(DcMotorEx motor) {
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setPower(0.0);
    }

    public void setDrivePower(double x, double y, double rotation) {
        double leftFrontPower = y + x + rotation;
        double rightFrontPower = y - x - rotation;
        double leftBackPower = y - x + rotation;
        double rightBackPower = y + x - rotation;

        double maxMagnitude = Math.max(
                1.0,
                Math.max(
                        Math.abs(leftFrontPower),
                        Math.max(
                                Math.abs(rightFrontPower),
                                Math.max(Math.abs(leftBackPower), Math.abs(rightBackPower))
                        )
                )
        );

        leftFrontMotor.setPower(leftFrontPower / maxMagnitude);
        rightFrontMotor.setPower(rightFrontPower / maxMagnitude);
        leftBackMotor.setPower(leftBackPower / maxMagnitude);
        rightBackMotor.setPower(rightBackPower / maxMagnitude);
    }

    public void stopAllMotors() {
        if (leftFrontMotor != null) {
            leftFrontMotor.setPower(0.0);
        }
        if (leftBackMotor != null) {
            leftBackMotor.setPower(0.0);
        }
        if (rightFrontMotor != null) {
            rightFrontMotor.setPower(0.0);
        }
        if (rightBackMotor != null) {
            rightBackMotor.setPower(0.0);
        }
    }

    @Override
    public void start() {
        stopAllMotors();
    }

    @Override
    public void update() {
        // TODO: Integrate odometry/localization feedback for closed-loop autonomous driving.
    }

    @Override
    public void stop() {
        stopAllMotors();
        if (telemetry != null) {
            telemetry.addData("DriveSubsystem", "Stopped");
        }
    }
}
