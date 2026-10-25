package org.firstinspires.ftc.teamcode.config.Subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDFController;

public class ShooterSubsystem extends SubsystemBase {

    // Motor RPM; matches the 28 ticks/revolution used in rpmIncra.
    private static final double TICKS_PER_REV = 28.0;
    private double target;
    private final DcMotorEx shooter1;
    // Tune these gains for RPM feedback and a normalized power output.
    private final PIDFController pidf = new PIDFController(0.4, 0.000, 0.004, 0.002);
    private boolean velocityControl = true;
    public ShooterSubsystem(HardwareMap hw) {
        shooter1 = hw.get(DcMotorEx.class, "s");

        // Orientation for shooter
        shooter1.setDirection(DcMotor.Direction.REVERSE);
        shooter1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    }

    public void setPower(double power) {
        velocityControl = false;
        target = 0;
        pidf.reset();
        shooter1.setPower(power);
    }

    public void setVelocity(double rpm) {
        if (!Double.isFinite(rpm) || rpm < 0) {
            throw new IllegalArgumentException("Shooter RPM must be finite and nonnegative");
        }
        if (!velocityControl || target != rpm) {
            pidf.reset();
        }
        target = rpm;
        velocityControl = true;
        if (rpm == 0) {
            shooter1.setPower(0);
        }
    }

    public double getVelocity() {
        return shooter1.getVelocity() * 60.0 / TICKS_PER_REV;
    }

    @Override
    public void periodic() {
        if (!velocityControl) {
            return;
        }
        if (target <= 0) {
            pidf.reset();
            shooter1.setPower(0);
            return;
        }
        double power = pidf.calculate(getVelocity(), target);
        shooter1.setPower(Range.clip(power, 0.0, 1.0));
    }

    public double getTarget() {
        return target;
    }

}
