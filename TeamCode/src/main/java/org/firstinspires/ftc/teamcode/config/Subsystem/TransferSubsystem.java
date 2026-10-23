package org.firstinspires.ftc.teamcode.config.Subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.SubsystemBase;

public class TransferSubsystem extends SubsystemBase {

    private DcMotorEx transfer;

    public TransferSubsystem(HardwareMap h){
        transfer = h.get(DcMotorEx.class,"transfer");
        transfer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
    public void setPower(double p) {
        transfer.setPower(p);
    } // setPositon


}
