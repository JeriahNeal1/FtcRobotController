package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.subsystems.*;
import org.firstinspires.ftc.teamcode.commands.*;
import org.firstinspires.ftc.teamcode.localization.Pose2d;

/**
 * Autonomous OpMode that repeatedly performs intake, navigation to goal, shooting, and goal reset.
 */
@Autonomous(name = "ContinuousCycleAuto", group = "Autonomous")
public class ContinuousCycleAuto extends LinearOpMode {
    private DriveSubsystem drive;
    private IntakeSubsystem intake;
    private ShooterSubsystem shooter;
    private VisionSubsystem vision;
    private LocalizationSubsystem localization;
    private PathPlanningSubsystem planner;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize subsystems
        drive = new DriveSubsystem();
        intake = new IntakeSubsystem();
        shooter = new ShooterSubsystem();
        vision = new VisionSubsystem();
        localization = new LocalizationSubsystem();
        planner = new PathPlanningSubsystem();

        drive.init(hardwareMap, telemetry);
        intake.init(hardwareMap, telemetry);
        shooter.init(hardwareMap, telemetry);
        vision.init(hardwareMap, telemetry);
        localization.init(hardwareMap, telemetry);
        planner.init(hardwareMap, telemetry);

        waitForStart();

        drive.start();
        intake.start();
        shooter.start();
        vision.start();
        localization.start();
        planner.start();

        // Main autonomous loop
        while (opModeIsActive()) {
            // TODO: implement state machine to perform continuous cycles
            // Example: IntakeCommand -> NavigateToPoseCommand -> ShootCommand -> ResetGoalCommand

            // Update subsystems
            drive.update();
            intake.update();
            shooter.update();
            vision.update();
            localization.update();
            planner.update();

            idle();
        }

        drive.stop();
        intake.stop();
        shooter.stop();
        vision.stop();
        localization.stop();
        planner.stop();
    }
}
