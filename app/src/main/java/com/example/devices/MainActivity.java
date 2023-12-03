package com.example.devices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_BT_CODE = 1001;
    private static final int REQUEST_STORAGE_CODE = 23;
    private Context context;
    private Activity activity;
    private TextView versionAndroid;
    private int versionSDK;
    private ProgressBar pbLevelBattery;
    private TextView tvLevelBattery;
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

    private BluetoothAdapter bluetoothAdapter;
    private Button btBluetooth;

    IntentFilter batteryFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beginning();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btLightOn.setOnClickListener(this::onLight);
        btLightOff.setOnClickListener(this::offLight);
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        ibFile.setOnClickListener(this::onSaveFile);
        btBluetooth.setOnClickListener(this::onBluetoothOn);
        registerReceiver(broReciever, batteryFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        versionAndroid.setText(versionSO + "-" + versionSDK);
        changeBtBT();
        checkConnection();
    }



    private void beginning() {
        this.versionAndroid = findViewById(R.id.tvAndroidVersion);
        this.pbLevelBattery = findViewById(R.id.pbBattery);
        this.tvLevelBattery = findViewById(R.id.tvBattery);
        this.tvConnection = findViewById(R.id.tvConection);
        this.btLightOn = findViewById(R.id.btLightOn);
        this.btLightOff = findViewById(R.id.btLightOff);
        this.context = getApplicationContext();
        this.etFile = findViewById(R.id.etFile);
        this.ibFile = findViewById(R.id.ibFile);
        this.btBluetooth = findViewById(R.id.btBluetooth);
        this.activity = this;
    }

    BroadcastReceiver broReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBaterry = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, '-');
            pbLevelBattery.setProgress(levelBaterry);
            tvLevelBattery.setText("Level Battery " + levelBaterry + "%");
        }
    };

    private void checkConnection() {
        connection = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connection.getActiveNetworkInfo();
        boolean stateNet = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if (stateNet) tvConnection.setText("State ON");
        else tvConnection.setText("State OFF");
    }

    private void offLight(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void onLight(View view) {
        try {
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestPermissionBT(){
            ActivityCompat.requestPermissions(this.activity,new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BT_CODE);
            Log.i("Bluetooth Permission","I have access to external storage");
    }
    private void changeBtBT(){
        if(bluetoothAdapter.isEnabled()){
            btBluetooth.setText("Activado");
            btBluetooth.setEnabled(false);
        }
    }
    private void onBluetoothOn(View view) {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent, null);
                changeBtBT();
            }
        }
        else{
            requestPermissionBT();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String permission=null;
        switch (requestCode){
            case REQUEST_STORAGE_CODE:
                permission="almacenamiento interno";
                break;
            case REQUEST_BT_CODE:
                permission="bluetooth";
                break;
            default:
                return;
        }
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            noPermissionsAlert(permission);
        }
    }

    private void noPermissionsAlert(String permission){
        new AlertDialog.Builder(this)
                .setTitle("Alerta de permisos")
                .setMessage("No se han concedido permisos para el acceso al "+permission+".Por favor, activarlos desde ajustes para continuar con el uso de la aplicacion")
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