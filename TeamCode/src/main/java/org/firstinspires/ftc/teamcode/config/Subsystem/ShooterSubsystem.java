package org.firstinspires.ftc.teamcode.config.Subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;


import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShooterSubsystem extends SubsystemBase {

    public double target = 0;
    public DcMotorEx shooter1;

    //@TODO
    // Mithra you got this part

    public ShooterSubsystem(HardwareMap hw, Telemetry t) {
        shooter1 = hw.get(DcMotorEx.class, "s");

        // Orientation for shooter
        shooter1.setDirection(DcMotor.Direction.REVERSE);

    } // init

    public void setPower(double p) {
        shooter1.setPower(p);
    }
    public double getTarget(){
        return target;
    }

}// end of shooter class
