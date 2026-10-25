package org.firstinspires.ftc.teamcode.config.util.paths;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;

/** Shared park route; blue mirrors the red poses and headings. */
public class straight {
    private final Pose start;
    private final Pose park;
    private final Pose parkControl;

    public straight(Alliance alliance) {
        PoseFactory poses = AlliancePoseFactory.forAlliance(alliance);
        start = poses.of(9, 33, 90);
        park = poses.of(8.6, 94, 180);
        parkControl = poses.of(40, 8, 0);
    }

    public Pose startPose() {
        return start;
    }

    public Path park() {
        return Paths.curve(start, parkControl, park).linear(start, park);
    }
}