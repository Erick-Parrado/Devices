package com.example.devices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_STORAGE_CODE = 23;
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
    private EditText etFile;
    private ImageButton ibFile;
    private ClFile clFile;
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
        ibFile.setOnClickListener(this::onSaveFile);
        registerReceiver(broReciever,batteryFilter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText(versionSO+"-"+versionSDK);
        checkConnection();
    }

    private void beginning(){
        this.versionAndroid = findViewById(R.id.tvAndroidVersion);
        this.pbLevelBattery = findViewById(R.id.pbBattery);
        this.tvLevelBattery = findViewById(R.id.tvBattery);
        this.tvConnection = findViewById(R.id.tvConection);
        this.btLightOn = findViewById(R.id.btLightOn);
        this.btLightOff = findViewById(R.id.btLightOff);
        this.context = getApplicationContext();
        this.etFile = findViewById(R.id.etFile);
        this.ibFile = findViewById(R.id.ibFile);
        this.activity = this;
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_STORAGE_CODE){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                noPermissionsAlert("l almacenamiento");
            }
        }
    }

    private void noPermissionsAlert(String permission){
        new AlertDialog.Builder(this)
                .setTitle("Alerta de permisos")
                .setMessage("No se han concedido permisos para el acceso a"+permission+".Por favor, activarlos desde ajustes para continuar con el uso de la aplicacion")
                .setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package",getPackageName(),null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                }).show();
    }

    public void onSaveFile(View view){
        String fileName = etFile.getText().toString()+".txt";
        String batteryInfo = tvLevelBattery.getText().toString();
        ClFile file = new ClFile(context,activity);
        file.saveFile(fileName,batteryInfo);
    }
}