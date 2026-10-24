package org.firstinspires.ftc.teamcode.opmode.auto;

import com.pedropathing.math.Pose;
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
import org.firstinspires.ftc.teamcode.config.util.paths.eightRed;

@Autonomous(name ="red4")
public class red4  extends CommandOpMode {
    Robot robot;
    eightRed path;

    @Override
    public void initialize() {
        robot = new Robot(hardwareMap, Alliance.RED,telemetry);
        path = new eightRed();
        robot.getFollower().setPose(new Pose(56, 8, 90));
                schedule(
                new RunCommand(() -> {
                    if (System.currentTimeMillis() % 20 < 5) {
                        robot.bulk();
                        robot.getFollower().update();
                        telemetry.addData("pose:",robot.getFollower().pose());
                        telemetry.addData("closest pose:",robot.getFollower().closestPose());
                        telemetry.addData("closest pose algo:",robot.getFollower().algorithm().closestPose());
                        telemetry.update();
                    }
                }),
                new SequentialCommandGroup(
                        new SetShooterVelocityCommand(robot.shooterSubsystem,3200),
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
                        new SetShooterVelocityCommand(robot.shooterSubsystem,2900),
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
