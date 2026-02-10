package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.dashboard.DashboardTelemetry;

/**
 * Basic teleop mode skeleton.
 */
@TeleOp(name = "TeleOpMode", group = "TeleOp")
public class TeleOpMode extends OpMode {
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private ShooterSubsystem shooter;
    private DashboardTelemetry dashboard;

    @Override
    public void init() {
        drive = new DriveSubsystem();
        intake = new IntakeSubsystem();
        shooter = new ShooterSubsystem();
        dashboard = new DashboardTelemetry(telemetry);

        drive.init(hardwareMap, telemetry);
        intake.init(hardwareMap, telemetry);
        shooter.init(hardwareMap, telemetry);
    }

    @Override
    public void start() {
        drive.start();
        intake.start();
        shooter.start();
    }

    @Override
    public void loop() {
        // TODO: read gamepad inputs and control subsystems

        drive.update();
        intake.update();
        shooter.update();

        dashboard.update();
    }

    @Override
    public void stop() {
        drive.stop();
        intake.stop();
        shooter.stop();
    }
}
