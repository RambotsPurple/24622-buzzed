package org.firstinspires.ftc.teamcode.config.Commands.CommandGroups;

import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import org.firstinspires.ftc.teamcode.config.Commands.Command.IntakeCommand;
import org.firstinspires.ftc.teamcode.config.Commands.Command.TransferCommand;
import org.firstinspires.ftc.teamcode.config.Subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.config.Subsystem.TransferSubsystem;

public class OutputCommandGroup extends ParallelCommandGroup {
    public OutputCommandGroup(IntakeSubsystem intake, TransferSubsystem transfer, double power) {
        super(new IntakeCommand(intake, power), new TransferCommand(transfer, power));
    }
}