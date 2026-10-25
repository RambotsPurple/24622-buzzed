package org.firstinspires.ftc.teamcode.config.Commands.Command;

import com.seattlesolvers.solverslib.command.InstantCommand;
import org.firstinspires.ftc.teamcode.config.Subsystem.ShooterSubsystem;

/** Open-loop motor power. Use SetShooterVelocityCommand for an RPM target. */
public class ShooterCommand extends InstantCommand {
    public ShooterCommand(ShooterSubsystem shooter, double power) {
        super(() -> shooter.setPower(power), shooter);
    }
}