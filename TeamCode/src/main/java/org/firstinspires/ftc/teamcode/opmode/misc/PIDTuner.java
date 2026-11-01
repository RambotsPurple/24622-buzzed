
package org.firstinspires.ftc.teamcode.opmode.misc;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import com.seattlesolvers.solverslib.controller.PIDFController;

import org.firstinspires.ftc.teamcode.config.Subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.config.Subsystem.TransferSubsystem;

import java.util.Locale;

@TeleOp(name = "Flywheel PIDF Tuner", group = "Tuning")
public class PIDTuner extends LinearOpMode {

    // ---------- Hardware ----------
    private static final String MOTOR_NAME = "s";
    private static final double TICKS_PER_REV = 28.0;

    // ---------- Targets ----------
    private static final double DEFAULT_RPM = 3200.0;
    private static final double RPM_STEP = 100.0;
    private static final double MAX_TARGET_RPM = 6000.0;

    // ---------- Telemetry ----------
    private static final double GRAPH_INTERVAL = 0.05; // 20 Hz
    private static final double READY_TOLERANCE = 75.0;

    // ---------- Tuning ----------
    private static final String[] GAIN_NAMES = {
            "P", "I", "D", "F"
    };

    private static final double[] GAIN_STEPS = {
            0.00001,
            0.000001,
            0.000001,
            0.00001
    };

    private final double[] gains = {0, 0, 0, 0};

    private IntakeSubsystem intake;
    private TransferSubsystem transfer;

