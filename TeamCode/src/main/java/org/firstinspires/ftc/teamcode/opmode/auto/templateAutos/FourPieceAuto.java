package org.firstinspires.ftc.teamcode.opmode.auto.templateAutos;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;
import org.firstinspires.ftc.teamcode.config.Commands.Command.IntakeCommand;
import org.firstinspires.ftc.teamcode.config.Commands.CommandGroups.ShootSequence;
import org.firstinspires.ftc.teamcode.config.util.templates.PedroAuto;
import org.firstinspires.ftc.teamcode.config.util.paths.eight;

/** Both alliances share the red routine's timing and shooter speeds. */
public abstract class FourPieceAuto extends PedroAuto {
    @Override
    protected final Command createRoutine() {
        eight path = new eight(alliance());
        robot.getFollower().setPose(path.startPose());
        return new SequentialCommandGroup(
                new ShootSequence(robot.shooterSubsystem, robot.intakeSubsystem,
                        robot.transferSubsystem, 3200),
                new FollowPathCommand(robot.getFollower(), path.toBalls()),
                new IntakeCommand(robot.intakeSubsystem, 1),
                new WaitCommand(1500),
                new IntakeCommand(robot.intakeSubsystem, 0),
                new FollowPathCommand(robot.getFollower(), path.toNextHive()),
                new ShootSequence(robot.shooterSubsystem, robot.intakeSubsystem,
                        robot.transferSubsystem, 2900),
                new FollowPathCommand(robot.getFollower(), path.toPark())
        );
    }
}