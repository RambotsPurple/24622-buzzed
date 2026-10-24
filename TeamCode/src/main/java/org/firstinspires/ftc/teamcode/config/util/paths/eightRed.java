package org.firstinspires.ftc.teamcode.config.util.paths;
import static com.pedropathing.api.Paths.*;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.config.util.Alliance;

public class eightRed {
    public  Alliance a;
    private final  PoseFactory poseFactory = PoseFactory.degrees();
    private final Pose         start = poseFactory.of(56, 8, 0);
    private final Pose         toballs = poseFactory.of(10.9676, 11.0294, 0);
    ;
    private final Pose         toballsControl1 = poseFactory.of(56.664, 16.4737, 0);
    ;


    private final Pose tostartStart = poseFactory.of(10.9676, 11.0294, 0);
    private final Pose tostart = poseFactory.of(56, 9, 0);
    private final Pose tostartControl1 = poseFactory.of(56.7745, 15.9434, 0);
    private final Pose topark = poseFactory.of(8.632, 93.5814, 0);

//
//    private final Pose         start = poseFactory.of(56, 8, 90);
//    private final Pose         toballs = poseFactory.of(10.9676, 11.0294, 260);
//    ;
//    private final Pose         toballsControl1 = poseFactory.of(56.664, 16.4737, 0);
//    ;
//
//
//    private final Pose tostartStart = poseFactory.of(10.9676, 11.0294, 260);
//    private final Pose tostart = poseFactory.of(56, 9, 90);
//    private final Pose tostartControl1 = poseFactory.of(56.7745, 15.9434, 0);
//    private final Pose topark = poseFactory.of(8.632, 93.5814, 90);

    public Path toballs() {
        return Paths.curve(start, toballsControl1, toballs).linear(start, toballs);
    }

    public Path tostart() {
        return Paths.curve(tostartStart, tostartControl1, tostart).linear(tostartStart, tostart);
    }

    public Path topark() {
        return Paths.line(tostart, topark).constant(topark);
    }
}
