package org.firstinspires.ftc.teamcode.config.Commands.Command;

import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.config.Subsystem.TransferSubsystem;


public class TransferCommand extends InstantCommand {
    private final TransferSubsystem transferSubsystem;
    private double pow;
    public TransferCommand(TransferSubsystem sub, double power){
        transferSubsystem = sub;
        pow = power;
        addRequirements(transferSubsystem);
    }

    @Override
    public void initialize() {
        transferSubsystem.setPower(pow);
    }
}
