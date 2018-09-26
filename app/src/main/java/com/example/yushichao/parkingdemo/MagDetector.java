package com.example.yushichao.parkingdemo;

import android.util.Log;

/**
 * Created by LiteShare on 2018/9/21.
 */

public class MagDetector {

    public float Mag;
    public float speed;

    private int Num=100;
    private float[] Mags;
    private int count;

    private int speed_num=100;
    private float[] speed_value;

    private MagDetectorCallback callback;

    public interface MagDetectorCallback{
        void MagState(float var,float speed);
    }

    public MagDetector(MagDetectorCallback callback){
        Mags=new float[Num];
        speed_value=new float[speed_num];
        count=0;
        speed=0;
        this.callback=callback;
    }

    public void refreshMag(float[] mags){
        if(Mags.length==0){
            return;
        }

        Mags[count]=(float)Math.sqrt(mags[0]*mags[0]+mags[1]*mags[1]+mags[2]*mags[2]);
        speed_value[count%speed_num]=Mags[count];
        Mag=Mags[count];

        if (count%speed_num==speed_num-1){
            detectorState();
        }

        if (++count==Num){
            count=0;
        }
    }

    private void detectorState(){
        float[] diff=new float[speed_num-1];

        for (int i=0;i<diff.length;i++){
            diff[i]=speed_value[i+1]-speed_value[i];
        }

        float ave=0;
        float var=0;

        for (int i=0;i<diff.length;i++){
            ave+=diff[i];
        }

        ave/=diff.length;

        for (int i=0;i<diff.length;i++){
            var+=(float)Math.sqrt((diff[i]-ave)*(diff[i]-ave)) ;
        }

        var/=diff.length;
        speedEstimate(var);
    }

    private void speedEstimate(float var){
         callback.MagState(var,speed);
    }
}
