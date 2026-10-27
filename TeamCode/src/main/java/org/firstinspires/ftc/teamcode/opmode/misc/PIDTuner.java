package org.firstinspires.ftc.teamcode.opmode.misc;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
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
    private static final long DISPLAY_PERIOD_MS = 50; // 20 Hz graph and DS telemetry

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

        // Panels 1.0.x: Graph reads numeric keys from Panels Telemetry.
        // Do not use the deprecated GraphManager API.
        TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

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

        telemetry.setMsTransmissionInterval((int) DISPLAY_PERIOD_MS);
        telemetry.addLine("RPM tuner: motor s, 28 ticks/rev, competition PIDF and 0..1 power.");
        telemetry.addLine("Gains start at zero. F selected. A enables; RB increases selected gain.");
        telemetry.addLine("Left-stick click: arm shot capture after spin-up. Right-stick click: clear.");
        telemetry.addLine("Panels Graph: select shooter.targetRpm and shooter.actualRpm.");
        telemetry.update();
        waitForStart();

        try {
            while (opModeIsActive()) {
                for (LynxModule hub : hubs) hub.clearBulkCache();
                double now = clock.seconds();
                if (lastLoopTime != 0) {
                    maxLoopMs = Math.max(maxLoopMs, (now - lastLoopTime) * 1000);
                }
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
                if (pressed[0]) { // A: enable/disable
                    enabled = !enabled;
                    controlChanged = true;
                }
                if (pressed[1]) multiplier = Math.min(1000, multiplier * 10);
                if (pressed[2]) multiplier = Math.max(0.001, multiplier / 10);
                if (pressed[3]) { // Y: toggle target between zero and default RPM
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
                if (pressed[10]) { // Start: reset tune
                    enabled = false;
                    targetRpm = SHOOTING_RPM;
                    for (int i = 0; i < gains.length; i++) gains[i] = 0;
                    selectedGain = 3;
                    multiplier = 1;
                    controlChanged = true;
                }
            }
        }
    }
}