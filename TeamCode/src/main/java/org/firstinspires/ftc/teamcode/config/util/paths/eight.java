package org.firstinspires.ftc.teamcode.config.util.paths;
import static com.pedropathing.api.Paths.*;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.config.util.Alliance;

public class eight {
    public  Alliance a;
    private  PoseFactory poseFactory ;


    private final Pose start;
    private final Pose toballs ;
    private final Pose toballsControl1;
    public eight(Alliance alliance){
        a = alliance;
        if (a == Alliance.RED) {
             poseFactory = PoseFactory.degrees().mirrorX(70.75);
        } else {
             poseFactory = PoseFactory.degrees();
        }
        start = poseFactory.of(56, 8, 90);
        toballs = poseFactory.of(10.9676, 11.0294, 260);
        toballsControl1 = poseFactory.of(56.664, 16.4737, 0);
    }
    private final Pose tostartStart = poseFactory.of(10.9676, 11.0294, 260);
    private final Pose tostart = poseFactory.of(56, 9, 90);
    private final Pose tostartControl1 = poseFactory.of(56.7745, 15.9434, 0);
    private final Pose topark = poseFactory.of(8.632, 93.5814, 90);

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
