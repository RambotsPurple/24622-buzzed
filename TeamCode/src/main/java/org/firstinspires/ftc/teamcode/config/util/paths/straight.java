package org.firstinspires.ftc.teamcode.config.util.paths;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.config.util.Alliance;

public class straight {
    public  Alliance a;
    private  PoseFactory poseFactory;
    public straight(Alliance alliance){
        a = alliance;
        if (a == Alliance.RED) {
            PoseFactory poseFactory = PoseFactory.degrees().mirrorX(70.75);
        } else {
            PoseFactory poseFactory = PoseFactory.degrees();
        }
    }

    private final Pose start = poseFactory.of(56, 8, 90);
    private final Pose path1 = poseFactory.of(8.6802, 94.2565, 90);

    public Path path1() {
        return Paths.line(start, path1).constant(path1);
    }
}
