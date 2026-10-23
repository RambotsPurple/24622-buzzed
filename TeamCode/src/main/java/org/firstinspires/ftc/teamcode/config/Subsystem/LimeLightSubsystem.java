package org.firstinspires.ftc.teamcode.config.Subsystem;

import static org.firstinspires.ftc.teamcode.config.util.Pipeline.COLOR;

import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLResult;
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

    public  LimeLightSubsystem(HardwareMap hw, Alliance alliance) {
        limelight = hw.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        this.alliance = alliance == Alliance.BLUE?0:1;
        limelight.pipelineSwitch(this.alliance);
    } //end of constructor

    public void lStart(){
        limelight.start();
    }//end of lStart

    public double getDistTag() {
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
            tagDist = (tag.getCameraPoseTargetSpace().getPosition().z/ DistanceUnit.mmPerInch);
        }//end of if
        return tagDist;

    }//end getDist

    // Null means no valid tag measurement is available.
    public Double getVisibleTagHorizontalError() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return null;
        }
        for (FiducialResult tag : result.getFiducialResults()) {
            if (tag != null) {
                return tag.getTargetXDegrees();
            }
        }
        return null;
    }

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

    public double getHorizontalErrorColor(){
        double colorYaw = 0;
        List<ColorResult> results = limelight.getLatestResult().getColorResults();
        for (ColorResult cr : results) {
            if(cr !=null){
                colorYaw = cr.getTargetXDegrees();
            }
        }return colorYaw;
    }
    public double getColorDist(){
        double colorDist = 0;
        List<ColorResult> results = limelight.getLatestResult().getColorResults();
        for (ColorResult cr : results) {
            if(cr !=null){
                colorDist = cr.getTargetPoseCameraSpace().getPosition().z/DistanceUnit.mmPerInch;
            }
        }return colorDist;
    }
    public void callPipeline(Pipeline pipeline){limelight.pipelineSwitch(pipeline == COLOR? 3:this.alliance );}





}//end of LimeLightSubsystem
