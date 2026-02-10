package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Base interface for all robot subsystems. Subsystems encapsulate hardware and control logic.
 */
public interface Subsystem {
    /**
     * Initialize the subsystem hardware. Called once when the OpMode is initialized.
     */
    void init(HardwareMap hardwareMap, Telemetry telemetry);

    /**
     * Start any asynchronous tasks or reset state before the OpMode loop begins.
     */
    void start();

    /**
     * Update the subsystem each loop iteration. Non-blocking.
     */
    void update();

    /**
     * Stop the subsystem and release any resources when the OpMode ends.
     */
    void stop();
}
