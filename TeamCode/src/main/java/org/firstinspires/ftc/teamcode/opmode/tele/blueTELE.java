package org.firstinspires.ftc.teamcode.opmode.tele;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;
import org.firstinspires.ftc.teamcode.config.util.templates.RobotTeleOp;

@TeleOp(name = "blue")
public class blueTELE extends RobotTeleOp {
    @Override
    protected Alliance alliance() {
        return Alliance.BLUE;
    }
}