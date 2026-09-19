package org.firstinspires.ftc.teamcode.opmode.misc;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.config.Subsystem.DriveSubsystem;

@TeleOp(name = "mech debug")
public class mecaunDebug extends LinearOpMode {

    @Override
    public void runOpMode() {

        DriveSubsystem drive = new DriveSubsystem(hardwareMap);

        boolean prevAStatus = false;  // front-left
        boolean prevBStatus = false;  // front-right
        boolean prevXStatus = false;  // back-left
        boolean prevYStatus = false;  // back-right

        boolean fl = false;
        boolean fr = false;
        boolean bl = false;
        boolean br = false;

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

//
            // --- Front Left (A) ---
            if (gamepad2.a != prevAStatus) {
                if (!gamepad2.a) { // toggle when released
                    fl = !fl;
                    drive.setFrontLeftPower(fl ? 1 : 0);
                }
                prevAStatus = gamepad2.a;
            }

            // --- Front Right (B) ---
            if (gamepad2.b != prevBStatus) {
                if (!gamepad2.b) {
                    fr = !fr;
                    drive.setFrontRightPower(fr ? 1 : 0);
                }
                prevBStatus = gamepad2.b;
            }

            // --- Back Left (X) ---
            if (gamepad2.x != prevXStatus) {
                if (!gamepad2.x) {
                    bl = !bl;
                    drive.setRearLeftPower(bl ? 1 : 0);
                }
                prevXStatus = gamepad2.x;
            }

            // --- Back Right (Y) ---
            if (gamepad2.y != prevYStatus) {
                if (!gamepad2.y) {
                    br = !br;
                    drive.setRearRightPower(br ? 1 : 0);
                }
                prevYStatus = gamepad2.y;
            }

            // Optional: telemetry feedback
            telemetry.addData("Front Left", fl ? "ON" : "OFF");
            telemetry.addData("Front Right", fr ? "ON" : "OFF");
            telemetry.addData("Back Left", bl ? "ON" : "OFF");
            telemetry.addData("Back Right", br ? "ON" : "OFF");
            telemetry.update();
        }
    }
}
