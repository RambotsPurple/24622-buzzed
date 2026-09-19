package org.firstinspires.ftc.teamcode.config.Subsystem;

import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLResultTypes.*;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.config.util.Alliance;
import org.firstinspires.ftc.teamcode.config.util.Pipeline;

import java.util.List;

public class LimeLightSubsystem {
    private final Limelight3A limelight;
    private int alliance;
    private int pipeline;
    public  LimeLightSubsystem(HardwareMap hw, Alliance alliance, Pipeline pipeline) {
        limelight = hw.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        this.alliance = alliance == Alliance.BLUE?0:1;
        this.pipeline =
        limelight.pipelineSwitch(this.alliance);
    } //end of constructor

    public void lStart(){
        limelight.start();
    }//end of lStart

    public double getDistTag() {
        switch(pipeline){
            case COLOR:
                break;
            Default:

        }
        FiducialResult tag = null;
        double tagDist = 0;
        List<FiducialResult> results = limelight.getLatestResult().getFiducialResults();

        if (results.isEmpty()) return 0;

        for (FiducialResult fiducial : results) {
            if (fiducial!=null){
                tag = fiducial;
                break;
            }//end of if
        }//end of for

        if(tag !=null){
            tagDist = (tag.getCameraPoseTargetSpace().getPosition().z/ DistanceUnit.mPerInch);
        }//end of if
        return tagDist;

    }//end getDist

    public double getHorizontalErrorTag(){
        FiducialResult tag = null;
        double tagYaw = 0;
        List<FiducialResult> results = limelight.getLatestResult().getFiducialResults();

        if (results.isEmpty()) return 0;

        for (FiducialResult fiducial : results) {
            if (fiducial != null ){
                tag = fiducial;
                break;
            }//end of if
        }//end of for

        if(tag !=null){
            tagYaw = tag.getTargetXDegrees();
        }//end of if

        return tagYaw;
    } //end of getHoriError




}//end of LimeLightSubsystem
