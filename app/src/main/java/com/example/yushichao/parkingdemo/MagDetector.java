package com.example.yushichao.parkingdemo;

import java.util.Timer;
import java.util.TimerTask;

public class MagDetector {

    public float mag;
    private float[] values;
    private int count=0;


    private int var_num=100;

    private float[] speeds=new float[100];
    private int speed_count=0;

    private Timer timer;
    private TimerTask task;

    private MyFile file;

    private MagDetectorCallback callback;

    public float ind=50f;

    public interface MagDetectorCallback{
        void MagState(float var,float speed);
    }

    public MagDetector(MagDetectorCallback callback){
        values=new float[1000];

        this.callback=callback;

        timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                calculateMag(mag);
            }
        };
        timer.schedule(task,100,20);
    }

    public void refreshMag(float[] mags){
        mag=(float)Math.sqrt(mags[0]*mags[0]+mags[1]*mags[1]+mags[2]*mags[2]);
    }

    public void saveData(String path){//path=context.getExternalFilesDir(null)
        file=new MyFile(path,"Mag.txt");
        file.CreateFile();
    }

    private void calculateMag(float mag){
        if(mag==0){
            return;
        }

        values[count]=mag;

        float[] data=new float[values.length];

        for(int i=0,j=count+1;i<values.length;i++,j++){
            if(j==values.length){
                j=0;
            }
            data[i]=values[j];
        }

        data=Utils.smoothFilter(data,50);

        float[] data2=Utils.diff(data);

        data2=Utils.smoothFilter(data2,50);

        float[] data3=new float[var_num];

        for (int i=0;i<data3.length;i++){
            data3[i]=data2[data2.length-i-1];
        }

        float ave=0;
        float var=0;
        for(int i=0;i<data3.length;i++){
            ave+=data3[i];
        }
        ave/=data3.length;

        for(int i=0;i<data3.length;i++){
            var+=(data3[i]-ave)*(data3[i]-ave);
        }

        var=(float) Math.sqrt(var/data3.length);

        speedCalculate(var);

        if (++count==values.length){
            count=0;
        }
    }

    private void speedCalculate(float var){
        speeds[speed_count]=var*ind;

        float[] data=Utils.smoothFilter(speeds,20);

        if (callback!=null) {
            callback.MagState(var, data[speed_count]);
        }

        if (++speed_count==speeds.length){
            speed_count=0;
        }
    }
}
