package org.firstinspires.ftc.teamcode.config;

import android.widget.Switch;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
import com.seattlesolvers.solverslib.controller.PDController;
import com.seattlesolvers.solverslib.controller.PIDFController;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.config.Commands.Command.IntakeCommand;
import org.firstinspires.ftc.teamcode.config.Commands.Command.SetShooterVelocityCommand;
import org.firstinspires.ftc.teamcode.config.Commands.CommandGroups.OutputCommandGroup;
import org.firstinspires.ftc.teamcode.config.Subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.config.Subsystem.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.config.Subsystem.TransferSubsystem;
import org.firstinspires.ftc.teamcode.config.pedro.Constants;
import org.firstinspires.ftc.teamcode.config.util.constants;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;
import org.firstinspires.ftc.teamcode.config.util.enums.States;
import java.util.List;


@Configurable
public class Robot {
    public static double turnP = 0.00002;
    public static double turnD = 0.001;

    public final ShooterSubsystem shooterSubsystem;
    public final IntakeSubsystem intakeSubsystem;
    public final TransferSubsystem transferSubsystem;

    private final Follower follower;
    private final Alliance alliance;
    private final List<LynxModule> allHubs;
    private final Telemetry telemetry;
    private final ElapsedTime loopTimer = new ElapsedTime();
    private final CommandScheduler scheduler = CommandScheduler.getInstance();
    private GamepadEx driver;
    private GamepadEx operator;
    private final PDController turnPD = new PDController(turnP, turnD);
    private final TelemetryManager panels;
    private  double targTelem =0.0;

    protected States state;


    public Robot(HardwareMap hardwareMap, Alliance alliance, Telemetry telemetry) {
        this.alliance = alliance;
        this.telemetry = telemetry;
        allHubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
        bulk();

        follower = Constants.create(hardwareMap);
        shooterSubsystem = new ShooterSubsystem(hardwareMap);
        intakeSubsystem = new IntakeSubsystem(hardwareMap);
        transferSubsystem = new TransferSubsystem(hardwareMap);
        scheduler.registerSubsystem(shooterSubsystem, intakeSubsystem, transferSubsystem);
         panels = PanelsTelemetry.INSTANCE.getTelemetry();
    }

    public Robot(HardwareMap hardwareMap, Alliance alliance, Gamepad driver,
                 Gamepad operator, Telemetry telemetry) {

        this(hardwareMap, alliance, telemetry);
        this.driver = new GamepadEx(driver);
        this.operator = new GamepadEx(operator);

        // IN HOUSE SCRIMMAGE START POSES IF AUTO IS CRAP

        // BLUE SIDE \\
        // follower.setPose(new Pose(83,130,0)); //right side
        // follower.setPose(new Pose(83,11,0)); //left side

        // Red SIDE \\
        // follower.setPose(new Pose(56,130,0)); //Left side
        follower.setPose(new Pose(56,11,0)); //Right side

        setState(States.MANUAL);
    }

    private void setState(States states) {
        this.state = states;
    }

    public Follower getFollower() {
        return follower;
    }

    public Alliance getAlliance() {
        return alliance;
    }

    public void bulk() {
        for (LynxModule hub : allHubs) {
            hub.clearBulkCache();
        }
    }

    public void tStart() {
        loopTimer.reset();
    }

