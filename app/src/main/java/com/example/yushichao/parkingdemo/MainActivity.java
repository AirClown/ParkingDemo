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
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.List;
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
    private MyView mv;
    private Button bt;
    private SeekBar sb;

    //控制器
    private MyCamera1 camera1;
    private MagDetector magDetector;
    private PositionCalCulate positionCalCulate;

    //线程
    private Thread cameraThread;

    private boolean pause=false;

    //拓扑图数据
    private List<Line> lines;
    private List<Lamp> lamps;

    private MyCamera1.MyCameraCallback myCameraCallback=new MyCamera1.MyCameraCallback() {
        @Override
        public void UpdateText(String text) {
            if (map!=null) {
                map.setText(text);
            }
        }

        @Override
        public void UpdateImage(Bitmap bitmap) {

        }

        @Override
        public void UpdateLamp(int LampId) {
            positionCalCulate.setLampId(LampId);
        }
    };

    private MagDetector.MagDetectorCallback magDetectorCallback=new MagDetector.MagDetectorCallback() {
        @Override
        public void MagState(float var, float speed) {
            if (map!=null){
                map.setText("速度估计:"+(int)(speed*3.6f)+"km/h"+"\r\n方差："+var);
            }

            if (positionCalCulate!=null){
                positionCalCulate.setSpeed(10*speed);
            }
        }
    };

    private PositionCalCulate.PositionCallback positionCallback=new PositionCalCulate.PositionCallback() {
        @Override
        public void positionChanged(int x, int y) {
            if (map!=null) {
                map.setPosition(x, y);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if(!hasPermissionsGranted(PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(PERMISSIONS, 1);
            }
        }

        Init();
    }

    private void Init(){
        //开启传感器
        manager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);

        //获取拓扑图数据
        lines=Utils.getLine();
        lamps=Utils.getLamp();

        //initUi
        sb=(SeekBar)findViewById(R.id.seekBar);
        camerabutton=(Button)findViewById(R.id.camera);
        sv=(SurfaceView)findViewById(R.id.surfaceView);
        bt=(Button)findViewById(R.id.pause);

        mv=(MyView)findViewById(R.id.myView);
        mv.setRange(100);

        map=(Map)findViewById(R.id.map);
        map.setText("迦南地");
        map.setLine(lines);
        map.setLamp(lamps);

        //控制器
        magDetector=new MagDetector(magDetectorCallback);
        magDetector.saveData(this.getExternalFilesDir(null)+"");
        camera1=new MyCamera1(this,sv,myCameraCallback);
        camera1.openCamera();

        positionCalCulate=new PositionCalCulate(positionCallback);
        positionCalCulate.setTopo(lines,lamps);

        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(camera1==null){
                    Toast.makeText(MainActivity.this,"程序正在初始化...",Toast.LENGTH_SHORT)
                            .show();
                }else if(cameraThread==null){
                    cameraThread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                while (true) {
                                    Thread.sleep(100);
                                    camera1.TakePhoto();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    cameraThread.start();
                    Toast.makeText(MainActivity.this,"开始连续拍照",Toast.LENGTH_SHORT)
                            .show();
                }else{
                    cameraThread.interrupt();
                    cameraThread=null;
                    Toast.makeText(MainActivity.this,"停止拍照",Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause=!pause;
            }
        });

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                magDetector.ind=progress;
                Toast.makeText(MainActivity.this,"参数为"+magDetector.ind,Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                break;
            case Sensor.TYPE_LIGHT:
                map.invalidate();
                break;
            case Sensor.TYPE_ORIENTATION:
                positionCalCulate.setAngle(event.values[0]-0);//110为地图偏角
                map.setAngle(event.values[0]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                if (magDetector!=null&&!pause) {
                    magDetector.refreshMag(event.values);
                    mv.setData(magDetector.mag);
                }
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
