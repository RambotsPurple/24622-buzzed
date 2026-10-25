package org.firstinspires.ftc.teamcode.config.util.templates;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import org.firstinspires.ftc.teamcode.config.Robot;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;

/** One follower update and one scheduler run per active loop, for either alliance. */
public abstract class PedroAuto extends CommandOpMode {
    protected Robot robot;
    private Command routine;

    protected abstract Alliance alliance();
    protected abstract Command createRoutine();

    @Override
    public final void initialize() {
        reset();
        robot = new Robot(hardwareMap, alliance(), telemetry);
        routine = createRoutine();
    }

    @Override
    public final void initialize_loop() {
        telemetry.addData("Alliance", robot.getAlliance());
        telemetry.addData("Starting pose", robot.getFollower().pose());
        telemetry.update();
    }

    @Override
    public final void preRun() {
        // Scheduling initializes commands immediately, so wait for Play.
        robot.tStart();
        schedule(routine);
    }

    @Override
    public final void run() {
        robot.bulk();
        robot.getFollower().update();
        super.run();
        robot.updateTelemetry();
    }

    @Override
    public final void end() {
        CommandScheduler.getInstance().cancelAll();
        reset();
        if (robot != null) {
            robot.stop();
        }
    }
}
