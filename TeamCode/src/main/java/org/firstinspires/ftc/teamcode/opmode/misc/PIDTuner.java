package org.firstinspires.ftc.teamcode.tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

@TeleOp(name = "PID Tuner", group = "Tuning")
public class PIDTuner extends LinearOpMode {

    private static final String MOTOR_NAME = "testMotor";

    private static final int LOW_TARGET  = 0;
    private static final int HIGH_TARGET = 1000;
    private static final int TARGET_STEP = 50;

    private static final double MAX_POWER = 0.60;

    // P, I, D
    private final double[] k = {
            0.001,
            0.0,
            0.0
    };

    private final double[] step = {
            0.0001,    // P
            0.000001,  // I
            0.00001    // D
    };

    private final String[] gainNames = {"P", "I", "D"};

    @Override
    public void runOpMode() {

        MotorEx motor = new MotorEx(hardwareMap, MOTOR_NAME);
        GamepadEx pad = new GamepadEx(gamepad1);

        PIDController pid =
                new PIDController(k[0], k[1], k[2]);

        motor.setRunMode(Motor.RunMode.RawPower);
        motor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        motor.resetEncoder();

        int target = LOW_TARGET;
        int selected = 0;

        double multiplier = 1.0;
        boolean enabled = false;

        telemetry.addLine("PID Tuner Ready");
        telemetry.addLine("A = enable");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            pad.readButtons();

            // ---------------- CONTROLS ----------------

            if (pad.wasJustPressed(GamepadKeys.Button.A)) {
                enabled = !enabled;
                pid.reset();
            }

            // Step response
            if (pad.wasJustPressed(GamepadKeys.Button.Y)) {
                target = target == LOW_TARGET
                        ? HIGH_TARGET
                        : LOW_TARGET;

                pid.reset();
            }

            // Target
            if (pad.wasJustPressed(GamepadKeys.Button.DPAD_UP))
                target += TARGET_STEP;

            if (pad.wasJustPressed(GamepadKeys.Button.DPAD_DOWN))
                target -= TARGET_STEP;


            // Select P / I / D
            if (pad.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT))
                selected = (selected + 1) % 3;

            if (pad.wasJustPressed(GamepadKeys.Button.DPAD_LEFT))
                selected = (selected + 2) % 3;


            // Change selected gain
            if (pad.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER))
                k[selected] += step[selected] * multiplier;

            if (pad.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER))
                k[selected] = Math.max(
                        0,
                        k[selected] - step[selected] * multiplier
                );


            // Fine / coarse adjustment
            if (pad.wasJustPressed(GamepadKeys.Button.X))
                multiplier = Math.max(
                        0.001,
                        multiplier / 10
                );

            if (pad.wasJustPressed(GamepadKeys.Button.B))
                multiplier = Math.min(
                        1000,
                        multiplier * 10
                );


            if (pad.wasJustPressed(GamepadKeys.Button.START))
                pid.reset();


            // ---------------- PID ----------------

            pid.setPID(
                    k[0],
                    k[1],
                    k[2]
            );

            double output = 0;

            if (enabled) {
                output = pid.calculate(
                        motor.getCurrentPosition(),
                        target
                );

                output = Range.clip(
                        output,
                        -MAX_POWER,
                        MAX_POWER
                );
            }

            motor.set(output);


            // ---------------- TELEMETRY ----------------

            telemetry.addData(
                    "STATE",
                    enabled ? "ENABLED" : "DISABLED"
            );

            telemetry.addLine();

            telemetry.addData("Target", target);
            telemetry.addData(
                    "Position",
                    motor.getCurrentPosition()
            );

            telemetry.addData(
                    "Velocity",
                    "%.1f ticks/s",
                    motor.getVelocity()
            );

            telemetry.addData(
                    "Error",
                    "%.1f",
                    pid.getPositionError()
            );

            telemetry.addData(
                    "Error Rate",
                    "%.1f",
                    pid.getVelocityError()
            );

            telemetry.addLine();

            telemetry.addData(
                    "Selected",
                    ">>> %s <<<",
                    gainNames[selected]
            );

            telemetry.addData("P", "%.7f", k[0]);
            telemetry.addData("I", "%.7f", k[1]);
            telemetry.addData("D", "%.7f", k[2]);

            telemetry.addData(
                    "Step",
                    "%.8f",
                    step[selected] * multiplier
            );

            telemetry.addLine();

            telemetry.addData(
                    "Output",
                    "%+.3f",
                    output
            );

            telemetry.addData(
                    "Loop Time",
                    "%.4f s",
                    pid.getPeriod()
            );

            telemetry.addLine();
            telemetry.addLine("A      Enable/Disable");
            telemetry.addLine("Y      0 ↔ 1000 step");
            telemetry.addLine("↑ ↓    Target");
            telemetry.addLine("← →    Select P/I/D");
            telemetry.addLine("LB/RB  Gain -/+");
            telemetry.addLine("X/B    Precision -/+");
            telemetry.addLine("START  Reset PID");

            telemetry.update();
        }

        motor.stopMotor();
    }
}