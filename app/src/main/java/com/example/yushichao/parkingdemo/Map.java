package com.example.yushichao.parkingdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

public class Map extends View{

    private float MapRadio=0;

    private List<Line> lines;
    private List<Lamp> lamps;
    private String text;
    private float angle;
    private int X;
    private int Y;

    public Map(Context context) {
        super(context);
    }

    public Map(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Map(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLine(List<Line> list){
        this.lines=list;
    }

    public void setLamp(List<Lamp> list){
        this.lamps=list;
    }

    public void setText(String text){
        this.text=text;
    }

    public void setPosition(int x,int y){
        this.X=x;
        this.Y=y;
        invalidate();
    }

    public void setAngle(float angle){
        this.angle=(float) Math.PI*angle/180;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (MapRadio==0){
            MapRadio=((float) canvas.getHeight()*canvas.getWidth())/1000000;
        }

        DrawLamp(canvas);
        DrawLine(canvas);
        DrewText(canvas);
        DrawPosition(canvas);
    }

    private void DrawPosition(Canvas canvas){
        if (X!=0&&Y!=0){
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(5);

            canvas.drawCircle(MapRadio*X,MapRadio*Y,50,paint);

            int x=(int)(X+80*Math.sin(angle)/MapRadio);
            int y=(int)(Y-80*Math.cos(angle)/MapRadio);

            canvas.drawLine(MapRadio*X,MapRadio*Y,MapRadio*x,MapRadio*y,paint);
        }else {
            Log.e("MapError","No Position");
        }
    }

    private void DrawLamp(Canvas canvas){
        if (lamps!=null&&lamps.size()>0){
            Paint paint = new Paint();
            paint.setColor(Color.YELLOW);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAlpha(200);

            for (Lamp lamp:lamps){
                canvas.drawCircle(MapRadio*lamp.x,MapRadio*lamp.y,20,paint);
            }
        }else{
            Log.e("MapError","No Lamp");
        }
    }

    private void DrawLine(Canvas canvas){
        if (lines!=null&&lines.size()>0){
            Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(5);
            paint.setStyle(Paint.Style.STROKE);

            for (Line line:lines){
                canvas.drawLine(MapRadio*line.fromx,MapRadio*line.fromy,
                        MapRadio*line.tox,MapRadio*line.toy,paint);
            }
        }else{
            Log.e("MapError","No Line");
        }
    }

    private void DrewText(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(100);
        paint.setAlpha(100);
        canvas.drawText(text,0,100,paint);
    }
}
