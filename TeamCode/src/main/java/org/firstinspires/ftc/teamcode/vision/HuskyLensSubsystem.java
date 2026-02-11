package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.config.HardwareConfig;
import org.firstinspires.ftc.teamcode.subsystems.Subsystem;

public class HuskyLensSubsystem implements Subsystem {

    private HuskyLens huskyLens;
    private Telemetry telemetry;

    @Override
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        // HuskyLens is an I2C smart sensor with onboard processing. :contentReference[oaicite:2]{index=2}
        huskyLens = hardwareMap.get(HuskyLens.class, HardwareConfig.HUSKYLENS);

        // TODO: pick the algorithm you want (object recognition, tracking, tag recognition, etc.)
        // Example (names may vary by SDK version):
        // huskyLens.selectAlgorithm(HuskyLens.Algorithm.OBJECT_RECOGNITION);
        //
        // Keep this as a TODO until you confirm the exact enum/method from your SDK sample
        // (look at: FtcRobotController external sample “SensorHuskyLens”).
    }

    @Override public void start() { /* TODO: optional warmup */ }

    @Override
    public void update() {
        // TODO: query blocks/arrows from HuskyLens and publish telemetry
        // Typical flow is: read blocks -> choose best -> output x/y/width/height/id
        // telemetry.addData("HL", "..."); telemetry.update();
    }

    @Override public void stop() { /* nothing required */ }

    // Convenience getters you’ll fill in later:
    // public boolean hasTarget() { ... }
    // public HuskyLens.Block getBestBlock() { ... }
}
