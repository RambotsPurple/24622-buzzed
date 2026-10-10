package org.firstinspires.ftc.teamcode.config.util;
import com.seattlesolvers.solverslib.util.InterpLUT;

public class lut {

    //Init the Look up table
    protected final InterpLUT interpLut = new InterpLUT();

    /**
     * Points are in (x,y) such that
     * x = distance
     * y = velocity
     * Record at min 8 points around the field at varying distance
     * trying to succesfully get in the hive
     */

    public lut() {
        //please replace this is example
        interpLut.add(1.0, 2.0);


        interpLut.createLUT();
    }

    public double getVelocity(double distance) {
        return interpLut.get(distance);
    }
}
