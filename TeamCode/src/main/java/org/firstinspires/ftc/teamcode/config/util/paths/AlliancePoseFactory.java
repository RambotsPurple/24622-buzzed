package org.firstinspires.ftc.teamcode.config.util.paths;

import com.pedropathing.api.PoseFactory;
import org.firstinspires.ftc.teamcode.config.util.enums.Alliance;

/** Path coordinates are authored for red, in inches and degrees. */
public final class AlliancePoseFactory {
    public static final double MIRROR_X = 70.75;

    private AlliancePoseFactory() {}

    public static PoseFactory forAlliance(Alliance alliance) {
        if (alliance == null) {
            throw new IllegalArgumentException("Alliance is required");
        }
        PoseFactory factory = PoseFactory.degrees();
        return alliance == Alliance.BLUE ? factory.mirrorX(MIRROR_X) : factory;
    }
}