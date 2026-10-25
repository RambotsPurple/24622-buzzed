package org.firstinspires.ftc.teamcode.config.util.paths;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;

/** Shared eight route; blue mirrors the red poses and headings. */
public class eight {
    private final Pose start;
    private final Pose balls;
    private final Pose ballsControl;
    private final Pose returnPose;
    private final Pose returnControl;
    private final Pose park;

    public eight(Alliance alliance) {
        PoseFactory poses = AlliancePoseFactory.forAlliance(alliance);
        start = poses.of(56, 8, 90);
        balls = poses.of(10.9676, 11.0294, 260);
        ballsControl = poses.of(56.664, 16.4737, 0);
        returnPose = poses.of(56, 9, 90);
        returnControl = poses.of(56.7745, 15.9434, 0);
        park = poses.of(8.632, 93.5814, 90);
    }

    public Pose startPose() {
        return start;
    }

    public Path toballs() {
        return Paths.curve(start, ballsControl, balls).linear(start, balls);
    }

    public Path tostart() {
        return Paths.curve(balls, returnControl, returnPose).linear(balls, returnPose);
    }

    public Path topark() {
        return Paths.line(returnPose, park).constant(park);
    }
}