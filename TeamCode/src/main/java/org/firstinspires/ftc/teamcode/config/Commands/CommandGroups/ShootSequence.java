package org.firstinspires.ftc.teamcode.config.Commands.CommandGroups;

import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import org.firstinspires.ftc.teamcode.config.Commands.Command.SetShooterVelocityCommand;
import org.firstinspires.ftc.teamcode.config.Subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.config.Subsystem.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.config.Subsystem.TransferSubsystem;

/** Spin up, feed for a fixed interval, then stop all shooting mechanisms. */
public class ShootSequence extends SequentialCommandGroup {
    private static final long SPIN_UP_MS = 3000;
    private static final long FEED_MS = 1500;

    public ShootSequence(ShooterSubsystem shooter, IntakeSubsystem intake,
                         TransferSubsystem transfer, double rpm) {
        super(
                new SetShooterVelocityCommand(shooter, rpm),
                new WaitCommand(SPIN_UP_MS),
                new OutputCommandGroup(intake, transfer, 1),
                new WaitCommand(FEED_MS),
                new ParallelCommandGroup(
                        new SetShooterVelocityCommand(shooter, 0),
                        new OutputCommandGroup(intake, transfer, 0))
        );
    }
}