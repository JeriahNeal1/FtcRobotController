package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.RobotHardwareConfig;
import org.firstinspires.ftc.teamcode.ShooterConfig;

/**
 * Integrated shooter stack with turret, hood, flywheel, Limelight, Pinpoint, and ballistics.
 * <p>
 * This class is the single public entry point intended for OpModes.
 */
public class ShooterSubsystem {
    public enum ControlMode {
        MANUAL,
        AUTO_AIM
    }

    public enum ShootingState {
        IDLE,
        TRACKING,
        SWEEPING,
        MANUAL,
        READY,
        SHOOTING
    }

    /**
     * Unified output from the ballistics pipeline.
     */
    public static final class ShooterSolution {
        public double turretTargetRad;
        public double hoodTargetAngleRad;
        public double flywheelTargetRpm;
        public double timeOfFlightSec;
        public boolean isValid;

        // Debug values for tuning and verification.
        public double rangeMeters;
        public double exitVelocityMps;
        public double leadRad;
        public double txDeg;
        public double tyDeg;
        public double legacyMappedRpm;
        public double legacyMappedHoodAngleRad;
        public double ballisticHoodAngleRad;
        public double vxRobotMps;
        public double vyRobotMps;

        void invalidate() {
            isValid = false;
            timeOfFlightSec = 0.0;
            rangeMeters = 0.0;
            exitVelocityMps = 0.0;
            leadRad = 0.0;
        }
    }

    /**
     * Lightweight telemetry cache. Updated in-place to avoid per-loop allocations.
     */
    public static final class TelemetrySnapshot {
        public ShootingState state = ShootingState.IDLE;
        public ControlMode controlMode = ControlMode.AUTO_AIM;
        public boolean sweepEnabled;
        public boolean visionValid;
        public boolean readyToShoot;
        public boolean shootRequested;
        public boolean solutionValid;
        public boolean verboseEnabled;

        public double turretCurrentRad;
        public double turretTargetRad;
        public double turretErrorRad;
        public double turretPower;

        public double hoodTargetAngleRad;
        public double hoodServoCommand;

        public double flywheelTargetRpm;
        public double flywheelAppliedRpm;
        public double flywheelMeasuredRpm;

        public double txDeg;
        public double tyDeg;
        public double ta;
        public double rangeMeters;
        public double tofSec;
        public double leadRad;

        public double odoVxFieldMps;
        public double odoVyFieldMps;
        public double odoHeadingRad;
    }

    private final TurretModule turretModule = new TurretModule();
    private final HoodModule hoodModule = new HoodModule();
    private final FlywheelModule flywheelModule = new FlywheelModule();
    private final VisionModule visionModule = new VisionModule();
    private final OdometryModule odometryModule = new OdometryModule();
    private final BallisticsModule ballisticsModule = new BallisticsModule();
    private final ShootingStateMachine stateMachine = new ShootingStateMachine();
    private final ShooterSolution solution = new ShooterSolution();
    private final TelemetrySnapshot telemetrySnapshot = new TelemetrySnapshot();

    private RobotHardwareConfig robotHardware = new RobotHardwareConfig();
    private ControlMode requestedMode = ShooterConfig.AUTO_AIM_ENABLED_DEFAULT ? ControlMode.AUTO_AIM : ControlMode.MANUAL;

    private boolean sweepEnabled;
    private boolean shootRequested;
    private boolean initialized;

    private double manualTurretInput;
    private double manualHoodInput;
    private double manualFlywheelInput;

    private double manualTurretTargetRad = ShooterConfig.MANUAL_DEFAULT_TURRET_ANGLE_RAD;
    private double manualHoodTargetRad = ShooterConfig.MANUAL_DEFAULT_HOOD_ANGLE_RAD;
    private double manualFlywheelTargetRpm = ShooterConfig.MANUAL_DEFAULT_FLYWHEEL_RPM;

    private long lastUpdateNanos;

    public ShooterSubsystem() {}

    public ShooterSubsystem(RobotHardwareConfig hardwareConfig) {
        this.robotHardware = hardwareConfig;
    }

    public void init(HardwareMap hardwareMap, RobotHardwareConfig hardwareConfig, int limelightPipeline) {
        this.robotHardware = hardwareConfig;
        init(hardwareMap, limelightPipeline);
    }

