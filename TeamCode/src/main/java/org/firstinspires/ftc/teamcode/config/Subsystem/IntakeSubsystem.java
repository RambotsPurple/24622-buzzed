package org.firstinspires.ftc.teamcode.config.Subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeSubsystem {
    private DcMotorEx intake, solo;

    public IntakeSubsystem(HardwareMap h){
        intake = h.get(DcMotorEx.class,"i");
        solo = h.get(DcMotorEx.class,"solo");
        solo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

//    @todo
//    make method to intake
//    make method for solo roller


}
