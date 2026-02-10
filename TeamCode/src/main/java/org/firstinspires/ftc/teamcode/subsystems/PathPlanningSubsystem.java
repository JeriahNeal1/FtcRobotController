package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pathplanning.PedroPathPlanner;
import org.firstinspires.ftc.teamcode.pathplanning.Trajectory;
import org.firstinspires.ftc.teamcode.localization.Pose2d;

/**
 * Generates and manages trajectories for driving between points using Pedro Pathing or another planner.
 */
public class PathPlanningSubsystem implements Subsystem {
    private PedroPathPlanner planner;

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        planner = new PedroPathPlanner();
    }

    @Override
    public void start() {
    }

    @Override
    public void update() {
        // Path planning updates if necessary
    }

    @Override
    public void stop() {
    }

    public Trajectory buildTrajectory(Pose2d start, Pose2d end) {
        // TODO: use planner to generate a trajectory
        return null;
    }
}
