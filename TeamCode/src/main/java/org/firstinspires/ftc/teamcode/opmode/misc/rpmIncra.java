package org.firstinspires.ftc.teamcode.opmode.misc;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.opMode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

@TeleOp
public class rpmIncra extends OpMode {
    private DcMotorEx s;
    private  int velS = 1000;
    @Override
    public void init() {
        s =hardwareMap.get(DcMotorEx.class, "s");
    }

    @Override
    public void loop() {
        if(gamepad1.b){
            s.setVelocity(velS);
        }
        if(gamepad1.a){
            velS +=100;
            s.setVelocity(velS);
        }
    }
}
