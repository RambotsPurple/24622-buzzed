package org.firstinspires.ftc.teamcode.opmode.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.CommandOpMode;
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

@Autonomous(name ="red4")
public class red4  extends CommandOpMode {
    Robot robot;
    eight path;

    @Override
    public void initialize() {
        robot = new Robot(hardwareMap, Alliance.RED,telemetry);
        path = new eight(Alliance.RED);
        schedule(
                new RunCommand(() -> {
                    if (System.currentTimeMillis() % 20 < 5) {
                        robot.bulk();
                    }
                }),
                new SequentialCommandGroup(
                        new SetShooterVelocityCommand(robot.shooterSubsystem,4000),
                        new WaitCommand(3000),
                        new ParallelCommandGroup(
                                new IntakeCommand(robot.intakeSubsystem,1),
                                new TransferCommand(robot.transferSubsystem,1)

                        ),
                        new WaitCommand(1500),
                        new ParallelCommandGroup(
                                new SetShooterVelocityCommand(robot.shooterSubsystem,0),
                                new IntakeCommand(robot.intakeSubsystem,0),
                                new TransferCommand(robot.transferSubsystem,0)
                        ),
                        new FollowPathCommand(robot.getFollower(),path.toballs()),
                        new IntakeCommand(robot.intakeSubsystem,1),
                        new WaitCommand(1500),
                        new IntakeCommand(robot.intakeSubsystem,0),
                        new FollowPathCommand(robot.getFollower(),path.tostart()),
                        new SetShooterVelocityCommand(robot.shooterSubsystem,4000),
                        new WaitCommand(3000),
                        new ParallelCommandGroup(
                                new IntakeCommand(robot.intakeSubsystem,1),
                                new TransferCommand(robot.transferSubsystem,1)

                        ),
                        new WaitCommand(1500),
                        new ParallelCommandGroup(
                                new SetShooterVelocityCommand(robot.shooterSubsystem,0),
                                new IntakeCommand(robot.intakeSubsystem,0),
                                new TransferCommand(robot.transferSubsystem,0)
                        ),
                        new FollowPathCommand(robot.getFollower(),path.topark())
                )


        );


    }
}