    public void init(HardwareMap hardwareMap, int limelightPipeline) {
        ShooterConfig.refreshDerivedConstantsIfNeeded();

        turretModule.init(hardwareMap);
        hoodModule.init(hardwareMap);
        flywheelModule.init(hardwareMap);
        visionModule.init(hardwareMap, limelightPipeline);
        odometryModule.init(hardwareMap);

        manualTurretTargetRad = clampTurret(manualTurretTargetRad);
        manualHoodTargetRad = clampHood(manualHoodTargetRad);
        manualFlywheelTargetRpm = clampFlywheel(manualFlywheelTargetRpm);

        sweepEnabled = false;
        shootRequested = false;
        initialized = true;
        lastUpdateNanos = 0L;
    }

    public void update() {
        if (!initialized) {
            return;
        }

        ShooterConfig.refreshDerivedConstantsIfNeeded();

        long nowNanos = System.nanoTime();
        double dtSec = 0.02;
        if (lastUpdateNanos != 0L) {
            dtSec = (nowNanos - lastUpdateNanos) * 1e-9;
            dtSec = ShooterConfig.clamp(dtSec, 0.001, 0.1);
        }
        lastUpdateNanos = nowNanos;

        visionModule.update();
        odometryModule.update();
        turretModule.updateSensors();
        flywheelModule.updateSensors();

        boolean manualInputActive = isManualInputActive();
        if (ShooterConfig.MANUAL_INPUT_FORCES_MANUAL && manualInputActive) {
            requestedMode = ControlMode.MANUAL;
        }

        if (requestedMode == ControlMode.MANUAL) {
            updateManualTargets(dtSec);
            solution.invalidate();
            turretModule.setTargetRad(manualTurretTargetRad);
            hoodModule.setTargetAngleRad(manualHoodTargetRad);
            flywheelModule.setTargetRpm(manualFlywheelTargetRpm);
        } else {
            if (visionModule.targetValid) {
                ballisticsModule.solve(solution);
                if (solution.isValid) {
                    turretModule.setTargetRad(solution.turretTargetRad);
                    hoodModule.setTargetAngleRad(solution.hoodTargetAngleRad);
                    flywheelModule.setTargetRpm(solution.flywheelTargetRpm);
                }
            } else {
                solution.invalidate();
                if (!sweepEnabled) {
                    updateManualTargets(dtSec);
                    turretModule.setTargetRad(manualTurretTargetRad);
                    hoodModule.setTargetAngleRad(manualHoodTargetRad);
                    flywheelModule.setTargetRpm(manualFlywheelTargetRpm);
                }
            }
        }

        boolean currentlySweeping = requestedMode == ControlMode.AUTO_AIM
                && !visionModule.targetValid
                && sweepEnabled;

        if (currentlySweeping) {
            turretModule.updateSweep(dtSec);
        } else {
            turretModule.updateClosedLoop(dtSec);
        }

        hoodModule.updateClosedLoop(dtSec);
        flywheelModule.updateClosedLoop(dtSec);

        boolean ready = solution.isValid
                && turretModule.atTarget()
                && hoodModule.atTarget()
                && flywheelModule.atTarget();

        stateMachine.update(
                requestedMode,
                visionModule.targetValid,
                solution.isValid,
                sweepEnabled,
                ready,
                shootRequested,
                manualInputActive
        );

        refreshTelemetrySnapshot(ready);
    }

    public void setManualMode() {
        requestedMode = ControlMode.MANUAL;
    }

    public void setAutoAimMode() {
        requestedMode = ControlMode.AUTO_AIM;
    }

    public void toggleSweepMode() {
        sweepEnabled = !sweepEnabled;
    }

    public void setManualTurretInput(double input) {
        manualTurretInput = input;
    }

    public void setManualHoodInput(double input) {
        manualHoodInput = input;
    }

    public void setManualFlywheelInput(double input) {
        manualFlywheelInput = input;
    }

    public void setManualTurretTargetRad(double turretTargetRad) {
        manualTurretTargetRad = clampTurret(turretTargetRad);
    }

    public void setManualHoodTargetAngleRad(double hoodTargetAngleRad) {
        manualHoodTargetRad = clampHood(hoodTargetAngleRad);
    }

