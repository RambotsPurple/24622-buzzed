package org.firstinspires.ftc.teamcode.opmode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.config.Commands.Command.IntakeCommand;
import org.firstinspires.ftc.teamcode.config.Commands.Command.SetShooterVelocityCommand;
import org.firstinspires.ftc.teamcode.config.Commands.Command.TransferCommand;
import org.firstinspires.ftc.teamcode.config.Robot;
import org.firstinspires.ftc.teamcode.config.util.Alliance;
import org.firstinspires.ftc.teamcode.config.util.paths.eight;
import org.firstinspires.ftc.teamcode.config.util.paths.straight;
import org.firstinspires.ftc.teamcode.config.util.paths.straightRED;

@Autonomous(name ="straightRed")
public class straightRed extends CommandOpMode {
    Robot robot;
    straightRED path;

    @Override
    public void initialize() {
        robot = new Robot(hardwareMap, Alliance.RED,telemetry);
        path = new straightRED();
        schedule(
                new RunCommand(() -> {
                    if (System.currentTimeMillis() % 20 < 5) {
                        robot.bulk();
                        robot.getFollower().update();
                    }
                }),
                new SequentialCommandGroup(
                        new FollowPathCommand(robot.getFollower(), path.park())
                )

        );

    }
}
