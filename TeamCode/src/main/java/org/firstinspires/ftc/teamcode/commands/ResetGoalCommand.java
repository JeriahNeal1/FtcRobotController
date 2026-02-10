package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

/**
 * Command to reset the goal by driving to the lever and triggering it.
 * This command likely requires a combination of drive and mechanism control.
 */
public class ResetGoalCommand implements Command {
    // TODO: add dependencies (drive subsystem, mechanism)

    @Override
    public void start() {
        // TODO: start driving to goal reset location
    }

    @Override
    public void update() {
        // TODO: monitor path progress and actuate reset mechanism
    }

    @Override
    public boolean isFinished() {
        // TODO: determine when reset is complete
        return false;
    }

    @Override
    public void stop() {
        // TODO: stop all motion
    }
}