    /** Teleop owns its scheduler loop; autonomous uses CommandOpMode's scheduler. */
    public void tPeriodic() {
        bulk();
        drive();
        scheduler.run();
        updateTelemetry();
    }
    // adding a auto lock with odom,later will introduce LL EKF fusion
    public void drive(){

        double rot = switch (state) {
            case LOCKED -> turnTo();
            case MANUAL -> driver.getRightX();
        };

        follower.manual(ManualDrive.fieldCentric(
                driver.getLeftY(), -driver.getLeftX(),rot,
                follower.pose().heading()));
        follower.update();
    }
    public double turnTo(){
        turnPD.setP(turnP);
        turnPD.setD(turnD);
        double targ;
        double heading = Math.toDegrees(follower.pose().heading());
        double x = follower.pose().x() ;
        double y = follower.pose().y();
        boolean middleOver = y>71;

        // math logic basic trig lit pythag
        if(middleOver){
            double eX;
            double eY;
            if(alliance.equals(Alliance.BLUE)){
                eX = constants.BlueLeft[0] - x;
                eY = constants.BlueLeft[1] - y;
            }else{
                eX = constants.redLeft[0] - x;
                eY = constants.redLeft[1] - y;
            }
        }else{
            double eX;
            double eY;
            if(alliance.equals(Alliance.BLUE)){
                eX = constants.BlueRight[0] - x;
                eY = constants.BlueRight[1] - y;
            }else{
                eX = constants.redRight[0] - x;
                eY = constants.redRight[1] - y;
            }
        }//end of if/else
        targ = Math.toDegrees(Math.atan2(y,x));
        targ = (targ < 0)? targ +360: targ; //this line looks very weird IK
        targTelem = targ;
        // will need to tune
        if((heading > targ+4) || (heading < targ-4)){
            double power = turnPD.calculate(heading, targ);
            return Range.clip(power, -1.0, 1.0);
        }//end of if

        return 0;
    }


    /** Bind once during teleop initialization. */
    public void tele() {

        new Trigger(() -> driver.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0)
                .whenActive(new IntakeCommand(intakeSubsystem, 1))
                .whenInactive(new IntakeCommand(intakeSubsystem, 0));
        new Trigger(() -> driver.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0)
                .whenActive(new IntakeCommand(intakeSubsystem, -1))
                .whenInactive(new IntakeCommand(intakeSubsystem, 0));

        operator.getGamepadButton(GamepadKeys.Button.X)
                .whenActive(new SetShooterVelocityCommand(shooterSubsystem, 0));
        new Trigger(() -> operator.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER) > 0)
                .whenActive(new OutputCommandGroup(intakeSubsystem, transferSubsystem, 1))
                .whenInactive(new OutputCommandGroup(intakeSubsystem, transferSubsystem, 0));
        new Trigger(() -> operator.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) > 0)
                .whenActive(new OutputCommandGroup(intakeSubsystem, transferSubsystem, -0.8))
                .whenInactive(new OutputCommandGroup(intakeSubsystem, transferSubsystem, 0));

        operator.getGamepadButton(GamepadKeys.Button.A).toggleWhenActive(
                new SetShooterVelocityCommand(shooterSubsystem, 2900),
                new SetShooterVelocityCommand(shooterSubsystem, 0));
        operator.getGamepadButton(GamepadKeys.Button.B).toggleWhenActive(
                new SetShooterVelocityCommand(shooterSubsystem, 2650),
                new SetShooterVelocityCommand(shooterSubsystem, 0));
        // idle speed
        operator.getGamepadButton(GamepadKeys.Button.X).toggleWhenActive(
                new SetShooterVelocityCommand(shooterSubsystem, 1000),
                new SetShooterVelocityCommand(shooterSubsystem, 0));

        driver.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenActive(
                new InstantCommand(() -> follower.setPose(follower.pose().withHeading(0))));

        driver.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER).toggleWhenActive(
                new InstantCommand(() -> setState(States.LOCKED)),
                new InstantCommand(() -> setState(States.MANUAL))
        );


    }


    public void updateTelemetry() {
        panels.addData("heading", Math.toDegrees(follower.pose().heading()));
        panels.addData("target", targTelem);
        telemetry.addData("target", targTelem);
        telemetry.addData("Alliance", alliance);
        telemetry.addData("x", follower.pose().x());
        telemetry.addData("y", follower.pose().y());
        telemetry.addData("heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Shooter RPM (actual / target)", "%.0f / %.0f",
                shooterSubsystem.getVelocity(), shooterSubsystem.getTarget());
        telemetry.addData("Loop (ms)", "%.1f", loopTimer.milliseconds());
        loopTimer.reset();
        panels.update(telemetry);
        telemetry.update();
    }

    /** Call after cancelling commands so interrupted commands cannot restart motors. */
    public void stop() {
        follower.stop();
        shooterSubsystem.setVelocity(0);
        intakeSubsystem.stop();
        transferSubsystem.setPower(0);
        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }
}
