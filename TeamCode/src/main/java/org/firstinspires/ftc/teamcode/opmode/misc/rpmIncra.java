package org.firstinspires.ftc.teamcode.opmode.misc;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.opMode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

@TeleOp
public class rpmIncra extends OpMode {
    private DcMotorEx s;
    private boolean toggle;

    public GamepadEx p;
    double targetRPM = 1000;
    double ticksPerRev = 28;
    @Override
    public void init() {
        s =hardwareMap.get(DcMotorEx.class, "s");
        toggle = false;
        s.setDirection(DcMotor.Direction.REVERSE);
        s.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        p = new GamepadEx(gamepad1);
    }

    @Override
    public void loop() {

        if(p.wasJustPressed(GamepadKeys.Button.A)){
            if(!toggle){
                s.setVelocity((targetRPM/60.0)*ticksPerRev);
                toggle = true;
            }else{
                s.setVelocity(0);
                toggle = false;
            }

        }
        if(p.wasJustPressed(GamepadKeys.Button.B)){
            targetRPM +=100;
            if(targetRPM >6000) targetRPM = 100;
            s.setVelocity((targetRPM/60.0)*ticksPerRev);
        }
        telemetry.addData("rpm:",(s.getVelocity()/60)*ticksPerRev);
        telemetry.update();
    }
}
