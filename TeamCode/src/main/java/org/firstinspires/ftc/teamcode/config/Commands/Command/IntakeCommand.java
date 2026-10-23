package org.firstinspires.ftc.teamcode.config.Commands.Command;

import com.seattlesolvers.solverslib.command.InstantCommand;

import org.firstinspires.ftc.teamcode.config.Subsystem.IntakeSubsystem;

public class IntakeCommand extends InstantCommand {
    private final IntakeSubsystem intakeSubsystem;
    private double pow;
    public IntakeCommand(IntakeSubsystem sub, double power){
        intakeSubsystem = sub;
        pow = power;
        addRequirements(intakeSubsystem);
    }

    @Override
    public void initialize() {
        intakeSubsystem.run(pow);
    }


}