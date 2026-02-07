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
@Autonomous(name = "RedGoalAuto", group = "Competition")
public class RedGoalAuto extends OpMode {
    private Follower follower; // Pedro Pathing follower instance
    private Timer pathTimer;
    ElapsedTime stateTimer = new ElapsedTime();
    GoBildaPrismDriver prism;

    PrismAnimations.RainbowSnakes rainbow = new PrismAnimations.RainbowSnakes();
    PrismAnimations.Solid solidRed = new PrismAnimations.Solid(Color.RED);
    PrismAnimations.Solid solidPink = new PrismAnimations.Solid(Color.PINK);
    PrismAnimations.Solid solidGreen = new PrismAnimations.Solid(Color.GREEN);
    PathState pathState;
    public enum PathState{
        DRIVE_STARTPOS_SHOOTPOS,
        SHOOT,
        DRIVE_SHOOTPOS_LOAD1POS,
        CLOSELOAD,
        CLASSIFIER_SETUP,
        CLASSIFIER,
        DRIVE_CLASSIFIER_SHOOTPOS,
        SHOOT1,
        DRIVE_SHOOTPOS_LOAD2POS,
        MIDDLELOAD,
        MIDDLELOADLEAVE,
        DRIVE_MIDDLELOADPOS_SHOOTPOS,
        SHOOT2,
        DRIVE_SHOOTPOS_LOAD3POS,
        FARLOAD,
        DRIVE_FARLOADPOS_SHOOTPOS,
        SHOOT3,
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


