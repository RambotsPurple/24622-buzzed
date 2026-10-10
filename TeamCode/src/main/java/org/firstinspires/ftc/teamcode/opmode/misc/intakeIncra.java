package org.firstinspires.ftc.teamcode.opmode.misc;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.opMode;
import static org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit.AMPS;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.gamepad.ButtonReader;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

@TeleOp(name = "intake-trans incra")
public class intakeIncra extends OpMode {
    private DcMotorEx s;

    public GamepadEx p;


    double incra1 =0.0;
    double incra2 =0.0;

    @Override
    public void init() {
        s =hardwareMap.get(DcMotorEx.class, "i");
        s.setDirection(DcMotor.Direction.REVERSE);
        s.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        p = new GamepadEx(gamepad1);

    }

    @Override
    public void loop() {
        p.readButtons();

        if(p.wasJustPressed(GamepadKeys.Button.A)){
            incra1 +=0.10;

        }
        if(p.wasJustPressed(GamepadKeys.Button.B)){
            incra1 =0.0;
        }

        if(p.wasJustPressed(GamepadKeys.Button.X)){
            incra2 +=0.10;

        }
        if(p.wasJustPressed(GamepadKeys.Button.Y)){
            incra2 =0.0;
        }
        s.setPower(incra1);
        telemetry.addData("Power expected",incra1);
        telemetry.addData("actual current",s.getCurrent(AMPS));
        telemetry.update();
    }
}
