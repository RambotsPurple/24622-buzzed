package org.firstinspires.ftc.teamcode.config.util;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

import com.qualcomm.robotcore.hardware.PIDFCoefficients;


public class constants {
    public static final double[] redRight= new double[]{58,58};
    public static final double[] redLeft= new double[]{58,84};
    public static final double[] BlueRight= new double[]{82,58};
    public static final double[] BlueLeft= new double[]{84,84};
    public static final PIDFCoefficients flyWheelPIDF = new PIDFCoefficients(0.00082, 0, 0, .000275);

}

