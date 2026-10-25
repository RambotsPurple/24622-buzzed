package org.firstinspires.ftc.teamcode.opmode.auto.allianceAutos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;
import org.firstinspires.ftc.teamcode.opmode.auto.templateAutos.FourPieceAuto;

@Autonomous(name = "red4")
public class red4 extends FourPieceAuto {
    @Override
    protected Alliance alliance() {
        return Alliance.RED;
    }
}