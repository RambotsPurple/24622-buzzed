package org.firstinspires.ftc.teamcode.config.Commands.Command;

import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.config.Subsystem.ShooterSubsystem;

public class ShooterCommand extends InstantCommand {
    public ShooterCommand(ShooterSubsystem shooter, double rpm) {
        super(() -> shooter.setPower(rpm), shooter);
    }
}
