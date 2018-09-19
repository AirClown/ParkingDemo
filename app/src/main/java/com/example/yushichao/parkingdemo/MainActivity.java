package com.example.yushichao.parkingdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //权限
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //传感器
    private SensorManager manager;

    //UI
    private Map map;
    private SurfaceView sv;
    private Button camerabutton;

    //控制器
    private MyCamera1 camera1;

    //定时器
    private Timer cameratimer;
    private TimerTask cameratask;

    private MyCamera1.MyCameraCallback myCameraCallback=new MyCamera1.MyCameraCallback() {
        @Override
        public void UpdateText(String text) {
            map.setText(text);
        }

        @Override
        public void UpdateImage(Bitmap bitmap) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if(!hasPermissionsGranted(PERMISSIONS)) {
            if(Build.VERSION.SDK_INT>=23) {
                requestPermissions(PERMISSIONS, 1);
            }
        }

        Init();
    }

    private void Init(){
        //开启传感器
        manager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);

        //initUi
        camerabutton=(Button)findViewById(R.id.camera);
        sv=(SurfaceView)findViewById(R.id.surfaceView);
        map=(Map)findViewById(R.id.map);
        map.setText("迦南地");

        //控制器
        camera1=new MyCamera1(this,sv,myCameraCallback);
        camera1.openCamera();

        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameratimer!=null){
                    StopCameraTimer();
                    Toast.makeText(MainActivity.this,"停止拍照",Toast.LENGTH_SHORT).show();
                }else{
                    StartCameraTimer(1000);
                    Toast.makeText(MainActivity.this,"开始连续拍照",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void StopCameraTimer(){
        if (cameratimer!=null) {
            cameratimer.purge();
            cameratimer.cancel();
            cameratask.cancel();
            cameratimer = null;
            cameratask = null;
        }
    }

    private void StartCameraTimer(int time){
        cameratimer=new Timer();
        cameratask=new TimerTask() {
            @Override
            public void run() {
                camera1.TakePhoto();
            }
        };
        cameratimer.schedule(cameratask,100,time);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                break;
            case Sensor.TYPE_LIGHT:
                map.setPosition(250,250);
                break;
            case Sensor.TYPE_ORIENTATION:
                map.setAngle(event.values[0]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(flag);
        }

        Sensor acc=manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this,acc,SensorManager.SENSOR_DELAY_GAME);

        Sensor light=manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        manager.registerListener(this,light,SensorManager.SENSOR_DELAY_GAME);

        Sensor angle=manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        manager.registerListener(this,angle,SensorManager.SENSOR_DELAY_GAME);

        Sensor mag=manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        manager.registerListener(this,mag,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == PERMISSIONS.length) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this,"请给权限",Toast.LENGTH_SHORT);
                        return;
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT>21) {
                    requestPermissions(permissions, requestCode);
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT>=23&&this.checkSelfPermission(permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}