package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.subsystems.ShooterSubsystem;

/**
 * Command to spin up the shooter and fire a ball.
 */
public class ShootCommand implements Command {
    private final ShooterSubsystem shooter;
    private boolean finished;

    public ShootCommand(ShooterSubsystem shooter) {
        this.shooter = shooter;
    }

    @Override
    public void start() {
        // TODO: spin up shooter
        finished = false;
    }

    @Override
    public void update() {
        // TODO: monitor shooter speed and release ball when ready
        // When done, set finished = true
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void stop() {
        // TODO: stop shooter
    }
}
