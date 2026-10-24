package org.firstinspires.ftc.teamcode.opmode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.config.Robot;
import org.firstinspires.ftc.teamcode.config.util.Alliance;
import org.firstinspires.ftc.teamcode.config.util.paths.eight;

@Autonomous(name ="red4")
public class red4  extends CommandOpMode {
    Robot robot;
    eight path;

    @Override
    public void initialize() {
        robot = new Robot(hardwareMap, Alliance.RED,telemetry);
        path = new eight(Alliance.RED);
        schedule(
                new SequentialCommandGroup(
                        new FollowPathCommand(robot.getFollower(),path.toballs()),
                        new FollowPathCommand(robot.getFollower(),path.tostart()),
                        new FollowPathCommand(robot.getFollower(),path.topark())
                )


        );

    }
}
