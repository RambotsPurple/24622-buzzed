package org.firstinspires.ftc.teamcode.config.Subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
    private DcMotorEx intake;

    public IntakeSubsystem(HardwareMap h){
        intake = h.get(DcMotorEx.class,"intake");
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setDirection(DcMotor.Direction.REVERSE);

    }

//    @todo
//    make method to intake
//    make method for solo roller
public void run(double power){
    intake.setPower(power);
}

    public void stop(){
        intake .setPower(0);
    }


}
