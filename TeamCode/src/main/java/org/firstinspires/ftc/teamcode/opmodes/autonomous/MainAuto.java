package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Configuration;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LocalizationSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;
import org.firstinspires.ftc.teamcode.vision.HuskyLensSubsystem;

import java.util.ArrayList;
import java.util.List;

@Autonomous(name = "MainAuto", group = "Autonomous")
public class MainAuto extends OpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    private final List<Subsystem> subsystems = new ArrayList<>();

    private Configuration.RobotHardware robotHardware;
    private DriveSubsystem driveSubsystem;
    private IntakeSubsystem intakeSubsystem;
    private ShooterSubsystem shooterSubsystem;
    private HuskyLensSubsystem huskyLensSubsystem;
    private LocalizationSubsystem localizationSubsystem;

    private DcMotorEx primaryDriveEncoderMotor;

    @Override
    public void init() {
        // Confirmed: drive, intake, and outtake names are unchanged and sourced from Configuration constants.
        robotHardware = Configuration.initHardware(hardwareMap);

        driveSubsystem = new DriveSubsystem(robotHardware);
        intakeSubsystem = new IntakeSubsystem(robotHardware);
        shooterSubsystem = new ShooterSubsystem(robotHardware);
        huskyLensSubsystem = new HuskyLensSubsystem();
        localizationSubsystem = new LocalizationSubsystem();

        subsystems.clear();
        subsystems.add(driveSubsystem);
        subsystems.add(intakeSubsystem);
        subsystems.add(shooterSubsystem);
        subsystems.add(huskyLensSubsystem);
        subsystems.add(localizationSubsystem);

        for (Subsystem subsystem : subsystems) {
            subsystem.init(hardwareMap, telemetry);
        }

        // HuskyLens runs tag recognition (not VisionPortal) to support later red/blue goal selection by tag IDs.
        // Two odometry pods are in use now, with third pod support ready through Configuration.ODOMETRY_Z.
        telemetry.addData("Init", "MainAuto initialized");
        telemetry.addData("Primary Encoder Motor", Configuration.PRIMARY_DRIVE_ENCODER_MOTOR_NAME);
        telemetry.addData("Odometry", "2 pods installed; 3rd supported");
        telemetry.update();
    }

    @Override
    public void start() {
        for (Subsystem subsystem : subsystems) {
            subsystem.start();
        }

        runtime.reset();

        // One encoder source is tracked now: PRIMARY_DRIVE_ENCODER_MOTOR_NAME.
        // Shooter remains open-loop until a second encoder path is available.
        primaryDriveEncoderMotor = selectPrimaryDriveEncoderMotor();
        if (primaryDriveEncoderMotor != null) {
            primaryDriveEncoderMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            primaryDriveEncoderMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    @Override
    public void loop() {
        for (Subsystem subsystem : subsystems) {
            subsystem.update();
        }

        LocalizationSubsystem.PoseEstimate poseEstimate = localizationSubsystem.getPoseEstimate();

        telemetry.addData("Runtime (s)", "%.2f", runtime.seconds());
        telemetry.addData("HuskyLens", huskyLensSubsystem.getStatus());
        telemetry.addData("Odometry", "2 pods installed; 3rd supported");
        telemetry.addData(
                "Pose (x,y,heading)",
                "%.2f in, %.2f in, %.2f rad",
                poseEstimate.xInches,
                poseEstimate.yInches,
                poseEstimate.headingRadians
        );
        if (primaryDriveEncoderMotor != null) {
            telemetry.addData("Primary Encoder Ticks", primaryDriveEncoderMotor.getCurrentPosition());
        }
        telemetry.update();

        // TODO: Enforce a hard 30.0s autonomous stop.
        // TODO: During the following 8.0s transition, keep intake/shooter/drive outputs at zero.
        // TODO: Replace placeholder loop with a scoring state machine for automatic cycles.
        // TODO: Use HuskyLens tag IDs to choose red vs blue goals reliably before shooting.
        // TODO: Replace single-motor encoder distance checks with odometry pod localization.
    }

    @Override
    public void stop() {
        if (driveSubsystem != null) {
            driveSubsystem.stopAllMotors();
        }
        if (intakeSubsystem != null) {
            intakeSubsystem.stopIntake();
        }
        if (shooterSubsystem != null) {
            shooterSubsystem.stopShooting();
        }

        if (robotHardware != null) {
            robotHardware.leftFrontMotor.setPower(0.0);
            robotHardware.leftBackMotor.setPower(0.0);
            robotHardware.rightFrontMotor.setPower(0.0);
            robotHardware.rightBackMotor.setPower(0.0);
            robotHardware.intakeRoller.setPower(0.0);
            robotHardware.outtakeRoller.setPower(0.0);
        }

        for (Subsystem subsystem : subsystems) {
            subsystem.stop();
        }
    }

    private DcMotorEx selectPrimaryDriveEncoderMotor() {
        if (robotHardware == null) {
            return null;
        }

        String primaryName = Configuration.PRIMARY_DRIVE_ENCODER_MOTOR_NAME;
        if (Configuration.LEFT_FRONT_MOTOR_NAME.equals(primaryName)) {
            return robotHardware.leftFrontMotor;
        }
        if (Configuration.LEFT_BACK_MOTOR_NAME.equals(primaryName)) {
            return robotHardware.leftBackMotor;
        }
        if (Configuration.RIGHT_FRONT_MOTOR_NAME.equals(primaryName)) {
            return robotHardware.rightFrontMotor;
        }
        if (Configuration.RIGHT_BACK_MOTOR_NAME.equals(primaryName)) {
            return robotHardware.rightBackMotor;
        }
        return robotHardware.leftFrontMotor;
    }
}
