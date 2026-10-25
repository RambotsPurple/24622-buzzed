package org.firstinspires.ftc.teamcode.opmode.auto.allianceAutos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;
import org.firstinspires.ftc.teamcode.opmode.auto.templateAutos.FourPieceAuto;

@Autonomous(name = "blue4")
public class blue4 extends FourPieceAuto {
    @Override
    protected Alliance alliance() {
        return Alliance.BLUE;
    }
}