    public void setManualHoodTargetServo(double hoodServoPosition) {
        setManualHoodTargetAngleRad(ShooterConfig.hoodServoPositionToAngleRad(hoodServoPosition));
    }

    public void setManualFlywheelTargetRpm(double flywheelTargetRpm) {
        manualFlywheelTargetRpm = clampFlywheel(flywheelTargetRpm);
    }

    public void requestShoot(boolean requested) {
        shootRequested = requested;
    }

    public boolean isReadyToShoot() {
        return stateMachine.state == ShootingState.READY && solution.isValid;
    }

    public TelemetrySnapshot getTelemetrySnapshot() {
        return telemetrySnapshot;
    }

    public void stop() {
        turretModule.stop();
        flywheelModule.stop();
    }

    private boolean isManualInputActive() {
        return Math.abs(manualTurretInput) > ShooterConfig.MANUAL_OVERRIDE_THRESHOLD
                || Math.abs(manualHoodInput) > ShooterConfig.MANUAL_OVERRIDE_THRESHOLD
                || Math.abs(manualFlywheelInput) > ShooterConfig.MANUAL_OVERRIDE_THRESHOLD;
    }

    private void updateManualTargets(double dtSec) {
        if (Math.abs(manualTurretInput) > ShooterConfig.TURRET_INPUT_DEADBAND) {
            manualTurretTargetRad += manualTurretInput * ShooterConfig.TURRET_MANUAL_RATE_RAD_PER_SEC * dtSec;
            manualTurretTargetRad = clampTurret(manualTurretTargetRad);
        }

        if (Math.abs(manualHoodInput) > ShooterConfig.TURRET_INPUT_DEADBAND) {
            manualHoodTargetRad += manualHoodInput * ShooterConfig.HOOD_MANUAL_RATE_RAD_PER_SEC * dtSec;
            manualHoodTargetRad = clampHood(manualHoodTargetRad);
        }

        if (Math.abs(manualFlywheelInput) > ShooterConfig.TURRET_INPUT_DEADBAND) {
            manualFlywheelTargetRpm += manualFlywheelInput * ShooterConfig.FLYWHEEL_MANUAL_DELTA_RPM_PER_SEC * dtSec;
            manualFlywheelTargetRpm = clampFlywheel(manualFlywheelTargetRpm);
        }
    }

    private void refreshTelemetrySnapshot(boolean ready) {
        telemetrySnapshot.state = stateMachine.state;
        telemetrySnapshot.controlMode = requestedMode;
        telemetrySnapshot.sweepEnabled = sweepEnabled;
        telemetrySnapshot.visionValid = visionModule.targetValid;
        telemetrySnapshot.readyToShoot = ready;
        telemetrySnapshot.shootRequested = shootRequested;
        telemetrySnapshot.solutionValid = solution.isValid;
        telemetrySnapshot.verboseEnabled = ShooterConfig.SHOOTER_VERBOSE_TELEMETRY;

        telemetrySnapshot.turretCurrentRad = turretModule.currentRad;
        telemetrySnapshot.turretTargetRad = turretModule.targetRad;
        telemetrySnapshot.turretErrorRad = turretModule.errorRad;
        telemetrySnapshot.turretPower = turretModule.motorPower;

        telemetrySnapshot.hoodTargetAngleRad = hoodModule.targetAngleRad;
        telemetrySnapshot.hoodServoCommand = hoodModule.lastServoCommand;

        telemetrySnapshot.flywheelTargetRpm = flywheelModule.targetRpm;
        telemetrySnapshot.flywheelAppliedRpm = flywheelModule.appliedRpm;
        telemetrySnapshot.flywheelMeasuredRpm = flywheelModule.measuredRpm;

        telemetrySnapshot.txDeg = visionModule.filteredTxDeg;
        telemetrySnapshot.tyDeg = visionModule.filteredTyDeg;
        telemetrySnapshot.ta = visionModule.filteredTa;

        telemetrySnapshot.rangeMeters = solution.rangeMeters;
        telemetrySnapshot.tofSec = solution.timeOfFlightSec;
        telemetrySnapshot.leadRad = solution.leadRad;

        telemetrySnapshot.odoVxFieldMps = odometryModule.vxFieldMps;
        telemetrySnapshot.odoVyFieldMps = odometryModule.vyFieldMps;
        telemetrySnapshot.odoHeadingRad = odometryModule.headingRad;
    }

