package org.firstinspires.ftc.teamcode.config.Commands.Command;

import com.seattlesolvers.solverslib.command.InstantCommand;
import org.firstinspires.ftc.teamcode.config.Subsystem.IntakeSubsystem;

public class IntakeCommand extends InstantCommand {
    public IntakeCommand(IntakeSubsystem intake, double power) {
        super(() -> intake.run(power), intake);
    }
}