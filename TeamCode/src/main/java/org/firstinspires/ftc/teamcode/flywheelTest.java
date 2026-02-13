package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Flywheeltest", group = "Competition")

public class flywheelTest extends OpMode {
    public DcMotor intake, flywheel;
    @Override
    public void init(){
        intake = hardwareMap.get(DcMotor.class, "intake");
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        intake.setPower(1);
        flywheel.setPower(0);
    }

    @Override
    public void loop() {

    }
}
