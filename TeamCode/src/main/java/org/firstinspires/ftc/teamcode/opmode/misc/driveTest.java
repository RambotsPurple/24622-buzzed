package org.firstinspires.ftc.teamcode.opmode.misc;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.config.subsystems.DriveSubsystem;
@TeleOp
public class driveTest extends OpMode {
    protected GamepadEx driver;
    private DriveSubsystem d;


    @Override
    public void init() {
        driver = new GamepadEx(gamepad1);
        d = new DriveSubsystem(hardwareMap);
    }

    @Override
    public void loop() {
        d.drive(-driver.getLeftX(), -driver.getLeftY(),driver.getRightX());
        if(driver.getButton(GamepadKeys.Button.B))d.resetAngle();
    }
}

