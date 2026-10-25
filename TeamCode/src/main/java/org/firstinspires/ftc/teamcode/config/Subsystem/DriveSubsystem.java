package org.firstinspires.ftc.teamcode.config.Subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;



import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Standalone backup drive for diagnostic OpModes. Do not instantiate alongside Pedro:
 * this constructor changes the configuration of the same four drive motors.
 */
public class DriveSubsystem{
    private final DcMotorEx frontLeft, frontRight, rearLeft, rearRight;
    public final IMU imu;

    /**
     * Constructor
     * @param hw Hardwaremap
     */
    public DriveSubsystem (HardwareMap hw) {
        frontLeft = hw.get(DcMotorEx.class, "lf");
        frontRight = hw.get(DcMotorEx.class, "rf");
        rearLeft = hw.get(DcMotorEx.class, "lb");
        rearRight = hw.get(DcMotorEx.class, "rb");

        imu = hw.get(IMU.class, "imu");

        imu.initialize(
                new IMU.Parameters(new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                        RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                ))
        );

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Orientation for drivetrain
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        rearLeft.setDirection(DcMotor.Direction.FORWARD);
        rearRight.setDirection(DcMotor.Direction.REVERSE);

        resetAngle();
    } // init

    /**
     * Drive the robot based on gamepad input
     * @param x
     * @param y
     * @param turn
     */
    public void drive(double x, double y, double turn) {
        double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Corrected field-centric math (if you use IMU)
        double rotatedX = x * Math.cos(-heading) - y * Math.sin(-heading);
        double rotatedY = x * Math.sin(-heading) + y * Math.cos(-heading);

        // Calculate raw motor powers
        double frontLeftPower = rotatedY + rotatedX + turn;
        double frontRightPower = rotatedY - rotatedX - turn;
        double rearLeftPower = rotatedY - rotatedX + turn;
        double rearRightPower = rotatedY + rotatedX - turn;

        // Normalize powers if any exceeds 1.0
        double max = Math.max(
                1.0,
                Math.max(
                        Math.abs(frontLeftPower),
                        Math.max(Math.abs(frontRightPower),
                                Math.max(Math.abs(rearLeftPower), Math.abs(rearRightPower)))
                )
        );

        frontLeftPower /= max;
        frontRightPower /= max;
        rearLeftPower /= max;
        rearRightPower /= max;

        // Send power to motors
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        rearLeft.setPower(rearLeftPower);
        rearRight.setPower(rearRightPower);
    }

    public void resetAngle() {
        imu.resetYaw();
    }

    public double getAngle(){
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }


    public void setFrontLeftPower(double power) {
        frontLeft.setPower(power);
    }

    public void setFrontRightPower(double power) {
        frontRight.setPower(power);
    }

    public void setRearLeftPower(double power) {
        rearLeft.setPower(power);
    }

    public void setRearRightPower(double power) {
        rearRight.setPower(power);
    }



} // DriveTrain
