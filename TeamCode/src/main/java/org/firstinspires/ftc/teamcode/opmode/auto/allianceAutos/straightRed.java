package org.firstinspires.ftc.teamcode.opmode.auto.allianceAutos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;
import org.firstinspires.ftc.teamcode.opmode.auto.templateAutos.StraightAuto;

@Autonomous(name = "straightRed")
public class straightRed extends StraightAuto {
    @Override
    protected Alliance alliance() {
        return Alliance.RED;
    }
}