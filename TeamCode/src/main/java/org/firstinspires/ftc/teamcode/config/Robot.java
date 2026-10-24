package org.firstinspires.ftc.teamcode.config;

// import static org.firstinspires.ftc.teamcode.config.pedro.Tuning.follower;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.controller.PDController;
import com.seattlesolvers.solverslib.controller.PIDFController;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import com.pedropathing.follower.Follower;
import com.pedropathing.utils.Timer;
import com.qualcomm.hardware.lynx.LynxModule;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.config.Commands.Command.IntakeCommand;
import org.firstinspires.ftc.teamcode.config.Commands.Command.SetShooterVelocityCommand;
import org.firstinspires.ftc.teamcode.config.Commands.Command.ShooterCommand;
import org.firstinspires.ftc.teamcode.config.Commands.CommandGroups.OutputCommandGroup;
import org.firstinspires.ftc.teamcode.config.pedro.Constants;
import org.firstinspires.ftc.teamcode.config.util.*;
import org.firstinspires.ftc.teamcode.config.Subsystem.*;

import java.util.List;

public class Robot {

    private final List<LynxModule> allHubs;
    private final Timer loop = new Timer();
    private Follower follower;
    public Alliance alliance;
    public States state = States.MANUAL;
    public CommandScheduler cs = CommandScheduler.getInstance();
    public ElapsedTime loopTimer;
    protected GamepadEx driver;
    protected GamepadEx oper;

    public TransferSubsystem transferSubsystem;
    // Instantiate subsystems
    public ShooterSubsystem shooterSubsystem;
    public IntakeSubsystem intakeSubsystem;

    public DriveSubsystem driveSubsystem;

    //public ElapsedTime turnTimer;


    public LimeLightSubsystem limeLightSubsystem;
    private final Telemetry telemetry;

    private final PDController pd = new PDController(0.3,  0.024);




    public Follower getFollower(){
        return follower;
    } // end of getFollower


    /**
     * This constructor below is for a single player auton anywhere
     * of the field
     * @param h hardwaremap
     * @param alliance blue or red
     * @param driver driver gamepad
     * @param telemetry allows for telemetry output
     **/
    public Robot(HardwareMap h, Alliance alliance, Gamepad driver,Gamepad oper, Telemetry telemetry) {
        shooterSubsystem = new ShooterSubsystem(h,telemetry);
        intakeSubsystem = new IntakeSubsystem(h);
        transferSubsystem = new TransferSubsystem(h);
//        limeLightSubsystem = new LimeLightSubsystem(h,alliance);
        driveSubsystem  = new DriveSubsystem(h);
        follower =  Constants.create(h);
        this.alliance = alliance;
        this.driver = new GamepadEx(driver);
        this.oper = new GamepadEx(oper);
        this.telemetry = telemetry;
        loopTimer = new ElapsedTime();

        allHubs = h.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }//end of for

