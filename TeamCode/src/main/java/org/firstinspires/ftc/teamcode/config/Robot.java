package org.firstinspires.ftc.teamcode.config;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.button.Trigger;
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
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;
import java.util.List;

/** Shared hardware setup. Pedro is the only drivetrain owner in competition modes. */
public class Robot {
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
    }

    public Robot(HardwareMap hardwareMap, Alliance alliance, Gamepad driver,
                 Gamepad operator, Telemetry telemetry) {
        this(hardwareMap, alliance, telemetry);
        this.driver = new GamepadEx(driver);
        this.operator = new GamepadEx(operator);
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
        follower.manual(ManualDrive.fieldCentric(
                -driver.getLeftY(), -driver.getLeftX(), driver.getRightX(),
                follower.pose().heading()));
        follower.update();
        scheduler.run();
        updateTelemetry();
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
                new SetShooterVelocityCommand(shooterSubsystem, 4200),
                new SetShooterVelocityCommand(shooterSubsystem, 0));
        operator.getGamepadButton(GamepadKeys.Button.B).toggleWhenActive(
                new SetShooterVelocityCommand(shooterSubsystem, 2600),
                new SetShooterVelocityCommand(shooterSubsystem, 0));

        driver.getGamepadButton(GamepadKeys.Button.DPAD_UP).whenActive(
                new InstantCommand(() -> follower.setPose(follower.pose().withHeading(0))));
    }

    public void updateTelemetry() {
        telemetry.addData("Alliance", alliance);
        telemetry.addData("Pose", follower.pose());
        telemetry.addData("Shooter RPM (actual / target)", "%.0f / %.0f",
                shooterSubsystem.getVelocity(), shooterSubsystem.getTarget());
        telemetry.addData("Loop (ms)", "%.1f", loopTimer.milliseconds());
        loopTimer.reset();
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
