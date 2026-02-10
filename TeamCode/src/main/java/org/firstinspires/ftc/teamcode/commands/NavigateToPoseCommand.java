package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.localization.Pose2d;
import org.firstinspires.ftc.teamcode.subsystems.PathPlanningSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveSubsystem;
import org.firstinspires.ftc.teamcode.pathplanning.Trajectory;

/**
 * Command to navigate the robot to a target pose using path planning and drive control.
 */
public class NavigateToPoseCommand implements Command {
    private final PathPlanningSubsystem pathPlanner;
    private final DriveSubsystem drive;
    private final Pose2d targetPose;
    private Trajectory trajectory;
    private boolean finished;

    public NavigateToPoseCommand(PathPlanningSubsystem pathPlanner, DriveSubsystem drive, Pose2d targetPose) {
        this.pathPlanner = pathPlanner;
        this.drive = drive;
        this.targetPose = targetPose;
    }

    @Override
    public void start() {
        // Build trajectory from current pose to target pose
        // TODO: get current pose from localization subsystem
        trajectory = pathPlanner.buildTrajectory(null, targetPose);
        finished = false;
    }

    @Override
    public void update() {
        // TODO: follow trajectory using drive subsystem and update finished when done
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void stop() {
        // TODO: stop drive
    }
}
