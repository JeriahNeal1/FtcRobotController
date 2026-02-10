package org.firstinspires.ftc.teamcode.dashboard;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Wrapper around Telemetry to simplify sending data to the FTC Dashboard.
 */
public class DashboardTelemetry {
    private final Telemetry telemetry;

    public DashboardTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void addData(String caption, Object value) {
        telemetry.addData(caption, value);
    }

    public void update() {
        telemetry.update();
    }
}
