package org.firstinspires.ftc.teamcode.opmode.misc;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

@TeleOp
public class motorTest extends OpMode {
    protected GamepadEx driver;
    private DcMotor motor;

    @Override
    public void init(){
        driver = new GamepadEx(gamepad1);
        motor = hardwareMap.get(DcMotor.class, "shi");
        telemetry.addLine("water");
        telemetry.update();
    }

    @Override
    public void loop() {
        if (gamepad1.a){
            motor.setPower(1);
        }
        if (gamepad1.b){
            motor.setPower(0);
        }
        telemetry.addLine("drain");
        telemetry.update();

    }

}
