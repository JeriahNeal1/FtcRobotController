package org.firstinspires.ftc.teamcode.commands;

import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;

/**
 * Command that runs the intake for a specified duration or until a sensor triggers.
 */
public class IntakeCommand implements Command {
    private final IntakeSubsystem intake;
    private final double durationSeconds;
    private double startTime;

    public IntakeCommand(IntakeSubsystem intake, double durationSeconds) {
        this.intake = intake;
        this.durationSeconds = durationSeconds;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis() / 1000.0;
        // TODO: start intake motor
    }

    @Override
    public void update() {
        // TODO: optionally handle sensor state
    }

    @Override
    public boolean isFinished() {
        return (System.currentTimeMillis() / 1000.0) - startTime >= durationSeconds;
    }

    @Override
    public void stop() {
        // TODO: stop intake motor
    }
}
