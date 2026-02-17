package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Flywheeltest", group = "Competition")

public class flywheelTest extends OpMode {
    private final RobotHardwareConfig robot = new RobotHardwareConfig();
    public DcMotor intake, flywheel;
    @Override
    public void init(){
        robot.initIntake(hardwareMap);
        robot.initSingleFlywheel(hardwareMap);

        intake = robot.intake;
        flywheel = robot.flywheel;

        intake.setPower(1);
        flywheel.setPower(0);
    }

    @Override
    public void loop() {

    }
}
