package org.firstinspires.ftc.teamcode.opmode.auto.templateAutos;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.pedroCommand.FollowPathCommand;

import org.firstinspires.ftc.teamcode.config.util.templates.PedroAuto;
import org.firstinspires.ftc.teamcode.config.util.paths.straight;

public abstract class StraightAuto extends PedroAuto {
    @Override
    protected final Command createRoutine() {
        straight path = new straight(alliance());
        robot.getFollower().setPose(path.startPose());
        return new FollowPathCommand(robot.getFollower(), path.park());
    }
}