    private static double clampTurret(double angleRad) {
        return ShooterConfig.clamp(angleRad, ShooterConfig.TURRET_MIN_ANGLE_RAD, ShooterConfig.TURRET_MAX_ANGLE_RAD);
    }

    private static double clampHood(double angleRad) {
        return ShooterConfig.clamp(angleRad, ShooterConfig.HOOD_ANGLE_MIN_RAD, ShooterConfig.HOOD_ANGLE_MAX_RAD);
    }

    private static double clampFlywheel(double rpm) {
        return ShooterConfig.clamp(rpm, ShooterConfig.FLYWHEEL_MIN_RPM, ShooterConfig.FLYWHEEL_MAX_RPM);
    }

    private final class TurretModule {
        private DcMotorEx motor;

        private double targetRad;
        private double currentRad;
        private double errorRad;
        private double previousErrorRad;
        private double motorPower;

        private boolean sweepForward = true;
        private double sweepPauseRemainingSec;

        void init(HardwareMap hardwareMap) {
            robotHardware.initTurret(hardwareMap, true);
            motor = robotHardware.turretMotor;
            targetRad = clampTurret(ShooterConfig.MANUAL_DEFAULT_TURRET_ANGLE_RAD);
            previousErrorRad = 0.0;
            sweepPauseRemainingSec = 0.0;
        }

        void updateSensors() {
            if (motor == null) {
                return;
            }
            currentRad = ShooterConfig.turretTicksToRadians(motor.getCurrentPosition());
        }

        void setTargetRad(double targetRad) {
            this.targetRad = clampTurret(targetRad);
        }

        void updateClosedLoop(double dtSec) {
            if (motor == null) {
                return;
            }
            errorRad = computeShortestSafeError(targetRad, currentRad);
            double derivative = (errorRad - previousErrorRad) / Math.max(1e-6, dtSec);
            previousErrorRad = errorRad;

            double command = ShooterConfig.TURRET_KP * errorRad + ShooterConfig.TURRET_KD * derivative;
            if (Math.abs(errorRad) < ShooterConfig.TURRET_INPUT_DEADBAND) {
                command = 0.0;
            }
            motorPower = ShooterConfig.clamp(command, -ShooterConfig.TURRET_MAX_POWER, ShooterConfig.TURRET_MAX_POWER);
            motor.setPower(motorPower);
        }

        void updateSweep(double dtSec) {
            double sweepLeft = clampTurret(Math.min(ShooterConfig.TURRET_SWEEP_LEFT_RAD, ShooterConfig.TURRET_SWEEP_RIGHT_RAD));
            double sweepRight = clampTurret(Math.max(ShooterConfig.TURRET_SWEEP_LEFT_RAD, ShooterConfig.TURRET_SWEEP_RIGHT_RAD));

            if (sweepPauseRemainingSec > 0.0) {
                sweepPauseRemainingSec -= dtSec;
            } else {
                double delta = ShooterConfig.TURRET_SWEEP_RATE_RAD_PER_SEC * dtSec;
                targetRad += sweepForward ? delta : -delta;

                if (targetRad >= sweepRight - ShooterConfig.TURRET_SWEEP_ENDPOINT_EPS_RAD) {
                    targetRad = sweepRight;
                    sweepForward = false;
                    sweepPauseRemainingSec = ShooterConfig.TURRET_SWEEP_PAUSE_SEC;
                } else if (targetRad <= sweepLeft + ShooterConfig.TURRET_SWEEP_ENDPOINT_EPS_RAD) {
                    targetRad = sweepLeft;
                    sweepForward = true;
                    sweepPauseRemainingSec = ShooterConfig.TURRET_SWEEP_PAUSE_SEC;
                }
            }
            targetRad = clampTurret(targetRad);
            updateClosedLoop(dtSec);
        }

        boolean atTarget() {
            return Math.abs(errorRad) <= ShooterConfig.TURRET_AIM_TOLERANCE_RAD;
        }

        void stop() {
            if (motor != null) {
                motor.setPower(0.0);
            }
        }

