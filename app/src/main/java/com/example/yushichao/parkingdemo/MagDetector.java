package com.example.yushichao.parkingdemo;

import java.util.Timer;
import java.util.TimerTask;

public class MagDetector {

    public float Mag;

    private int speed_n=5;
    private int speed_count=0;
    private float[] speed;

    private int Num=100;
    private float[] Mags;
    private int count;

    private int speed_num=1000/40;
    private float[] speed_value;

    private Timer timer;
    private TimerTask task;

    private MyFile file;

    private MagDetectorCallback callback;

    public interface MagDetectorCallback{
        void MagState(float var,float speed);
    }

    public MagDetector(MagDetectorCallback callback){
        Mags=new float[Num];
        speed_value=new float[speed_num];
        count=0;
        speed=new float[speed_n];
        this.callback=callback;

        timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                calculateMag(Mag);
            }
        };
        timer.schedule(task,100,40);
    }

    public void refreshMag(float[] mags){
        Mag=(float)Math.sqrt(mags[0]*mags[0]+mags[1]*mags[1]+mags[2]*mags[2]);
    }

    public void saveData(String path){//path=context.getExternalFilesDir(null)
        file=new MyFile(path,"Mag.txt");
        file.CreateFile();
    }

    private void calculateMag(float mag){
        if(Mags.length==0){
            return;
        }

        Mags[count]=mag;
        speed_value[count%speed_num]=Mags[count];

        if (file!=null){
            file.WriteIntoFile(mag+"");
        }

        if (count%speed_num==speed_num-1){
            detectorState();
        }

        if (++count==Num) {
            count = 0;
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

        int x=(int)(var/0.1);

        if (x<=5){
            speed[speed_count]=0;
        }else{
            speed[speed_count]=var*2f;
        }

        if(++speed_count==speed_n){
            speed_count=0;
        }

        float ave=0;
        for (int i=0;i<speed.length;i++){
            ave+=speed[i];
        }

        callback.MagState(var,ave/speed.length);
    }
}
