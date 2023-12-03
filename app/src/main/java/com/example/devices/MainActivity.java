package com.example.devices;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Context context;

    private Activity activity;

    private TextView versionAndroid;
    private int versionSDK;
    private ProgressBar pbLevelBattery;
    private  TextView tvLevelBattery;

    //Conection
    private TextView tvConnection;
    ConnectivityManager connection;

     CameraManager cameraManager;
     String cameraId;

     //File
    private EditText nameFile;
    //private ClFile clFile;

    //Light
    private Button btLightOn;
    private Button btLightOff;

    IntentFilter batteryFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beginning();
        btLightOn.setOnClickListener(this::onLight);
        btLightOff.setOnClickListener(this::offLight);
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broReciever,batteryFilter);

    }

    BroadcastReceiver broReciever = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBaterry = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,'-');
            pbLevelBattery.setProgress(levelBaterry);
            tvLevelBattery.setText("Level Battery "+levelBaterry+"%");
        }
    };
    private  void checkConnection(){
        connection = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connection.getActiveNetworkInfo();
        boolean stateNet = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if(stateNet) tvConnection.setText("State ON");
        else tvConnection.setText("State OFF");
   }
    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText(versionSO+"-"+versionSDK);
    }

    private void offLight(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId,false);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void onLight(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId,true);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void beginning(){
        this.versionAndroid = findViewById(R.id.tvAndroidVersion);
        this.pbLevelBattery = findViewById(R.id.pbBattery);
        this.tvLevelBattery = findViewById(R.id.tvBattery);
        this.tvConnection = findViewById(R.id.tvConection);
        this.btLightOn = findViewById(R.id.btLightOn);
        this.btLightOff = findViewById(R.id.btLightOff);
        //this
    }
}