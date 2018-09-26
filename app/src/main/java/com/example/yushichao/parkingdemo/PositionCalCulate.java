package com.example.yushichao.parkingdemo;

import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yushi on 2018/9/26.
 */

public class PositionCalCulate {
    public int X;
    public int Y;

    private int lampId=1;

    private List<Line> lines;
    private List<Lamp> lamps;

    private PositionCallback callback;

    private float speed=0;
    private float angle=0;

    private Timer timer;
    private TimerTask task;

    public interface PositionCallback{
        void positionChanged(int x,int y);
    }

    public PositionCalCulate(final PositionCallback callback){
        this.callback=callback;

        task=new TimerTask() {
            @Override
            public void run() {
                calulatePosition();
                callback.positionChanged(X,Y);
            }
        };
        timer=new Timer();
    }

    public void setPosition(int x,int y){
        this.X=x;
        this.Y=y;
    }

    public void setTopo(List<Line> lines,List<Lamp> lamps){
        this.lines=lines;
        this.lamps=lamps;

        this.X=lamps.get(0).x;
        this.Y=lamps.get(0).y;
        this.lampId=1;
        this.callback.positionChanged(X,Y);
        this.timer.schedule(task,100,1000);
    }

    public void setLampId(int LampId){
        if(lamps==null){
            Log.e("PositionCalculateError","ErrorLampTopo");
        }

        if (lamps.size()>=LampId){
            this.X=lamps.get(LampId-1).x;
            this.Y=lamps.get(LampId-1).y;
            this.callback.positionChanged(this.X,this.Y);
        }else {
            Log.e("PositionCalculateError","ErrorLampId");
        }
    }

    public void setSpeed(float speed){
        this.speed=speed;
    }

    public void setAngle(float angle){
        this.angle=(float) Math.PI*angle/180;
    }

    private void calulatePosition(){
        if (speed>0){
            X+=speed*Math.sin(angle);
            Y-=speed*Math.cos(angle);
        }
    }
}
