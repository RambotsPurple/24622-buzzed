package org.firstinspires.ftc.teamcode.opmode.misc;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.controller.PIDFController;
import org.firstinspires.ftc.teamcode.config.Subsystem.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.config.util.FlywheelShotCapture;
import java.util.List;

@TeleOp(name = "Flywheel PIDF Tuner", group = "Tuning")
public class PIDTuner extends LinearOpMode {
    // Match competition hardware, units, controller, direction and output range.
    private static final String MOTOR_NAME = "s";
    private static final double SHOOTING_RPM = 3200;
    private static final double TARGET_STEP_RPM = 100;
    private static final String[] GAIN_NAMES = {"P", "I", "D", "F"};
    private static final double[] GAIN_STEPS = {0.00001, 0.000001, 0.000001, 0.00001};

    // Begin with F only; these are tuning-session values, not competition gains.
    private final double[] gains = {0, 0, 0, 0};

    @Override
    public void runOpMode() {
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, MOTOR_NAME);
        motor.setDirection(DcMotor.Direction.REVERSE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0);
        List<LynxModule> hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        PIDFController controller = new PIDFController(0, 0, 0, 0);
        FlywheelShotCapture shot = new FlywheelShotCapture();
        ElapsedTime clock = new ElapsedTime();
        ElapsedTime displayTimer = new ElapsedTime();
        double targetRpm = SHOOTING_RPM;
        double multiplier = 1;
        int selectedGain = 3;
        boolean enabled = false;
        boolean[] previous = new boolean[14];
        String captureMessage = "Wait for steady speed, then click left stick to arm.";
        double lastLoopTime = 0;
        double maxLoopMs = 0;

        telemetry.setMsTransmissionInterval(50);
        telemetry.addLine("RPM tuner: motor s, 28 ticks/rev, competition PIDF and 0..1 power.");
        telemetry.addLine("Gains start at zero. F selected. A enables; RB increases selected gain.");
        telemetry.addLine("Left-stick click: arm shot capture after spin-up. Right-stick click: clear.");
        telemetry.update();
        waitForStart();

