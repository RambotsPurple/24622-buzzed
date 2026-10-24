package org.firstinspires.ftc.teamcode.config.util.paths;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.config.util.Alliance;

public class straightRED {
    public  Alliance a;
    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(9, 33, 90);
    private final Pose park = poseFactory.of(8.6, 94, 180);
    private final Pose parkControl1 = poseFactory.of(40, 8, 0);

    public Path park() {
        return Paths.curve(start, parkControl1, park).linear(start, park);
    }
}
