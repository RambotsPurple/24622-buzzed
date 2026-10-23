package org.firstinspires.ftc.teamcode.config.Commands.Command;

import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.config.Subsystem.ShooterSubsystem;

public class SetShooterVelocityCommand extends InstantCommand {
    public SetShooterVelocityCommand(ShooterSubsystem shooter, double rpm) {
        super(() -> shooter.setVelocity(rpm), shooter);
    }
}