        try {
            while (opModeIsActive()) {
                for (LynxModule hub : hubs) hub.clearBulkCache();
                double now = clock.seconds();
                if (lastLoopTime != 0) maxLoopMs = Math.max(maxLoopMs, (now - lastLoopTime) * 1000);
                lastLoopTime = now;

                boolean[] buttons = {
                        gamepad1.a, gamepad1.b, gamepad1.x, gamepad1.y,
                        gamepad1.dpad_up, gamepad1.dpad_down,
                        gamepad1.dpad_left, gamepad1.dpad_right,
                        gamepad1.left_bumper, gamepad1.right_bumper,
                        gamepad1.start, gamepad1.back,
                        gamepad1.left_stick_button, gamepad1.right_stick_button
                };
                boolean[] pressed = new boolean[buttons.length];
                for (int i = 0; i < buttons.length; i++) {
                    pressed[i] = buttons[i] && !previous[i];
                }
                previous = buttons;

                boolean controlChanged = false;
                if (pressed[0]) {
                    enabled = !enabled;
                    controlChanged = true;
                }
                if (pressed[1]) multiplier = Math.min(1000, multiplier * 10);
                if (pressed[2]) multiplier = Math.max(0.001, multiplier / 10);
                if (pressed[3]) {
                    targetRpm = targetRpm == 0 ? SHOOTING_RPM : 0;
                    controlChanged = true;
                }
                if (pressed[4]) {
                    targetRpm += TARGET_STEP_RPM;
                    controlChanged = true;
                }
                if (pressed[5]) {
                    targetRpm = Math.max(0, targetRpm - TARGET_STEP_RPM);
                    controlChanged = true;
                }
                if (pressed[6]) selectedGain = (selectedGain + 3) % 4;
                if (pressed[7]) selectedGain = (selectedGain + 1) % 4;
                if (pressed[8]) {
                    gains[selectedGain] = Math.max(0,
                            gains[selectedGain] - GAIN_STEPS[selectedGain] * multiplier);
                    controlChanged = true;
                }
                if (pressed[9]) {
                    gains[selectedGain] += GAIN_STEPS[selectedGain] * multiplier;
                    controlChanged = true;
                }
                if (pressed[10]) {
                    enabled = false;
                    targetRpm = SHOOTING_RPM;
                    for (int i = 0; i < gains.length; i++) gains[i] = 0;
                    selectedGain = 3;
                    multiplier = 1;
                    controlChanged = true;
                }
                if (pressed[11]) controlChanged = true;
                if (controlChanged) {
                    controller.reset();
                    shot.clear();
                    captureMessage = "Settings changed: wait for steady speed, then re-arm.";
                }
                controller.setPIDF(gains[0], gains[1], gains[2], gains[3]);

                double measuredRpm = motor.getVelocity() * 60.0 / ShooterSubsystem.TICKS_PER_REV;
                double rawPower = 0;
                double feedforwardPower = 0;
                if (enabled && targetRpm > 0) {
                    rawPower = controller.calculate(measuredRpm, targetRpm);
                    feedforwardPower = gains[3] * targetRpm;
                } else {
                    controller.reset();
                }
                double appliedPower = Range.clip(rawPower, 0, 1);
                motor.setPower(appliedPower);

                if (pressed[13]) {
                    shot.clear();
                    captureMessage = "Capture cleared.";
                } else if (pressed[12]) {
                    boolean armed = enabled && shot.arm(targetRpm, measuredRpm);
                    captureMessage = armed
                            ? "Armed: feed ONE ball, then wait for recovery."
                            : "Not armed: enable and settle within +/-50 RPM of target.";
                }
                shot.update(now, measuredRpm, appliedPower);

                // Capture runs every loop; display formatting/transmission runs at 20 Hz.
                if (displayTimer.milliseconds() >= 50) {
                    telemetry.addData("State", enabled ? "ENABLED" : "DISABLED");
                    telemetry.addData("RPM target / actual", "%.0f / %.0f", targetRpm, measuredRpm);
                    telemetry.addData("RPM error", "%+.0f", targetRpm - measuredRpm);
                    telemetry.addData("Raw / applied power", "%.3f / %.3f", rawPower, appliedPower);
                    telemetry.addData("F / PID power", "%.3f / %.3f",
                            feedforwardPower, rawPower - feedforwardPower);
                    telemetry.addData("Output at limit", rawPower >= 1 ? "FULL POWER"
                            : rawPower < 0 ? "CLAMPED TO ZERO" : "no");
                    for (int i = 0; i < gains.length; i++) {
                        telemetry.addData((i == selectedGain ? "> " : "") + GAIN_NAMES[i],
                                "%.9f", gains[i]);
                    }
                    telemetry.addData("Gain step", "%.9f", GAIN_STEPS[selectedGain] * multiplier);
                    telemetry.addData("Capture", shot.state());
                    telemetry.addLine(captureMessage);
                    if (shot.state() != FlywheelShotCapture.State.IDLE) {
                        telemetry.addData("Baseline / minimum RPM", "%.0f / %.0f",
                                shot.baselineRpm(), shot.minimumRpm());
                        telemetry.addData("Largest sampled drop RPM", "%.0f", shot.dropRpm());
                        telemetry.addData("Peak power since armed", "%.3f", shot.peakPower());
                        if (shot.state() == FlywheelShotCapture.State.COMPLETE) {
                            telemetry.addData("Recovery (ms)", "%.0f", shot.recoverySeconds() * 1000);
                        } else if (shot.state() == FlywheelShotCapture.State.TIMED_OUT) {
                            telemetry.addLine("Not settled within 5 s after detected dip.");
                        }
                    }
                    telemetry.addLine("Dip trigger: 75 RPM; recovered: +/-50 RPM for 200 ms.");
                    telemetry.addData("Longest loop since display (ms)", "%.1f", maxLoopMs);
                    telemetry.addLine("A enable | Y 0/3200 RPM | D-pad up/down target +/-100");
                    telemetry.addLine("D-pad left/right gain | LB/RB gain -/+ | X/B finer/coarser");
                    telemetry.addLine("Left stick arm | Right stick clear | Back reset PID/capture");
                    telemetry.addLine("Start stop + reset gains | Copy tuned RPM gains to ShooterSubsystem");
                    telemetry.update();
                    displayTimer.reset();
                    maxLoopMs = 0;
                }
            }
        } finally {
            motor.setPower(0);
            for (LynxModule hub : hubs) {
                hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
            }
        }
    }
}