    @Override
    public void runOpMode() {

        // ---------- Hardware initialization ----------
        DcMotorEx motor = hardwareMap.get(
                DcMotorEx.class, MOTOR_NAME
        );

        motor.setDirection(DcMotor.Direction.REVERSE);
        motor.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.FLOAT
        );
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0);

        intake = new IntakeSubsystem(hardwareMap);
        transfer = new TransferSubsystem(hardwareMap);

        intake.run(0);
        transfer.setPower(0);

        // ---------- Controller ----------
        PIDFController pidf =
                new PIDFController(0, 0, 0, 0);

        // ---------- State ----------
        double targetRpm = DEFAULT_RPM;
        double actualRpm = 0;
        double motorPower = 0;
        double errorRpm = 0;

        double multiplier = 1.0;
        int selectedGain = 3; // Start on F

        boolean enabled = false;

        boolean[] previous = new boolean[14];

        ElapsedTime clock = new ElapsedTime();
        ElapsedTime graphTimer = new ElapsedTime();

        // ---------- Shot capture ----------
        boolean capturing = false;
        boolean shotStarted = false;
        double minShotRpm = Double.POSITIVE_INFINITY;
        double shotStartTime = 0;
        double recoveryTime = -1;
        double shotDropRpm = 0;

        telemetry.setMsTransmissionInterval(50);

        telemetry.addLine("Flywheel PIDF Tuner");
        telemetry.addLine("A: enable | Y: target 0/default");
        telemetry.addLine("Dpad L/R: select P/I/D/F");
        telemetry.addLine("LB/RB: decrease/increase gain");
        telemetry.addLine("B/X: gain step x10 / /10");
        telemetry.addLine("Dpad Up/Down: target +/-100 RPM");
        telemetry.addLine("LS: arm shot capture");
        telemetry.addLine("RS: reset controller and capture");
        telemetry.addLine("Start: reset all gains");
        telemetry.addLine("Gamepad 2 RT: intake + transfer");
        telemetry.update();

        waitForStart();

        clock.reset();
        graphTimer.reset();

        try {
            while (opModeIsActive()) {

                double now = clock.seconds();

                // ---------- Button edge detection ----------
                boolean[] buttons = {
                        gamepad1.a,
                        gamepad1.b,
                        gamepad1.x,
                        gamepad1.y,
                        gamepad1.dpad_up,
                        gamepad1.dpad_down,
                        gamepad1.dpad_left,
                        gamepad1.dpad_right,
                        gamepad1.left_bumper,
                        gamepad1.right_bumper,
                        gamepad1.start,
                        gamepad1.back,
                        gamepad1.left_stick_button,
                        gamepad1.right_stick_button
                };

                boolean[] pressed = new boolean[buttons.length];

                for (int i = 0; i < buttons.length; i++) {
                    pressed[i] = buttons[i] && !previous[i];
                }

                System.arraycopy(
                        buttons, 0, previous, 0, buttons.length
                );

                boolean resetController = false;
                boolean gainsChanged = false;

                // ---------- Enable / disable ----------
                if (pressed[0]) {
                    enabled = !enabled;
                    resetController = true;
                }

                // ---------- Step multiplier ----------
                if (pressed[1]) {
                    multiplier = Math.min(
                            1000, multiplier * 10
                    );
                }

                if (pressed[2]) {
                    multiplier = Math.max(
                            0.001, multiplier / 10
                    );
                }

                // ---------- Target control ----------
                if (pressed[3]) {
                    targetRpm = targetRpm == 0
                            ? DEFAULT_RPM : 0;
                    resetController = true;
                }

                if (pressed[4]) {
                    targetRpm = Math.min(
                            MAX_TARGET_RPM,
                            targetRpm + RPM_STEP
                    );
                    resetController = true;
                }

                if (pressed[5]) {
                    targetRpm = Math.max(
                            0, targetRpm - RPM_STEP
                    );
                    resetController = true;
                }

                // ---------- Select coefficient ----------
                if (pressed[6]) {
                    selectedGain = (selectedGain + 3) % 4;
                }

                if (pressed[7]) {
                    selectedGain = (selectedGain + 1) % 4;
                }

                // ---------- Decrease coefficient ----------
                if (pressed[8]) {
                    gains[selectedGain] = Math.max(
                            0,
                            gains[selectedGain]
                                    - GAIN_STEPS[selectedGain]
                                    * multiplier
                    );
                    gainsChanged = true;
                }

                // ---------- Increase coefficient ----------
                if (pressed[9]) {
                    gains[selectedGain] +=
                            GAIN_STEPS[selectedGain] * multiplier;
                    gainsChanged = true;
                }

                // ---------- Full tuning reset ----------
                if (pressed[10]) {
                    enabled = false;
                    targetRpm = DEFAULT_RPM;

                    for (int i = 0; i < gains.length; i++) {
                        gains[i] = 0;
                    }

                    selectedGain = 3;
                    multiplier = 1;

                    gainsChanged = true;
                    resetController = true;
                }

                // ---------- Shot capture arm ----------
                if (pressed[12]) {
                    capturing = true;
                    shotStarted = false;
                    minShotRpm = Double.POSITIVE_INFINITY;
                    recoveryTime = -1;
                    shotDropRpm = 0;
                }

                // ---------- Reset PID state ----------
                if (pressed[13]) {
                    resetController = true;
                    capturing = false;
                    shotStarted = false;
                    minShotRpm = Double.POSITIVE_INFINITY;
                    recoveryTime = -1;
                    shotDropRpm = 0;
                }

                // ---------- Apply coefficients ----------
                if (gainsChanged) {
                    pidf.setPIDF(
                            gains[0],
                            gains[1],
                            gains[2],
                            gains[3]
                    );
                }

                if (resetController) {
                    pidf.reset();
                }

                // ---------- Read motor velocity ----------
                double ticksPerSecond = motor.getVelocity();

                actualRpm =
                        Math.abs(ticksPerSecond)
                                * 60.0 / TICKS_PER_REV;

                errorRpm = targetRpm - actualRpm;

                // ---------- PIDF calculation ----------
                if (enabled && targetRpm > 0) {

                    double output = pidf.calculate(
                            actualRpm,
                            targetRpm
                    );

                    motorPower = Range.clip(output, 0, 1);

                } else {
                    motorPower = 0;
                }

                motor.setPower(motorPower);

                // ---------- Intake / transfer ----------
                boolean feeding =
                        gamepad2.right_trigger > 0.2
                                && enabled
                                && targetRpm > 0;

                if (feeding) {
                    intake.run(1);
                    transfer.setPower(1);
                } else {
                    intake.run(0);
                    transfer.setPower(0);
                }

                // ---------- Shot capture ----------
                if (capturing && feeding && !shotStarted) {
                    shotStarted = true;
                    shotStartTime = now;
                    minShotRpm = actualRpm;
                }

                if (capturing && shotStarted) {

                    minShotRpm = Math.min(
                            minShotRpm, actualRpm
                    );

                    shotDropRpm = Math.max(
                            0, targetRpm - minShotRpm
                    );

                    boolean hasDropped =
                            shotDropRpm > READY_TOLERANCE;

                    boolean recovered =
                            Math.abs(errorRpm)
                                    <= READY_TOLERANCE;

                    if (hasDropped
                            && recovered
                            && recoveryTime < 0) {

                        recoveryTime = now - shotStartTime;
                        capturing = false;
                    }
                }

                // ---------- Driver Station telemetry ----------
                if (graphTimer.seconds() >= GRAPH_INTERVAL) {

                    boolean ready =
                            enabled
                                    && targetRpm > 0
                                    && Math.abs(errorRpm)
                                    <= READY_TOLERANCE;

                    // Driver Station telemetry
                    telemetry.addData("Enabled", enabled);
                    telemetry.addData("Ready", ready);

                    telemetry.addData(
                            "Target RPM", "%.0f", targetRpm
                    );

                    telemetry.addData(
                            "Actual RPM", "%.1f", actualRpm
                    );

                    telemetry.addData(
                            "Error RPM", "%.1f", errorRpm
                    );

                    telemetry.addData(
                            "Motor Power", "%.3f", motorPower
                    );

                    telemetry.addData(
                            "Selected Gain",
                            GAIN_NAMES[selectedGain]
                    );

                    telemetry.addData(
                            "Multiplier", multiplier
                    );

                    telemetry.addData(
                            "Gain Step", "%.8f",
                            GAIN_STEPS[selectedGain] * multiplier
                    );

                    telemetry.addData(
                            "PIDF",
                            String.format(
                                    Locale.US,
                                    "P=%.7f I=%.7f D=%.7f F=%.7f",
                                    gains[0],
                                    gains[1],
                                    gains[2],
                                    gains[3]
                            )
                    );

                    telemetry.addData(
                            "Capture Armed", capturing
                    );

                    telemetry.addData(
                            "Shot Drop RPM", "%.1f", shotDropRpm
                    );

                    telemetry.addData(
                            "Recovery Time", "%.3f", recoveryTime
                    );

                    telemetry.update();
                    graphTimer.reset();
                }

                idle();
            }
        } finally {
            motor.setPower(0);
            intake.run(0);
            transfer.setPower(0);
        }
    }
}
