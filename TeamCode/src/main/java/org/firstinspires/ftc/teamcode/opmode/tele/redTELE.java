package org.firstinspires.ftc.teamcode.opmode.tele;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.config.Robot;
import org.firstinspires.ftc.teamcode.config.util.Alliance;
@TeleOp(name = "red")
public class redTELE extends OpMode {
    Robot robot;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, Alliance.BLUE, gamepad1,gamepad2, telemetry);
        robot.tele();
    }

    @Override
    public void init_loop() {
        telemetry.addData("current alliance selected:", robot.getAllaince());
        telemetry.update();
    }
//
    @Override
    public void start(){
        robot.tStart();
    }

    @Override
    public void loop() {
        robot.tPeriodic();
    }
}
