package com.example.yushichao.parkingdemo;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yushi on 2018/9/19.
 */

public class Utils {
    private static final int Indx=300;
    private static final int Indy=300;

    public static List<Line> getLine(){
        List<Line> lines=new ArrayList<>();

        Line line1=new Line(266,345,266,957);
        lines.add(line1);

        Line line2=new Line(line1.tox,line1.toy,428,957);
        lines.add(line2);

        Line line3=new Line(line1.fromx,line1.fromy,428,345);
        lines.add(line3);

        Line line4=new Line(line3.tox,line3.toy,line2.tox,line2.toy);
        lines.add(line4);

        Line line5=new Line(line4.tox,line4.toy,588,957);
        lines.add(line5);

        Line line6=new Line(line5.tox,line5.toy,588,1514);
        lines.add(line6);

        Line line7=new Line(267,1514,line6.tox,line6.toy);
        lines.add(line7);

        Line line8=new Line(118,1514,line7.tox,line7.toy);
        lines.add(line8);

        Line line9=new Line(line8.fromx,line8.fromy,118,2129);
        lines.add(line9);

        Line line10=new Line(line9.tox,line9.toy,267,2129);
        lines.add(line10);

        Line line11=new Line(line7.fromx,line7.fromy,267,1904);
        lines.add(line11);

        Line line12=new Line(line11.tox,line11.toy,362,1904);
        lines.add(line12);

        Line line13=new Line(line11.tox,line11.toy,line10.tox,line10.toy);
        lines.add(line13);

        for(Line line:lines){
            line.fromx+=Indx;
            line.fromy-=Indy;
            line.tox+=Indx;
            line.toy-=Indy;
        }

        return lines;
    }

    public static List<Lamp> getLamp(){
        List<Lamp> lamps=new ArrayList<>();

        Lamp lamp1=new Lamp(118,1840);
        lamps.add(lamp1);

        Lamp lamp2=new Lamp(267,2095);
        lamps.add(lamp2);

        Lamp lamp3=new Lamp(267,1913);
        lamps.add(lamp3);

        Lamp lamp4=new Lamp(267,1675);
        lamps.add(lamp4);

        Lamp lamp5=new Lamp(171,1514);
        lamps.add(lamp5);

        Lamp lamp6=new Lamp(118,1676);
        lamps.add(lamp6);

        Lamp lamp7=new Lamp(175,2129);
        lamps.add(lamp7);

        Lamp lamp8=new Lamp(118,2005);
        lamps.add(lamp8);

        Lamp lamp9=new Lamp(331,1514);
        lamps.add(lamp9);

        Lamp lamp10=new Lamp(451,1514);
        lamps.add(lamp10);

        Lamp lamp11=new Lamp(588,1447);
        lamps.add(lamp11);

        Lamp lamp12=new Lamp(588,1278);
        lamps.add(lamp12);

        Lamp lamp13=new Lamp(588,994);
        lamps.add(lamp13);

        Lamp lamp14=new Lamp(428,828);
        lamps.add(lamp14);

        Lamp lamp15=new Lamp(428,553);
        lamps.add(lamp15);

        Lamp lamp16=new Lamp(428,404);
        lamps.add(lamp16);

        Lamp lamp17=new Lamp(320,345);
        lamps.add(lamp17);

        Lamp lamp18=new Lamp(266,510);
        lamps.add(lamp18);

        Lamp lamp19=new Lamp(266,756);
        lamps.add(lamp19);

        Lamp lamp20=new Lamp(266,940);
        lamps.add(lamp20);

        Lamp lamp21=new Lamp(428,940);
        lamps.add(lamp21);

        for(Lamp lamp:lamps){
            lamp.x+=Indx;
            lamp.y-=Indy;
        }
        return lamps;
    }

    public static int getPeakNum(float[] data){
        int peak=0;
        data=smoothFilter(data,3);
        int[] PeakAndTrough=new int[data.length];

        //需要三个不同的值进行比较，取lo,mid，hi分别为三值
        for (int lo=0,mid=1,hi=2;hi<data.length;hi++){
            //先令data[lo]不等于data[mid]
            while (mid<data.length&&data[mid]==data[lo]){
                mid++;
            }

            hi=mid+1;

            //令data[hi]不等于data[mid]
            while (hi<data.length&&data[hi]==data[mid]){
                hi++;
            }

            if (hi>=data.length){
                break;
            }

            //检测是否为峰值
            if (data[mid]>data[lo]&&data[mid]>data[hi]){
                PeakAndTrough[mid]=1;       //1代表波峰
            }else if(data[mid]<data[lo]&&data[mid]<data[hi]){
                PeakAndTrough[mid]=-1;      //-1代表波谷
            }

            lo=mid;
            mid=hi;
        }

        //计算均值
        float ave=0;
        for (int i=0;i<data.length;i++){
            ave+=data[i];
        }
        ave/=data.length;

        //排除大于均值的波谷和小于均值的波峰
        for (int i=0;i<PeakAndTrough.length;i++){
            if ((PeakAndTrough[i]>0&&data[i]<ave)||(PeakAndTrough[i]<0&&data[i]>ave)){
                PeakAndTrough[i]=0;
            }
        }

        //统计波峰数量
        for (int i=0;i<PeakAndTrough.length;){
            while (i<PeakAndTrough.length&&PeakAndTrough[i]<=0){
                i++;
            }

            if (i>=PeakAndTrough.length){
                break;
            }

            peak++;

            while (i<PeakAndTrough.length&&PeakAndTrough[i]>=0){
                i++;
            }
        }

        return peak;
    }

