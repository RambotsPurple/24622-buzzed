package org.firstinspires.ftc.teamcode.config.Commands.Command;

import com.seattlesolvers.solverslib.command.InstantCommand;
import org.firstinspires.ftc.teamcode.config.Subsystem.TransferSubsystem;

public class TransferCommand extends InstantCommand {
    public TransferCommand(TransferSubsystem transfer, double power) {
        super(() -> transfer.setPower(power), transfer);
    }
}