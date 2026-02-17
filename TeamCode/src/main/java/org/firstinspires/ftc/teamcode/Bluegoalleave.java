package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

@Configurable
@Autonomous(name = "Back up fib", group = "Competition")
public class Bluegoalleave extends OpMode {
    private final RobotHardwareConfig robot = new RobotHardwareConfig();
    private Follower follower; // Pedro Pathing follower instance
    private Timer pathTimer;
    ElapsedTime stateTimer = new ElapsedTime();
    GoBildaPrismDriver prism;

    PrismAnimations.RainbowSnakes rainbow = new PrismAnimations.RainbowSnakes();
    PrismAnimations.Solid solidRed = new PrismAnimations.Solid(Color.RED);
    PrismAnimations.Solid solidPink = new PrismAnimations.Solid(Color.PINK);
    PrismAnimations.Solid solidGreen = new PrismAnimations.Solid(Color.GREEN);
    PrismAnimations.Solid solidBlue = new PrismAnimations.Solid(Color.BLUE);
    PrismAnimations.Solid solidYellow = new PrismAnimations.Solid(Color.YELLOW);
    PathState pathState;
    public enum PathState{
        DRIVE_STARTPOS_SHOOTPOS,
        END
    }

    private ShooterSubsystem shooterSubsystem;
    private DcMotorEx intake;
    private Servo lbstop, rbstop;
    public static double SHOOT_TIME = 3400;
    public static double INTAKE_RPM = 575;
    public static double INTAKE_SHOOT_RPM = 550;
    public static double SHOOT_ANGLE = 38;


    private final Pose startPose = new Pose(95.40391254315304, 8.634292289988485, Math.toRadians(90));
    private final Pose shootPose = new Pose (98.16340621403913, 33.50287686996549, Math.toRadians(Math.toRadians(90)));

    private PathChain startdriveshoot, drivetocloseload,  end;
    public void buildPaths() {
        startdriveshoot = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setConstantHeadingInterpolation(startPose.getHeading())
                .build();
    }
    public void statePathUpdate(){
        switch(pathState){
            case DRIVE_STARTPOS_SHOOTPOS:
                follower.followPath(startdriveshoot, false);
                setPathState(PathState.END);
                stateTimer.reset();
                break;
            case END:

            default:
                break;
        }
    }

    public void setPathState(PathState newState){
        pathState = newState;
        pathTimer.resetTimer();
    }

    @Override
    public void init() {
        pathState = PathState.DRIVE_STARTPOS_SHOOTPOS;
        pathTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);
        shooterSubsystem = new ShooterSubsystem(robot);
        shooterSubsystem.init(hardwareMap, RobotHardwareConfig.LIMELIGHT_BLUE_TELEOP_PIPELINE);
        shooterSubsystem.setManualMode();

        robot.initIntake(hardwareMap);
        robot.initBeamStopServos(hardwareMap);
        robot.initPrism(hardwareMap);

        intake = robot.intake;
        lbstop = robot.lbstop;
        rbstop = robot.rbstop;
        prism = robot.prism;

        rbstop.setPosition(RobotHardwareConfig.BEAM_STOP_CLOSED_POSITION);
        lbstop.setPosition(RobotHardwareConfig.BEAM_STOP_CLOSED_POSITION);

        solidRed.setBrightness(100);
        solidRed.setStartIndex(0);
        solidRed.setStopIndex(36);

        solidPink.setBrightness(100);
        solidPink.setStartIndex(0);
        solidPink.setStopIndex(36);

        rainbow.setNumberOfSnakes(3);
        rainbow.setSnakeLength(3);
        rainbow.setSpacingBetween(2);
        rainbow.setSpeed(0.6f);

        solidBlue.setBrightness(100);
        solidBlue.setStopIndex(36);
        solidBlue.setStartIndex(18);

        solidYellow.setStartIndex(0);
        solidYellow.setStopIndex(17);
        solidYellow.setBrightness(100);

        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, solidBlue);
        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_1, solidYellow);

        buildPaths();
        follower.setStartingPose(startPose);

    }

    public void start() {
        setPathState(PathState.DRIVE_STARTPOS_SHOOTPOS);
        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, solidPink);
        stateTimer.reset();
    }

    @Override
    public void loop() {
        follower.update();
        shooterSubsystem.update();
        statePathUpdate();

    }

    @Override
    public void stop() {
        shooterSubsystem.stop();
        prism.clearAllAnimations();
        prism.updateAllAnimations();
    }

}