    public static int[] getPeakNum2(float[] data){
        data=smoothFilter(data,5
        );
        int[] PeakAndTrough=new int[data.length];

        //需要三个不同的值进行比较，取lo,mid，hi分别为三值
        for (int lo=0,mid=1,hi=2;hi<data.length;hi++){
            //先令data[lo]不等于data[mid]
            while (mid<data.length&&data[mid]==data[lo]){
                mid++;
            }

            hi=mid+1;

            //令data[hi]不等于data[mid]
            while (hi<data.length&&data[hi]==data[mid]){
                hi++;
            }

            if (hi>=data.length){
                break;
            }

            //检测是否为峰值
            if (data[mid]>data[lo]&&data[mid]>data[hi]){
                PeakAndTrough[mid]=1;       //1代表波峰
            }else if(data[mid]<data[lo]&&data[mid]<data[hi]){
                PeakAndTrough[mid]=-1;      //-1代表波谷
            }

            lo=mid;
            mid=hi;
        }

        //计算均值
        float ave=0;
        for (int i=0;i<data.length;i++){
            ave+=data[i];
        }
        ave/=data.length;

        //排除大于均值的波谷和小于均值的波峰
        for (int i=0;i<PeakAndTrough.length;i++){
            if ((PeakAndTrough[i]>0&&data[i]<ave)||(PeakAndTrough[i]<0&&data[i]>ave)){
                PeakAndTrough[i]=0;
            }
        }

        //排除相邻的峰谷值
        for (int i=0;i<PeakAndTrough.length;){
            int index=i++;

            while(i<PeakAndTrough.length&&PeakAndTrough[i]<=0){
                if (PeakAndTrough[i]==-1) {
                    if(data[i]<data[index]) {
                        PeakAndTrough[index]=0;
                        index=i;
                    }else{
                        PeakAndTrough[i]=0;
                    }
                }
                i++;
            }

            if (i>=PeakAndTrough.length){
                break;
            }

            index=i++;

            while(i<PeakAndTrough.length&&PeakAndTrough[i]>=0){
                if (PeakAndTrough[i]==1) {
                    if(data[i]>data[index]) {
                        PeakAndTrough[index]=0;
                        index=i;
                    }else{
                        PeakAndTrough[i]=0;
                    }
                }
                i++;
            }
        }

        return PeakAndTrough;
    }

    public static float[] smoothFilter(float[] data,int strength){
        if (strength>data.length/2){
            strength=data.length/2;
        }

        float[] back=new float[data.length];

        back[0]=data[0];
        for (int i=1;i<data.length;i++){
            if (i<strength){
                back[i]=back[i-1]+data[i];
            }else{
                back[i]=back[i-1]-data[i-strength]+data[i];
            }
        }

        for (int i=1;i<back.length;i++){
            if (i<strength){
                back[i]/=(i+1);
            }else{
                back[i]/=strength;
            }
        }

        return back;
    }

    public static float[] diff(float[] values){
        float[] data=new float[values.length-1];

        for(int i=0;i<data.length;i++){
            data[i]=values[i]-values[i+1];
        }

        return data;
    }


//    public static float[] smoothFilter(float[] data,int strength){
//        if (strength>data.length){
//            strength=data.length;
//        }
//        float[] back=new float[data.length];
//
//        back[0]=data[0];
//        back[back.length-1]=data[back.length-1];
//
//        for (int i=1,j=back.length-2;i<=strength/2;i++,j--){
//            back[i]=back[i-1]+data[i*2]+data[i*2-1];
//            back[j]=back[j+1]+data[back.length-i*2-1]+data[back.length-i*2];
//        }
//
//        for (int i=strength/2+1;i<back.length-strength/2-1;i++){
//            back[i]=back[i-1]-data[i-strength/2-1]+data[i+strength/2];
//        }
//
//        for (int i=1;i<=strength/2;i++){
//            back[i]/=(2*i+1);
//            back[back.length-1-i]/=(2*i+1);
//        }
//
//        for (int i=strength/2+1;i<back.length-strength/2-1;i++){
//            back[i]/=strength;
//        }
//
//        return back;
//    }

    //    public static int getPeakNum0(float[] data){
//        int PEAK=1;
//        int TROUGH=-1;
//
//        float[] data1=smoothFilter(data,3);
//        float[] data2=new float[data.length];
//
//        for (int lo=0,mid=1,hi=2;hi<data1.length;){
//            while(mid<data1.length&&data1[mid]==data1[lo]){
//                mid++;
//            }
//
//            hi=mid+1;
//
//            while(hi<data1.length&&data1[hi]==data1[mid]){
//                hi++;
//            }
//
//            if (hi>=data1.length){
//                break;
//            }
//
//            if (data1[mid]>data1[lo]&&data1[mid]>data1[hi]){
//                data2[mid]=PEAK;
//            }else if (data1[mid]<data1[lo]&&data1[mid]<data1[hi]){
//                data2[mid]=TROUGH;
//            }
//
//            lo=mid;
//            mid=hi;
//            ++hi;
//        }
//
//        return 0;
//    }
}
