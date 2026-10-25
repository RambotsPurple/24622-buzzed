package org.firstinspires.ftc.teamcode.opmode.misc;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.controller.PDController;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.config.Subsystem.DriveSubsystem;
import org.firstinspires.ftc.teamcode.config.Subsystem.LimeLightSubsystem;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;

import java.util.List;

@TeleOp(name = "Rotation PD Tuner", group = "Tuning")
public class RotationPDTuner extends OpMode {
    // Change to RED to use the red AprilTag pipeline.
    private static final Alliance ALLIANCE = Alliance.BLUE;
    private static final double MAX_TURN = 0.3;
    private final double[] gains = {0.0, 0.0}; // P, D; start with P only.
    private final double[] steps = {0.001, 0.0001};
    private final PDController pd = new PDController(0, 0);
    private final ElapsedTime loopTimer = new ElapsedTime();
    private DriveSubsystem drive;
    private LimeLightSubsystem vision;
    private Limelight3A camera;
    private List<LynxModule> hubs;
    private GamepadEx pad;
    private int selected = 0;
    private double multiplier = 1.0;
    private double turnSign = 1.0;

    @Override
    public void init() {
        drive = new DriveSubsystem(hardwareMap);
        drive.drive(0, 0, 0);
        vision = new LimeLightSubsystem(hardwareMap, ALLIANCE);
        camera = hardwareMap.get(Limelight3A.class, "limelight");
        hubs = hardwareMap.getAll(LynxModule.class);
        pad = new GamepadEx(gamepad1);
        telemetry.addLine("Rotation only. Gains start at zero.");
        telemetry.addLine("Hold A to track; release to stop.");
        telemetry.addData("Alliance / pipeline", ALLIANCE);
    }

    @Override
    public void start() {
        vision.lStart();
        pd.reset();
        loopTimer.reset();
    }

    @Override
    public void loop() {
        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }
        pad.readButtons();
        if (pad.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)) selected = 0;
        if (pad.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) selected = 1;
        if (pad.wasJustPressed(GamepadKeys.Button.X)) multiplier = Math.max(0.01, multiplier / 10);
        if (pad.wasJustPressed(GamepadKeys.Button.B)) multiplier = Math.min(100, multiplier * 10);
        if (pad.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            gains[selected] += steps[selected] * multiplier;
            pd.reset();
        }
        if (pad.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {
            gains[selected] = Math.max(0, gains[selected] - steps[selected] * multiplier);
            pd.reset();
        }
        if (pad.wasJustPressed(GamepadKeys.Button.Y)) {
            turnSign *= -1;
            pd.reset();
        }
        pd.setP(gains[0]);
        pd.setD(gains[1]);

        Double error = vision.getVisibleTagHorizontalError();
        boolean tracking = pad.getButton(GamepadKeys.Button.A) && error != null;
        double rawTurn = 0;
        if (tracking) {
            rawTurn = turnSign * pd.calculate(error, 0);
        } else {
            pd.reset();
        }
        double turn = Range.clip(rawTurn, -MAX_TURN, MAX_TURN);
        drive.drive(0, 0, turn);

        telemetry.addData("State", tracking ? "TRACKING" : "STOPPED");
        telemetry.addData("Alliance", ALLIANCE);
        telemetry.addData("Tag visible", error != null);
        if (error != null) telemetry.addData("Aim error (deg)", "%.2f", error);
        telemetry.addData("P / D", "%.6f / %.6f", gains[0], gains[1]);
        telemetry.addData("Selected / step", "%s / %.6f",
                selected == 0 ? "P" : "D", steps[selected] * multiplier);
        telemetry.addData("Turn sign", "%.0f", turnSign);
        telemetry.addData("Turn (raw / limited)", "%.3f / %.3f", rawTurn, turn);
        telemetry.addData("Loop (ms)", "%.1f", loopTimer.milliseconds());
        loopTimer.reset();
        telemetry.addLine("Hold A: track | D-pad left/right: select P/D");
        telemetry.addLine("LB/RB: decrease/increase | X/B: finer/coarser step");
        telemetry.addLine("Y: reverse turn sign | Copy final gains into Robot.java");
        telemetry.update();
    }

    @Override
    public void stop() {
        if (drive != null) drive.drive(0, 0, 0);
        if (camera != null) camera.stop();
        pd.reset();
    }
}