        private double computeShortestSafeError(double targetAngle, double currentAngle) {
            double directDiff = targetAngle - currentAngle;
            double wrappedDiff = ShooterConfig.wrapAngleRad(directDiff);

            double predictedAngle = currentAngle + wrappedDiff;
            if (predictedAngle < ShooterConfig.TURRET_MIN_ANGLE_RAD || predictedAngle > ShooterConfig.TURRET_MAX_ANGLE_RAD) {
                wrappedDiff = directDiff;
            }

            if (currentAngle >= ShooterConfig.TURRET_MAX_ANGLE_RAD && wrappedDiff > 0.0) {
                wrappedDiff = 0.0;
            }
            if (currentAngle <= ShooterConfig.TURRET_MIN_ANGLE_RAD && wrappedDiff < 0.0) {
                wrappedDiff = 0.0;
            }

            return wrappedDiff;
        }
    }

    private final class HoodModule {
        private Servo leftServo;
        private Servo rightServo;
        private double targetAngleRad = ShooterConfig.MANUAL_DEFAULT_HOOD_ANGLE_RAD;
        private double lastServoCommand = ShooterConfig.SHOOTER_HOOD_DOWN_SERVO;

        void init(HardwareMap hardwareMap) {
            robotHardware.initHoodServos(hardwareMap);
            leftServo = robotHardware.lhoodtilt;
            rightServo = robotHardware.rhoodtilt;
            setTargetAngleRad(targetAngleRad);
            updateClosedLoop(0.0);
        }

        void setTargetAngleRad(double targetAngleRad) {
            this.targetAngleRad = clampHood(targetAngleRad);
        }

        void updateClosedLoop(double dtSec) {
            double servoPosition = ShooterConfig.hoodAngleRadToServoPosition(targetAngleRad);
            lastServoCommand = servoPosition;
            if (leftServo != null) {
                leftServo.setPosition(servoPosition);
            }
            if (rightServo != null) {
                rightServo.setPosition(servoPosition);
            }
        }

        boolean atTarget() {
            return true;
        }
    }

    private final class FlywheelModule {
        private DcMotorEx topMotor;
        private DcMotorEx bottomMotor;
        private final PIDFCoefficients pidfCoefficients = new PIDFCoefficients(0.0, 0.0, 0.0, 0.0);

        private double targetRpm;
        private double appliedRpm;
        private double measuredRpm;

        private double lastP = Double.NaN;
        private double lastI = Double.NaN;
        private double lastD = Double.NaN;
        private double lastF = Double.NaN;

        void init(HardwareMap hardwareMap) {
            robotHardware.initTopBottomFlywheels(hardwareMap, true);
            topMotor = robotHardware.topFlywheel;
            bottomMotor = robotHardware.bottomFlywheel;
            targetRpm = 0.0;
            appliedRpm = 0.0;
            measuredRpm = 0.0;
            updatePidfIfNeeded(0.0);
        }

        void setTargetRpm(double targetRpm) {
            this.targetRpm = clampFlywheel(targetRpm);
        }

        void updateSensors() {
            if (topMotor == null || bottomMotor == null) {
                measuredRpm = 0.0;
                return;
            }
            double avgTicksPerSecond = (topMotor.getVelocity() + bottomMotor.getVelocity()) * 0.5;
            measuredRpm = ShooterConfig.ticksPerSecondToFlywheelRpm(avgTicksPerSecond);
        }

        void updateClosedLoop(double dtSec) {
            if (topMotor == null || bottomMotor == null) {
                return;
            }

            double maxDelta = ShooterConfig.FLYWHEEL_RAMP_RPM_PER_SEC * Math.max(0.0, dtSec);
            if (appliedRpm < targetRpm) {
                appliedRpm = Math.min(appliedRpm + maxDelta, targetRpm);
            } else if (appliedRpm > targetRpm) {
                appliedRpm = Math.max(appliedRpm - maxDelta, targetRpm);
            }

            double f = ShooterConfig.FLYWHEEL_KF_PER_RPM * appliedRpm;
            updatePidfIfNeeded(f);

            double targetTicksPerSecond = ShooterConfig.flywheelRpmToTicksPerSecond(appliedRpm);
            topMotor.setVelocity(targetTicksPerSecond);
            bottomMotor.setVelocity(targetTicksPerSecond);
        }

