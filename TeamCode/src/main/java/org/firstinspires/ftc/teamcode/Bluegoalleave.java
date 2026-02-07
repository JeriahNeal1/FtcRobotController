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
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Configurable
@Autonomous(name = "Back up fib", group = "Competition")
public class Bluegoalleave extends OpMode {
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

    private DualPidMotor flywheel;
    private DcMotorEx intake;
    private Servo lbstop, rbstop, lhoodtilt, rhoodtilt;
    private static final double MIN_TILT = 0.02;
    private static double HOOD_TILT = .34;
    private static double FLYWHEEL_RPM = 2300;
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
        flywheel = new DualPidMotor (hardwareMap, "topflywheel", "bottomflywheel");
        lhoodtilt = hardwareMap.get(Servo.class, "lhoodtilt");
        rhoodtilt = hardwareMap.get(Servo.class, "rhoodtilt");
        intake = hardwareMap.get(DcMotorEx.class, "intake");
        lbstop = hardwareMap.get(Servo.class, "lbstop");
        rbstop = hardwareMap.get(Servo.class, "rbstop");
        prism = hardwareMap.get(GoBildaPrismDriver.class,"prism");

        intake.setDirection(DcMotor.Direction.REVERSE);
        rbstop.setDirection(Servo.Direction.REVERSE);
        lhoodtilt.setDirection(Servo.Direction.REVERSE);

        lhoodtilt.setPosition(MIN_TILT);
        rbstop.setPosition(0.15);
        lbstop.setPosition(0.15);

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
        flywheel.Update();
        statePathUpdate();

    }

    @Override
    public void stop() {
        prism.clearAllAnimations();
        prism.updateAllAnimations();
    }

}