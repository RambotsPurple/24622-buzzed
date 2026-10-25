package org.firstinspires.ftc.teamcode.opmode.auto.allianceAutos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;
import org.firstinspires.ftc.teamcode.opmode.auto.templateAutos.StraightAuto;

@Autonomous(name = "straightBlue")
public class straightBlue extends StraightAuto {
    @Override
    protected Alliance alliance() {
        return Alliance.BLUE;
    }
}