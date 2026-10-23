package org.firstinspires.ftc.teamcode.config.Commands.CommandGroups;

import com.seattlesolvers.solverslib.command.ParallelCommandGroup;

import org.firstinspires.ftc.teamcode.config.Commands.Command.IntakeCommand;
import org.firstinspires.ftc.teamcode.config.Commands.Command.TransferCommand;
import org.firstinspires.ftc.teamcode.config.Subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.config.Subsystem.TransferSubsystem;

public class OutputCommandGroup extends ParallelCommandGroup {
    private double pow;
    public OutputCommandGroup(IntakeSubsystem i, TransferSubsystem t, double pow)
    {
        this.pow = pow;
        addCommands(
                new IntakeCommand(i, pow),
                new TransferCommand(t,pow)
        );
        addRequirements(i,t);
    }
}
