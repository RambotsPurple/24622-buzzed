
package org.firstinspires.ftc.teamcode.opmode.misc;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import com.seattlesolvers.solverslib.controller.PIDController;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

@TeleOp(name = "Flywheel PIDF Tuner", group = "Tuning")
public class PIDTuner extends LinearOpMode {

    private static final String MOTOR_NAME = "testMotor";

    // Velocity units: encoder ticks/second
    private static final double LOW_TARGET = 0;
    private static final double HIGH_TARGET = 1000;
    private static final double TARGET_STEP = 100;

    private static final double MAX_POWER = 1;

    // P, I, D, F
    private final double[] k = {
            0.0001,
            0.0,
            0.0,
            0.0
    };

    private final double[] step = {
            0.00001,   // P
            0.000001,  // I
            0.000001,  // D
            0.00001    // F
    };

    private final String[] gainNames = {
            "P", "I", "D", "F"
    };

    @Override
    public void runOpMode() {

        MotorEx motor = new MotorEx(hardwareMap, MOTOR_NAME);

        PIDController pid = new PIDController(
                k[0], k[1], k[2]
        );
        motor.setInverted(true);

        motor.setRunMode(Motor.RunMode.RawPower);
        motor.setZeroPowerBehavior(
                Motor.ZeroPowerBehavior.FLOAT
        );
        motor.resetEncoder();

        double target = LOW_TARGET;
        int selected = 0;
        double multiplier = 1.0;
        boolean enabled = false;

        // Previous button states
        boolean prevA = false;
        boolean prevB = false;
        boolean prevX = false;
        boolean prevY = false;

        boolean prevUp = false;
        boolean prevDown = false;
        boolean prevLeft = false;
        boolean prevRight = false;

        boolean prevLB = false;
        boolean prevRB = false;
        boolean prevStart = false;
        boolean prevBack = false;

        telemetry.addLine("Flywheel PIDF Tuner Ready");
        telemetry.addLine("DPAD LEFT/RIGHT = Select P/I/D/F");
        telemetry.addLine("A = Enable/Disable");
        telemetry.addLine("START = Full Reset");
        telemetry.update();

        waitForStart();

        try {
            while (opModeIsActive()) {

                // -------- BUTTON EDGE DETECTION --------

                boolean a = gamepad1.a && !prevA;
                boolean b = gamepad1.b && !prevB;
                boolean x = gamepad1.x && !prevX;
                boolean y = gamepad1.y && !prevY;

                boolean up =
                        gamepad1.dpad_up && !prevUp;
                boolean down =
                        gamepad1.dpad_down && !prevDown;
                boolean left =
                        gamepad1.dpad_left && !prevLeft;
                boolean right =
                        gamepad1.dpad_right && !prevRight;

                boolean lb =
                        gamepad1.left_bumper && !prevLB;
                boolean rb =
                        gamepad1.right_bumper && !prevRB;

                boolean start =
                        gamepad1.start && !prevStart;
                boolean back =
                        gamepad1.back && !prevBack;

                prevA = gamepad1.a;
                prevB = gamepad1.b;
                prevX = gamepad1.x;
                prevY = gamepad1.y;

                prevUp = gamepad1.dpad_up;
                prevDown = gamepad1.dpad_down;
                prevLeft = gamepad1.dpad_left;
                prevRight = gamepad1.dpad_right;

                prevLB = gamepad1.left_bumper;
                prevRB = gamepad1.right_bumper;
                prevStart = gamepad1.start;
                prevBack = gamepad1.back;

                // -------- ENABLE / DISABLE --------

                if (a) {
                    enabled = !enabled;
                    pid.reset();
                }

                // -------- STEP RESPONSE --------

                if (y) {
                    target = target == LOW_TARGET
                            ? HIGH_TARGET
                            : LOW_TARGET;

                    pid.reset();
                }

                // -------- TARGET VELOCITY --------

                if (up)
                    target += TARGET_STEP;

                if (down)
                    target = Math.max(
                            0,
                            target - TARGET_STEP
                    );

                // -------- SELECT P / I / D / F --------

                if (right)
                    selected = (selected + 1) % 4;

                if (left)
                    selected = (selected + 3) % 4;

                // -------- ADJUST GAIN --------

                if (rb)
                    k[selected] +=
                            step[selected] * multiplier;

                if (lb)
                    k[selected] = Math.max(
                            0,
                            k[selected] -
                                    step[selected] * multiplier
                    );

                // -------- PRECISION --------

                if (x)
                    multiplier = Math.max(
                            0.001,
                            multiplier / 10
                    );

                if (b)
                    multiplier = Math.min(
                            1000,
                            multiplier * 10
                    );

                // -------- RESET PID ERRORS --------

                if (back) {
                    pid.reset();
                }

                // -------- FULL RESET --------

                if (start) {
                    enabled = false;
                    target = LOW_TARGET;

                    motor.set(0);
                    pid.reset();
                    motor.resetEncoder();
                }

                // -------- PIDF VELOCITY CONTROL --------

                pid.setPID(k[0], k[1], k[2]);

                double velocity = motor.getVelocity();
                double error = target - velocity;

                double pidOutput = 0;
                double ffOutput = 0;
                double output = 0;

                if (enabled && target > 0) {

                    pidOutput = pid.calculate(
                            velocity,
                            target
                    );

                    // Velocity-proportional feedforward
                    ffOutput = k[3] * target;

                    output = Range.clip(
                            pidOutput + ffOutput,
                            -MAX_POWER,
                            MAX_POWER
                    );

                } else {
                    pid.reset();
                }

                motor.set(output);

                // -------- TELEMETRY --------

                telemetry.addData(
                        "STATE",
                        enabled ? "ENABLED" : "DISABLED"
                );

                telemetry.addLine();

                telemetry.addData(
                        "Target Velocity",
                        "%.1f ticks/s", target
                );

                telemetry.addData(
                        "Actual Velocity",
                        "%.1f ticks/s", velocity
                );

                telemetry.addData(
                        "Velocity Error",
                        "%+.1f ticks/s", error
                );

                telemetry.addData(
                        "Error Derivative",
                        "%+.1f ticks/s^2",
                        enabled && target > 0
                                ? pid.getVelocityError() : 0.0
                );

                telemetry.addData(
                        "Encoder Position",
                        "%d ticks",
                        motor.getCurrentPosition()
                );

                telemetry.addLine();

                telemetry.addData(
                        "SELECTED GAIN",
                        ">>> %s <<<",
                        gainNames[selected]
                );

                for (int i = 0; i < 4; i++) {
                    telemetry.addData(
                            i == selected
                                    ? ">>> " + gainNames[i]
                                    : "    " + gainNames[i],
                            "%.8f", k[i]
                    );
                }

                telemetry.addData(
                        "Adjustment Step",
                        "%.9f",
                        step[selected] * multiplier
                );

                telemetry.addData(
                        "Multiplier",
                        "%.3fx",
                        multiplier
                );

                telemetry.addLine();

                telemetry.addData(
                        "PID Output",
                        "%+.4f", pidOutput
                );

                telemetry.addData(
                        "FF Output",
                        "%+.4f", ffOutput
                );

                telemetry.addData(
                        "Motor Power",
                        "%+.4f", output
                );

                telemetry.addData(
                        "Loop Time",
                        "%.4f s", pid.getPeriod()
                );

                telemetry.addLine();

                telemetry.addLine("---- CONTROLS ----");
                telemetry.addLine("A      Enable/Disable");
                telemetry.addLine("Y      Step 0/1000");
                telemetry.addLine("UP/DN  Velocity Target");
                telemetry.addLine("LT/RT  Select P/I/D/F");
                telemetry.addLine("LB/RB  Gain -/+");
                telemetry.addLine("X/B    Precision -/+");
                telemetry.addLine("START  Full Reset");
                telemetry.addLine("BACK   Reset PID Errors");

                telemetry.update();
            }
        } finally {
            motor.stopMotor();
        }
    }
}
