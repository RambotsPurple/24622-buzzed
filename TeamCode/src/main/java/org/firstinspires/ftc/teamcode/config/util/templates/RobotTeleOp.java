package org.firstinspires.ftc.teamcode.config.util.templates;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import org.firstinspires.ftc.teamcode.config.Robot;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;

/** Shared lifecycle prevents red/blue controls and scheduler cleanup from drifting. */
public abstract class RobotTeleOp extends OpMode {
    private Robot robot;

    protected abstract Alliance alliance();

    @Override
    public final void init() {
        CommandScheduler.getInstance().reset();
        robot = new Robot(hardwareMap, alliance(), gamepad1, gamepad2, telemetry);
        robot.tele();
    }

    @Override
    public final void init_loop() {
        telemetry.addData("Alliance", robot.getAlliance());
        telemetry.update();
    }

    @Override
    public final void start() {
        robot.tStart();
    }

    @Override
    public final void loop() {
        robot.tPeriodic();
    }

    @Override
    public final void stop() {
        CommandScheduler.getInstance().cancelAll();
        CommandScheduler.getInstance().reset();
        if (robot != null) {
            robot.stop();
        }
    }
}