    private final Pose startPose = new Pose(123.627157652, 121.34407364787114, Math.toRadians(36));
    private final Pose shootPose = new Pose (98.7652474108, 97.64326812428078, Math.toRadians(SHOOT_ANGLE));
    private final Pose shootTravelPose = new Pose (104, 93);
    private final Pose load1StartPose = new Pose (95, 88, Math.toRadians(0));
    private final Pose load1EndPose = new Pose (120, 88, Math.toRadians(0));
    private final Pose load2StartPose = new Pose (95, 63, Math.toRadians(0));
    private final Pose load2ControlPose = new Pose (95, 54, Math.toRadians(0));
    private final Pose load2EndPose = new Pose (126, 62, Math.toRadians(0));
    private final Pose load3StartPose = new Pose (90, 39, Math.toRadians(0));
    private final Pose load3EndPose = new Pose (129, 39, Math.toRadians(0));
    private final Pose classifierEmptyPose = new Pose (122.5, 85, Math.toRadians(280));
    private final Pose classiferControlPose = new Pose (104, 85, Math.toRadians(0));
    private PathChain startdriveshoot, drivetocloseload, closeload, closeclassifieralign, closeclassifier, closeloadshoot, drivetomiddleload, middleload, middleloadsetup, middleloadtravel, drivetofarload, farload, farloadshoot, end;
    public void buildPaths() {
        startdriveshoot = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        drivetocloseload = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, load1StartPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), load1StartPose.getHeading())
                .build();
        closeload = follower.pathBuilder()
                .addPath(new BezierLine(load1StartPose, load1EndPose))
                .setConstantHeadingInterpolation(load1EndPose.getHeading())
                .build();
        closeclassifieralign = follower.pathBuilder()
                .addPath(new BezierLine (load1EndPose, classiferControlPose))
                .setConstantHeadingInterpolation(Math.toRadians(classiferControlPose.getHeading()))
                .build();
        closeclassifier = follower.pathBuilder()
                .addPath(new BezierLine(classiferControlPose, classifierEmptyPose))
                .setConstantHeadingInterpolation(classifierEmptyPose.getHeading())
                .build();
        closeloadshoot = follower.pathBuilder()
                .addPath(new BezierLine(classifierEmptyPose, shootPose))
                .setLinearHeadingInterpolation(classifierEmptyPose.getHeading(), shootPose.getHeading())
                .build();
        drivetomiddleload = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, load2StartPose))
                .setConstantHeadingInterpolation(load2StartPose.getHeading())
                .build();
        middleload = follower.pathBuilder()
                .addPath(new BezierLine(load2StartPose, load2EndPose))
                .setConstantHeadingInterpolation(load2EndPose.getHeading())
                .build();

        middleloadsetup = follower.pathBuilder()
                .addPath(new BezierLine(load2EndPose, load2StartPose))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();
        middleloadtravel = follower.pathBuilder()
                .addPath(new BezierLine(load2ControlPose, shootPose))
                .setLinearHeadingInterpolation(load2StartPose.getHeading(),shootPose.getHeading())
                .build();
        drivetofarload = follower.pathBuilder()
                .addPath(new BezierLine(shootPose,load3StartPose))
                .setConstantHeadingInterpolation(Math.toRadians(0))
                .build();
        farload = follower.pathBuilder()
                .addPath(new BezierLine(load3StartPose, load3EndPose))
                .setConstantHeadingInterpolation(load3EndPose.getHeading())
                .build();
        farloadshoot = follower.pathBuilder()
                .addPath(new BezierLine(load3EndPose, shootPose))
                .setLinearHeadingInterpolation(load3EndPose.getHeading(), shootPose.getHeading())
                .build();
        end = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, classiferControlPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), classifierEmptyPose.getHeading())
                .build();
    }
    public void statePathUpdate(){
        switch(pathState){
            case DRIVE_STARTPOS_SHOOTPOS:
                flywheel.setVelocity(FLYWHEEL_RPM);
                lhoodtilt.setPosition(HOOD_TILT);
                rhoodtilt.setPosition(HOOD_TILT);
                follower.followPath(startdriveshoot, false);
                setPathState(PathState.SHOOT);
                stateTimer.reset();
                break;
            case SHOOT:
                if (!follower.isBusy()){
                    intake.setVelocity((145.1 * INTAKE_SHOOT_RPM)/60);
                    lbstop.setPosition(0);
                    rbstop.setPosition(0);

                    if (stateTimer.milliseconds() > SHOOT_TIME + 50) {
                        flywheel.setVelocity(0);
                        intake.setVelocity(0);
                        lhoodtilt.setPosition(0.02);
                        rhoodtilt.setPosition(0.02);
                        follower.followPath(drivetocloseload);
                        setPathState(PathState.DRIVE_SHOOTPOS_LOAD1POS);
                    }
                }
                break;
            case DRIVE_SHOOTPOS_LOAD1POS:
                if (!follower.isBusy()) {
                    flywheel.setVelocity(-FLYWHEEL_RPM);
                    setPathState(PathState.CLOSELOAD);
                    stateTimer.reset();
                }
                break;
            case CLOSELOAD:
                if (!follower.isBusy()){
                    intake.setVelocity((145.1*INTAKE_RPM)/60);
                    lbstop.setPosition(0.13);
                    rbstop.setPosition(0.13);
                    follower.followPath(closeload, 0.8, false);
                    setPathState(PathState.CLASSIFIER_SETUP);
                    stateTimer.reset();
                }
                break;
            case CLASSIFIER_SETUP:
                if (!follower.isBusy()){
                    intake.setVelocity(0);
                    follower.followPath(closeclassifieralign, false);
                    setPathState(PathState.CLASSIFIER);
                }
                break;
            case CLASSIFIER:
                if (!follower.isBusy()){
                    follower.followPath(closeclassifier, 0.8, true);
                    setPathState(PathState.DRIVE_CLASSIFIER_SHOOTPOS);
                }
            case DRIVE_CLASSIFIER_SHOOTPOS:
                if (!follower.isBusy()){
                    if (stateTimer.milliseconds() < 300) {
                        intake.setVelocity(-400);
                    } else if (stateTimer.milliseconds() > 500){
                        intake.setVelocity(0);
                        lbstop.setPosition(.15);
                        rbstop.setPosition(.15);
                    }

                    follower.followPath(closeloadshoot, false);
                    setPathState(PathState.SHOOT1);
                    stateTimer.reset();
                }
                break;
            case SHOOT1:
                lhoodtilt.setPosition(HOOD_TILT);
                rhoodtilt.setPosition(HOOD_TILT);
                flywheel.setVelocity(FLYWHEEL_RPM);
                if (!follower.isBusy()){
                    intake.setVelocity((145.1 * INTAKE_SHOOT_RPM)/60);
                    lbstop.setPosition(0);
                    rbstop.setPosition(0);

                    if (stateTimer.milliseconds() > SHOOT_TIME - 350) {
                        flywheel.setVelocity(0);
                        lhoodtilt.setPosition(0.02);
                        rhoodtilt.setPosition(0.02);
                        intake.setVelocity(0);
                        follower.followPath(drivetomiddleload);
                        stateTimer.reset();
                        setPathState(PathState.DRIVE_SHOOTPOS_LOAD2POS);
                    }
                }
                break;
            case DRIVE_SHOOTPOS_LOAD2POS:
                flywheel.setVelocity(-FLYWHEEL_RPM);
                lbstop.setPosition(0.14);
                rbstop.setPosition(0.14);
                if (!follower.isBusy()) {
                    intake.setVelocity((145.1 * INTAKE_RPM)/60);
                    follower.followPath(middleload, 0.8, false);
                    stateTimer.reset();
                    setPathState(PathState.MIDDLELOAD);
                }
                break;

            case MIDDLELOAD:
                if (!follower.isBusy()){
                    intake.setVelocity(0);
                    follower.followPath(middleloadtravel, false);
                    stateTimer.reset();
                    setPathState(PathState.MIDDLELOADLEAVE);
                }
                break;
            case MIDDLELOADLEAVE:
                if (!follower.isBusy()){
                    follower.followPath(middleloadtravel, false);
                    setPathState(PathState.DRIVE_MIDDLELOADPOS_SHOOTPOS);
                }
            case DRIVE_MIDDLELOADPOS_SHOOTPOS:
                flywheel.setVelocity(FLYWHEEL_RPM);
                if (!follower.isBusy()) {
                    stateTimer.reset();
                    lhoodtilt.setPosition(HOOD_TILT);
                    rhoodtilt.setPosition(HOOD_TILT);
                    setPathState(PathState.SHOOT2);
                }
                break;

            case SHOOT2:
                flywheel.setVelocity(FLYWHEEL_RPM);
                if (!follower.isBusy()){
                    intake.setVelocity((145.1 * INTAKE_SHOOT_RPM)/60);
                    lbstop.setPosition(0);
                    rbstop.setPosition(0);

                    if (stateTimer.milliseconds() > SHOOT_TIME - 1500) {
                        flywheel.setVelocity(0);
                        intake.setVelocity(0);
                        setPathState(PathState.DRIVE_SHOOTPOS_LOAD3POS);
                        follower.followPath(drivetofarload);
                    }
                }
                break;
            case DRIVE_SHOOTPOS_LOAD3POS:
                lhoodtilt.setPosition(0.02);
                rhoodtilt.setPosition(0.02);
                flywheel.setVelocity(-FLYWHEEL_RPM);
                lbstop.setPosition(0.12);
                rbstop.setPosition(0.12);
                if (!follower.isBusy()) {
                    stateTimer.reset();
                }
                setPathState(PathState.FARLOAD);
                break;
            case FARLOAD:
                if (!follower.isBusy()){
                    intake.setVelocity((145.1*INTAKE_RPM)/60);
                    follower.followPath(farload, 0.7, false);
                    setPathState(PathState.DRIVE_FARLOADPOS_SHOOTPOS);
                    stateTimer.reset();
                }
                break;
            case DRIVE_FARLOADPOS_SHOOTPOS:
                if (!follower.isBusy()){

                    flywheel.setVelocity(FLYWHEEL_RPM);
                    lhoodtilt.setPosition(HOOD_TILT);
                    rhoodtilt.setPosition(HOOD_TILT);
                    intake.setVelocity(0);
                    lbstop.setPosition(.15);
                    rbstop.setPosition(.15);

                    follower.followPath(farloadshoot, false);
                    setPathState(PathState.SHOOT3);
                    stateTimer.reset();
                }
                break;
            case SHOOT3:
                if (!follower.isBusy()){
                    intake.setVelocity((145.1 * INTAKE_SHOOT_RPM)/60);
                    lbstop.setPosition(0);
                    rbstop.setPosition(0);

                    if (stateTimer.milliseconds() > SHOOT_TIME+800) {
                        lhoodtilt.setPosition(0.02);
                        rhoodtilt.setPosition(0.02);
                        flywheel.setVelocity(0);
                        intake.setVelocity(0);
                        follower.followPath(end, true);
                        setPathState(PathState.END);
                    }
                }
                break;
            case END:
                if (!follower.isBusy()){
                    prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, solidGreen);
                }
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

        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, solidRed);

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