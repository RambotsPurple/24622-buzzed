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
    private final Pose toParkStart;
    private final Pose toPark;
    public eight(Alliance alliance) {
        PoseFactory poseFactory = AlliancePoseFactory.forAlliance(alliance);
        start = poseFactory.of(56, 8, 90);
        toBalls = poseFactory.of(9.687, 8.2549, 260);
        toBallsControl1 = poseFactory.of(23.7968, 26.0777, 0);
        toNextHiveStart = poseFactory.of(9.687, 8.2549, 260);
        toNextHive = poseFactory.of(60.8039, 128.3922, 270);
        toNextHiveControl1 = poseFactory.of(31.48, 97.1479, 0);
        toNextHiveControl2 = poseFactory.of(41.4668, 120.325, 0);
        toParkStart = poseFactory.of(60.8039, 128.3922, 270);
        toPark = poseFactory.of(19.6373, 116.8175, 90);
    }

    public Pose startPose() {
        return start;
    }

    public Path toBalls() {
        return Paths.curve(start, toBallsControl1, toBalls).linear(start, toBalls);
    }

    public Path toNextHive() {
        return Paths.curve(toNextHiveStart, toNextHiveControl1, toNextHiveControl2, toNextHive).linear(toNextHiveStart, toNextHive);
    }

    public Path toPark() {
        return Paths.line(toParkStart, toPark).linear(toParkStart, toPark);
    }
}