        boolean atTarget() {
            return Math.abs(targetRpm - measuredRpm) <= ShooterConfig.FLYWHEEL_AIM_TOLERANCE_RPM;
        }

        void stop() {
            if (topMotor != null) {
                topMotor.setPower(0.0);
            }
            if (bottomMotor != null) {
                bottomMotor.setPower(0.0);
            }
        }

        private void updatePidfIfNeeded(double fTerm) {
            if (topMotor == null || bottomMotor == null) {
                return;
            }

            double p = ShooterConfig.FLYWHEEL_KP;
            double i = ShooterConfig.FLYWHEEL_KI;
            double d = ShooterConfig.FLYWHEEL_KD;

            if (p == lastP && i == lastI && d == lastD && fTerm == lastF) {
                return;
            }

            pidfCoefficients.p = p;
            pidfCoefficients.i = i;
            pidfCoefficients.d = d;
            pidfCoefficients.f = fTerm;

            topMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
            bottomMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);

            lastP = p;
            lastI = i;
            lastD = d;
            lastF = fTerm;
        }
    }

    private final class VisionModule {
        private Limelight3A limelight;

        private boolean targetValid;
        private double filteredTxDeg;
        private double filteredTyDeg;
        private double filteredTa;
        private long lastValidTimestampMs;
        private boolean initializedFilter;

        void init(HardwareMap hardwareMap, int pipeline) {
            robotHardware.initLimelight(hardwareMap, pipeline);
            limelight = robotHardware.limelight;
            if (limelight != null) {
                limelight.setPollRateHz(ShooterConfig.LIMELIGHT_POLL_RATE_HZ);
                limelight.start();
            }
            targetValid = false;
            initializedFilter = false;
            lastValidTimestampMs = 0L;
        }

        void update() {
            targetValid = false;
            if (limelight == null) {
                return;
            }

            LLResult result = limelight.getLatestResult();
            if (result == null || !result.isValid()) {
                return;
            }

            double rawTx = result.getTx();
            double rawTy = result.getTy();
            double rawTa = result.getTa();

            boolean passesThresholds =
                    Math.abs(rawTx) <= ShooterConfig.LIMELIGHT_MAX_TX_ABS_DEG
                            && Math.abs(rawTy) <= ShooterConfig.LIMELIGHT_MAX_TY_ABS_DEG
                            && rawTa >= ShooterConfig.LIMELIGHT_MIN_TA;

            if (!passesThresholds) {
                return;
            }

            if (!initializedFilter || !ShooterConfig.LIMELIGHT_ENABLE_FILTERING) {
                filteredTxDeg = rawTx;
                filteredTyDeg = rawTy;
                filteredTa = rawTa;
                initializedFilter = true;
            } else {
                filteredTxDeg = lowPass(filteredTxDeg, rawTx, ShooterConfig.LIMELIGHT_TX_LOW_PASS_ALPHA);
                filteredTyDeg = lowPass(filteredTyDeg, rawTy, ShooterConfig.LIMELIGHT_TY_LOW_PASS_ALPHA);
                filteredTa = lowPass(filteredTa, rawTa, ShooterConfig.LIMELIGHT_TA_LOW_PASS_ALPHA);
            }

            targetValid = true;
            lastValidTimestampMs = System.currentTimeMillis();
        }
    }

    private final class OdometryModule {
        private GoBildaPinpointDriver odometry;

        private double vxFieldMps;
        private double vyFieldMps;
        private double headingRad;
        private boolean initializedFilter;

        void init(HardwareMap hardwareMap) {
            robotHardware.initOdometry(hardwareMap);
            odometry = robotHardware.odo;
            vxFieldMps = 0.0;
            vyFieldMps = 0.0;
            headingRad = 0.0;
            initializedFilter = false;
        }

        void update() {
            if (odometry == null) {
                return;
            }

            odometry.update();

            double rawVxMps = odometry.getVelX(DistanceUnit.MM) / 1000.0;
            double rawVyMps = odometry.getVelY(DistanceUnit.MM) / 1000.0;
            double rawHeading = odometry.getHeading(AngleUnit.RADIANS);

            if (!initializedFilter || !ShooterConfig.ODOMETRY_ENABLE_VELOCITY_FILTERING) {
                vxFieldMps = rawVxMps;
                vyFieldMps = rawVyMps;
                headingRad = rawHeading;
                initializedFilter = true;
            } else {
                vxFieldMps = lowPass(vxFieldMps, rawVxMps, ShooterConfig.ODOMETRY_VEL_LOW_PASS_ALPHA);
                vyFieldMps = lowPass(vyFieldMps, rawVyMps, ShooterConfig.ODOMETRY_VEL_LOW_PASS_ALPHA);
                headingRad = lowPass(headingRad, rawHeading, ShooterConfig.ODOMETRY_HEADING_LOW_PASS_ALPHA);
            }
        }
    }

    private final class BallisticsModule {
        void solve(ShooterSolution out) {
            out.invalidate();
            out.txDeg = visionModule.filteredTxDeg;
            out.tyDeg = visionModule.filteredTyDeg;

            double txRad = Math.toRadians(visionModule.filteredTxDeg);
            double tyRad = Math.toRadians(visionModule.filteredTyDeg);

            double cameraPitchRad = ShooterConfig.BALLISTIC_CAMERA_PITCH_RAD + tyRad;
            double tanPitch = Math.tan(cameraPitchRad);
            if (Math.abs(tanPitch) < 1e-6) {
                return;
            }

            double cameraToTargetHeight = ShooterConfig.BALLISTIC_TARGET_HEIGHT_M - ShooterConfig.BALLISTIC_CAMERA_HEIGHT_M;
            double cameraRangeMeters = cameraToTargetHeight / tanPitch;
            if (cameraRangeMeters <= 0.0) {
                return;
            }

            double xCamera = cameraRangeMeters * Math.cos(txRad);
            double yCamera = cameraRangeMeters * Math.sin(txRad);

            double xShooter = xCamera - ShooterConfig.BALLISTIC_CAMERA_TO_SHOOTER_FORWARD_M;
            double yShooter = yCamera - ShooterConfig.BALLISTIC_CAMERA_TO_SHOOTER_LATERAL_M;
            double horizontalRangeMeters = Math.hypot(xShooter, yShooter);
            out.rangeMeters = horizontalRangeMeters;

            if (horizontalRangeMeters < ShooterConfig.BALLISTIC_MIN_RANGE_M
                    || horizontalRangeMeters > ShooterConfig.BALLISTIC_MAX_RANGE_M) {
                return;
            }

            double distanceInches = ShooterConfig.metersToInches(horizontalRangeMeters);
            double mappedRpm = ShooterConfig.estimateLegacyShooterRpm(distanceInches);
            double mappedHoodAngle = ShooterConfig.estimateLegacyHoodAngleRad(distanceInches);
            out.legacyMappedRpm = mappedRpm;
            out.legacyMappedHoodAngleRad = mappedHoodAngle;

            double flywheelTargetRpm = clampFlywheel(mappedRpm);
            double exitVelocityMps = (flywheelTargetRpm * ShooterConfig.BALLISTIC_WHEEL_RADIUS_M * ShooterConfig.TWO_PI / 60.0)
                    * ShooterConfig.BALLISTIC_SLIP_FACTOR;
            exitVelocityMps += ShooterConfig.evaluateExitVelocityCorrection(horizontalRangeMeters);
            out.exitVelocityMps = exitVelocityMps;
            if (exitVelocityMps <= 0.01) {
                return;
            }

            double shooterLaunchHeight = ShooterConfig.BALLISTIC_CAMERA_HEIGHT_M + ShooterConfig.BALLISTIC_CAMERA_TO_SHOOTER_VERTICAL_M;
            double targetHeightDelta = ShooterConfig.BALLISTIC_TARGET_HEIGHT_M - shooterLaunchHeight;
            double g = ShooterConfig.BALLISTIC_GRAVITY_MPS2;
            double v2 = exitVelocityMps * exitVelocityMps;
            double discriminant = (v2 * v2) - g * (g * horizontalRangeMeters * horizontalRangeMeters + 2.0 * targetHeightDelta * v2);
            if (discriminant < 0.0) {
                return;
            }

            double sqrtDiscriminant = Math.sqrt(discriminant);
            double denominator = g * horizontalRangeMeters;
            if (Math.abs(denominator) < 1e-6) {
                return;
            }

            double tanLow = (v2 - sqrtDiscriminant) / denominator;
            double tanHigh = (v2 + sqrtDiscriminant) / denominator;
            double selectedTan = ShooterConfig.BALLISTIC_PREFER_HIGH_ARC ? tanHigh : tanLow;
            double ballisticHoodRad = Math.atan(selectedTan);
            out.ballisticHoodAngleRad = ballisticHoodRad;

            if (!Double.isFinite(ballisticHoodRad)) {
                return;
            }

            double cosLaunch = Math.cos(ballisticHoodRad);
            if (Math.abs(cosLaunch) < 1e-6) {
                return;
            }

            double tof = horizontalRangeMeters / (exitVelocityMps * cosLaunch);
            if (!Double.isFinite(tof) || tof <= 0.0) {
                return;
            }
            out.timeOfFlightSec = tof;

            double cosHeading = Math.cos(odometryModule.headingRad);
            double sinHeading = Math.sin(odometryModule.headingRad);
            double vxRobot = odometryModule.vxFieldMps * cosHeading + odometryModule.vyFieldMps * sinHeading;
            double vyRobot = -odometryModule.vxFieldMps * sinHeading + odometryModule.vyFieldMps * cosHeading;
            out.vxRobotMps = vxRobot;
            out.vyRobotMps = vyRobot;

            double leadRad = 0.0;
            if (ShooterConfig.BALLISTIC_ENABLE_MOTION_COMP) {
                double lateralLeadMeters = vyRobot * tof * ShooterConfig.BALLISTIC_LATERAL_LEAD_COEF;
                leadRad = ShooterConfig.BALLISTIC_TURRET_LEAD_SIGN
                        * Math.atan2(lateralLeadMeters, Math.max(1e-6, horizontalRangeMeters));
            }
            out.leadRad = leadRad;

            double hoodTarget = ShooterConfig.BALLISTIC_HOOD_BLEND * ballisticHoodRad
                    + (1.0 - ShooterConfig.BALLISTIC_HOOD_BLEND) * mappedHoodAngle;

            if (ShooterConfig.BALLISTIC_ENABLE_FORWARD_PITCH_COMP) {
                double pitchComp = ShooterConfig.BALLISTIC_FORWARD_PITCH_COEF
                        * (vxRobot * tof / Math.max(1e-6, horizontalRangeMeters));
                hoodTarget += pitchComp;
            }

            double turretTarget = turretModule.currentRad - txRad + leadRad;
            turretTarget = clampTurret(turretTarget);

            if (hoodTarget < ShooterConfig.HOOD_ANGLE_MIN_RAD || hoodTarget > ShooterConfig.HOOD_ANGLE_MAX_RAD) {
                return;
            }

            out.turretTargetRad = turretTarget;
            out.hoodTargetAngleRad = hoodTarget;
            out.flywheelTargetRpm = flywheelTargetRpm;
            out.isValid = true;
        }
    }

    private final class ShootingStateMachine {
        private ShootingState state = ShootingState.IDLE;

        void update(ControlMode mode,
                    boolean visionValid,
                    boolean solutionValid,
                    boolean sweepEnabledNow,
                    boolean ready,
                    boolean shootRequestedNow,
                    boolean manualInputActive) {
            // Deterministic transition priority:
            // 1) Manual mode always wins.
            // 2) Valid tracked solution gates READY/SHOOTING.
            // 3) AUTO + no vision -> SWEEPING if enabled, otherwise MANUAL fallback.
            // 4) Otherwise IDLE.
            if (mode == ControlMode.MANUAL || manualInputActive) {
                state = ShootingState.MANUAL;
                return;
            }

            if (visionValid) {
                if (solutionValid && ready) {
                    state = shootRequestedNow ? ShootingState.SHOOTING : ShootingState.READY;
                } else {
                    state = ShootingState.TRACKING;
                }
                return;
            }

            if (sweepEnabledNow) {
                state = ShootingState.SWEEPING;
                return;
            }

            state = ShootingState.IDLE;
        }
    }

    private static double lowPass(double previous, double input, double alpha) {
        double clampedAlpha = ShooterConfig.clamp(alpha, 0.0, 1.0);
        return previous + clampedAlpha * (input - previous);
    }
}