        loop.reset();
        cs.registerSubsystem(
                shooterSubsystem, intakeSubsystem, transferSubsystem
        );

    } //end of teleop constructor

    /**
     * The constructor below is for auto on any side of the field
     *
     * @param h
     * @param alliance
     * @param telemetry
     */

    public Robot(HardwareMap h, Alliance alliance, Telemetry telemetry) {
        transferSubsystem = new TransferSubsystem(h);
        shooterSubsystem = new ShooterSubsystem(h, telemetry);
        intakeSubsystem = new IntakeSubsystem(h);
//        limeLightSubsystem = new LimeLightSubsystem(h, alliance);
        loopTimer = new ElapsedTime();
        driveSubsystem = new DriveSubsystem(h);
        this.alliance = alliance;
        this.telemetry = telemetry;

        // instaniate the lynx mod
        allHubs = h.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }//end of for

        cs.registerSubsystem(
                shooterSubsystem, intakeSubsystem, transferSubsystem
        ); // end of cs

    }//end of auto constructor

    /**
     * loops periodically during teleOp
     */
    public void tPeriodic() {
        bulk();
        follower.manual(powers());
        follower.update();
        //driveSubsystem.drive(-driver.getLeftX(), -driver.getLeftY(),rot());
        teleTelemetry();
        telemetry.update();
        cs.run();

    } //end of periodic

    public DrivePowers powers(){
        return
                ManualDrive.fieldCentric(
                        driver.getLeftY(), -driver.getLeftX(),
                rot(),
                follower.pose().heading()
        );
    }

    /**
     * Run on start of teleOp
     */
    public void tStart() {
        //turnTimer = new ElapsedTime();
        //limeLightSubsystem.lStart();
    } // end of tStart

    public void bulk(){
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    public double rot(){
        double turn = driver.getRightX();
        //if (state == States.LOCKED && targetVisible) {
        //    turn = Range.clip(
        //            // ll sub is just place holdfer
        //            pd.calculate(limeLightSubsystem.getHorizontalErrorTag(), 0),
        //            -3,
        //            3            );
        //} else {
        //    pd.reset();
        //}
        return turn;
    }


    /**
     * Sets up listeners at the start of teleOp
     */
    public void tele() {

        // event listeners
        // all input goes here except for driving which is passed in periodically
        // --------------------

        /*
         * right trigger - hold: intake
         * right bumper - toggle: indexer
         * left bumper - toggle: Locked state so LLS take's over turn
         * A - activate shooter
         * B - deactivate shooter
         */

        // code code just need to make the method @mitrha
        new Trigger(() -> driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0)
                .whenActive(new IntakeCommand(intakeSubsystem,1))
                .whenInactive(new IntakeCommand(intakeSubsystem, 0));

        new Trigger(() -> driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0)
                .whenActive(new IntakeCommand(intakeSubsystem,-1))
                .whenInactive(new IntakeCommand(intakeSubsystem, 0));

        // to be changed

        oper.getGamepadButton(GamepadKeys.Button.X).toggleWhenActive(
                new SetShooterVelocityCommand(shooterSubsystem, 0)
        );
        new Trigger(() -> oper.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0)
                .whenActive(new OutputCommandGroup(intakeSubsystem,transferSubsystem,1))
                .whenInactive(new OutputCommandGroup(intakeSubsystem,transferSubsystem,0));

        new Trigger(() -> oper.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0)
                .whenActive(new OutputCommandGroup(intakeSubsystem,transferSubsystem,-0.8))
                .whenInactive(new OutputCommandGroup(intakeSubsystem,transferSubsystem,0));

        oper.getGamepadButton((GamepadKeys.Button.A))
                .toggleWhenActive(new SetShooterVelocityCommand(shooterSubsystem, 5000),new SetShooterVelocityCommand(shooterSubsystem, 0));

        oper.getGamepadButton((GamepadKeys.Button.B))
                .toggleWhenActive(new SetShooterVelocityCommand(shooterSubsystem, 4000),new SetShooterVelocityCommand(shooterSubsystem, 0));

//        oper.getGamepadButton((GamepadKeys.Button.A))
//                .toggleWhenActive(new ShooterCommand(shooterSubsystem, 1),new ShooterCommand(shooterSubsystem, 0));
//
//        oper.getGamepadButton((GamepadKeys.Button.B))
//                .toggleWhenActive(new ShooterCommand(shooterSubsystem, .8),new ShooterCommand(shooterSubsystem, 0));

        driver.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenActive(new InstantCommand(() -> {
                    state = state == States.MANUAL ? States.LOCKED : States.MANUAL;
                    pd.reset();
                }));


        //driver.getGamepadButton(GamepadKeys.Button.DPAD_LEFT).whenActive(new InstantCommand(driveSubsystem::resetAngle));
        driver.getGamepadButton(GamepadKeys.Button.DPAD_LEFT).whenActive(new InstantCommand(driveSubsystem::resetAngle));


    } //end of tele method



    /**
     * Telemetry data for teleOp
     */
    public void teleTelemetryDebug() {
        teleTelemetry();
    }

    public void teleTelemetry() {
        double loopTime = loopTimer.milliseconds();
        loopTimer.reset();

        telemetry.addData("Alliance", alliance);
        telemetry.addData("Drive state", state);
        //Double aimError = limeLightSubsystem.getVisibleTagHorizontalError();
        //telemetry.addData("Tag visible", aimError != null);
        //if (aimError != null) {
        //    telemetry.addData("Aim error (deg)", "%.1f", aimError);
        //}
        telemetry.addData("Shooter RPM (actual / target)", "%.0f / %.0f",
                shooterSubsystem.getVelocity(), shooterSubsystem.getTarget());
        telemetry.addData("Loop (ms)", "%.1f", loopTime);
    }

    public void autoTelemetry() {
        teleTelemetry();
    }
    public Alliance getAlliance(){
        return this.alliance;
    }


} //end of Robot
