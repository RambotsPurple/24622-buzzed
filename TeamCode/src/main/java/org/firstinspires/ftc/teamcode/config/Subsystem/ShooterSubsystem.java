package org.firstinspires.ftc.teamcode.config.Subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;



import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShooterSubsystem {

    public double target = 0;
    public DcMotorEx shooter1;

    public PIDFCoefficients pidf = new PIDFCoefficients(
            3,   // P
            0.0,   // I
            0.0,   // D
            16.0   // F
    );
    public ShooterSubsystem(HardwareMap hw, Telemetry t) {
        shooter1 = hw.get(DcMotorEx.class, "s1");

        // Orientation for shooter
        shooter1.setDirection(DcMotor.Direction.REVERSE);

        shooter1.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);


    } // init

    public void setPower(double p) {
        shooter1.setPower(p);
    } // shoot
    public double getTarget(){
        return target;
    }
    //Init the Look up table

//Adding each val with a key

    // set RPM of motorv
    public void setRPM(double RPM) {
        shooter1.setVelocity(RPM * 6, AngleUnit.DEGREES);

    }

    public double getRPM() {
        return shooter1.getVelocity(AngleUnit.DEGREES) / 6;
    }

    public double getTPS() {
        return shooter1.getVelocity();
    }

}// end of shooter class
