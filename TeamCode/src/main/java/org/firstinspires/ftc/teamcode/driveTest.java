package org.firstinspires.ftc.teamcode.opmode.misc;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import org.firstinspires.ftc.teamcode.config.subsystem.DriveSubsystem;
public class ddTest extends OpMode {
    protected GamepadEx driver;
    private DriveSubsystem d;


    @Override
    public void init() {
        driver = new GamepadEx(gamepad1);
        d = new DriveSubsystem(hardwareMap);
    }

    @Override
    public void loop() {
        d.drive(driver.getLeftX(), driver.getLeftY(),driver.getRightX());
    }
}
