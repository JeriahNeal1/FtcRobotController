package org.firstinspires.ftc.teamcode.commands;

/**
 * Represents a self-contained action that executes over time and reports when complete.
 */
public interface Command {
    void start();
    void update();
    boolean isFinished();
    void stop();
}
