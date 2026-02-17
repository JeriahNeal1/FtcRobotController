package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotHardwareConfig;

/**
 * Reusable turret controller with Limelight auto-aim and manual joystick fallback.
 */
public class TurretSubsystem {
    private final RobotHardwareConfig robot;

    private double targetAngleRad;
    private double currentAngleRad;
    private double errorRad;
    private double motorOutput;
    private double joystickMagnitude;
    private double lastYawDeg;
    private boolean targetVisible;

    public TurretSubsystem(RobotHardwareConfig robot) {
        this.robot = robot;
        targetAngleRad = 0.0;
    }

    public void init(HardwareMap hardwareMap, int limelightPipeline) {
        robot.initTurret(hardwareMap, true);
        robot.initLimelight(hardwareMap, limelightPipeline);
    }

    public void startVision() {
        robot.startLimelight();
    }

    public void stop() {
        if (robot.turretMotor != null) {
            robot.turretMotor.setPower(0.0);
        }
    }

    public void setTargetAngle(double targetAngleRad) {
        this.targetAngleRad = RobotHardwareConfig.clamp(
                targetAngleRad,
                RobotHardwareConfig.turretMinAngleRad(),
                RobotHardwareConfig.turretMaxAngleRad()
        );
    }

    public void setTargetAngleDegrees(double targetAngleDeg) {
        setTargetAngle(Math.toRadians(targetAngleDeg));
    }

    public double getTargetAngleRad() {
        return targetAngleRad;
    }

    public double getCurrentAngleRad() {
        return currentAngleRad;
    }

    public double getErrorRad() {
        return errorRad;
    }

    public double getMotorOutput() {
        return motorOutput;
    }

    public double getJoystickMagnitude() {
        return joystickMagnitude;
    }

    public double getLastYawDeg() {
        return lastYawDeg;
    }

    public boolean isTargetVisible() {
        return targetVisible;
    }

    public void update() {
        update(0.0, 0.0);
    }

    public void update(double joystickX, double joystickY) {
        if (robot.turretMotor == null) {
            return;
        }

        currentAngleRad = RobotHardwareConfig.turretTicksToRadians(robot.turretMotor.getCurrentPosition());
        joystickMagnitude = Math.hypot(joystickX, joystickY);

        targetVisible = false;
        if (robot.limelight != null) {
            LLResult result = robot.limelight.getLatestResult();
            targetVisible = result != null && result.isValid();
            if (targetVisible) {
                lastYawDeg = result.getTx();
                double yawRad = Math.toRadians(lastYawDeg);
                setTargetAngle(currentAngleRad - yawRad);
            }
        }

        if (!targetVisible && joystickMagnitude > RobotHardwareConfig.TURRET_JOYSTICK_DEADBAND) {
            setTargetAngle(Math.atan2(joystickX, joystickY));
        }

        errorRad = computeLimitedError(targetAngleRad, currentAngleRad);
        motorOutput = RobotHardwareConfig.clamp(
                RobotHardwareConfig.TURRET_KP * errorRad,
                -1.0,
                1.0
        );

        robot.turretMotor.setPower(motorOutput);
    }

    public void addTelemetry(Telemetry telemetry) {
        telemetry.addData("TargetSeen", targetVisible);
        telemetry.addData("TagYawDeg", lastYawDeg);
        telemetry.addData("JoystickMag", joystickMagnitude);
        telemetry.addData("TurretAngleRad", currentAngleRad);
        telemetry.addData("TargetAngleRad", targetAngleRad);
        telemetry.addData("ErrorRad", errorRad);
        telemetry.addData("MotorOutput", motorOutput);
    }

    private double computeLimitedError(double targetAngle, double currentAngle) {
        double minAngleRad = RobotHardwareConfig.turretMinAngleRad();
        double maxAngleRad = RobotHardwareConfig.turretMaxAngleRad();

        double directDiff = targetAngle - currentAngle;
        double wrappedDiff = RobotHardwareConfig.wrapAngleRad(directDiff);

        double predictedAngle = currentAngle + wrappedDiff;
        if (predictedAngle < minAngleRad || predictedAngle > maxAngleRad) {
            wrappedDiff = directDiff;
        }

        if (currentAngle >= maxAngleRad && wrappedDiff > 0.0) {
            wrappedDiff = 0.0;
        }
        if (currentAngle <= minAngleRad && wrappedDiff < 0.0) {
            wrappedDiff = 0.0;
        }

        return wrappedDiff;
    }
}
