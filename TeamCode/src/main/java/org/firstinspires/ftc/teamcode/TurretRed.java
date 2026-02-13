package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;

@Configurable
@TeleOp(name = "turret red", group = "Competition")

public class TurretRed extends OpMode {
    DcMotorEx turretMotor;
    Limelight3A limelight;
    public double targetAngle;
    public double turretAngle;
    // --- Compute magnitude ---
    public double deadband = 0.2;
    public static double kP = 0.01;
    public static double MAX_ANGLE = 170;
    public static double MIN_ANGLE = -170;
    double ticksPerTurretRev = 537.7 * (200.0 / 86.0);

    @Override
    public void init(){
    turretMotor = hardwareMap.get(DcMotorEx.class, "turret");
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.pipelineSwitch(1); // Switch to pipeline number 1

}

    @Override
    public void start(){
        limelight.start();
    }

    @Override
    public void loop() {
        double x = gamepad2.right_stick_x;
        double y = -gamepad2.right_stick_y;
        double magnitude = Math.hypot(x, y);

        turretAngle = turretMotor.getCurrentPosition();

        if (magnitude > 0.2) {
            targetAngle = Math.toDegrees(Math.atan2(x, y));
        }

// Clamp target inside safe range
        targetAngle = Math.max(MIN_ANGLE, Math.min(MAX_ANGLE, targetAngle));

// Simple linear error (NO normalize)
        double error = targetAngle - turretAngle;

// Soft stop protection
        if (turretAngle >= MAX_ANGLE && error > 0) error = 0;
        if (turretAngle <= MIN_ANGLE && error < 0) error = 0;

        double output = kP * error;

        turretMotor.setPower(output);

        telemetry.addData("magnitude", magnitude);
        telemetry.addData("turret angle", turretAngle);
        telemetry.addData("target angle", targetAngle);

    }
}
