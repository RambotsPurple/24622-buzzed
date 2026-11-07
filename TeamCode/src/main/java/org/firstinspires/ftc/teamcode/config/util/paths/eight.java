package org.firstinspires.ftc.teamcode.config.util.paths;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;

/** Shared eight route; blue mirrors the red poses and headings. */
public class eight {
    private final Pose start;
    private final Pose toBalls;
    private final Pose toBallsControl1;
    private final Pose toNextHiveStart;
    private final Pose toNextHive;
    private final Pose toNextHiveControl1;
    private final Pose toNextHiveControl2;
    private final Pose toNextHiveControl3;
    private final Pose toParkStart;
    private final Pose toPark;
    public eight(Alliance alliance) {
        PoseFactory poseFactory = AlliancePoseFactory.forAlliance(alliance);
        start = poseFactory.of(56, 4, 90);
        toBalls = poseFactory.of(9.4179, 12, 260);
        toBallsControl1 = poseFactory.of(36.8846, 33.4442, 0);
        toNextHiveStart =poseFactory.of(9.4179, 9.7852, 260);
        toNextHive = poseFactory.of(60.8039, 115.3922, 270);
        toNextHiveControl1 = poseFactory.of(39.3966, 33.7479, 0);
        toNextHiveControl2 = poseFactory.of(16.0581, 119.546, 0);
        toNextHiveControl3 = poseFactory.of(39.5554, 108.13, 0);
        toParkStart = poseFactory.of(61.4383, 131.5, 270);
        toPark = poseFactory.of(18.6642, 105.323, 0);
    }

    public Pose startPose() {
        return start;
    }

    public Path toBalls() {
        return Paths.curve(start, toBallsControl1, toBalls).linear(start, toBalls);
    }

    public Path toNextHive() {
        return Paths.curve(toNextHiveStart, toNextHiveControl1, toNextHiveControl2, toNextHiveControl3, toNextHive).linear(toNextHiveStart, toNextHive);
    }

    public Path toPark() {
        return Paths.line(toParkStart, toPark).linear(toParkStart, toPark);
    }
}