package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Configuration;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

public class HuskyLensSubsystem implements Subsystem {
    private HuskyLens huskyLens;
    private Telemetry telemetry;
    private boolean connected;
    private HuskyLens.Block[] latestBlocks = new HuskyLens.Block[0];

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        try {
            huskyLens = hardwareMap.get(HuskyLens.class, Configuration.HUSKYLENS);
            connected = huskyLens.knock();
            if (connected) {
                // Tag recognition is the selected algorithm for later red/blue goal selection by tag ID.
                huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);
            }
        } catch (IllegalArgumentException ignored) {
            huskyLens = null;
            connected = false;
        }
    }

    @Override
    public void start() {
        // No warm-up required.
    }

    @Override
    public void update() {
        if (huskyLens == null || !connected) {
            latestBlocks = new HuskyLens.Block[0];
            return;
        }

        latestBlocks = huskyLens.blocks();
        // TODO: Parse block IDs to differentiate red vs blue goals and pick target priorities.
    }

    @Override
    public void stop() {
        if (telemetry != null) {
            telemetry.addData("HuskyLens", "Stopped");
        }
    }

    public boolean hasDetections() {
        return latestBlocks.length > 0;
    }

    public HuskyLens.Block[] getLatestBlocks() {
        return latestBlocks;
    }

    public String getStatus() {
        if (huskyLens == null) {
            return "Not mapped";
        }
        if (!connected) {
            return "No response";
        }
        return hasDetections() ? "Detections: " + latestBlocks.length : "No detections";
    }
}
