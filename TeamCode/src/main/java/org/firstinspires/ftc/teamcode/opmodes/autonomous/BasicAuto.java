package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;

/**
 * A simple autonomous that just drives a short distance and stops. Useful for testing.
 */
@Autonomous(name = "BasicAuto", group = "Autonomous")
public class BasicAuto extends LinearOpMode {
    private DriveSubsystem drive;

    @Override
    public void runOpMode() throws InterruptedException {
        drive = new DriveSubsystem();
        drive.init(hardwareMap, telemetry);
        waitForStart();
        drive.start();
        // TODO: drive forward for a short distance using encoder-based position control
        sleep(2000);
        drive.stop();
    }
}
