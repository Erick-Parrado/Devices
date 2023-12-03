package com.example.devices;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;

public class ClFile {
    private static final int REQUEST_CODE = 23;
    private Context context;
    private Activity activity;

    public ClFile(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    private boolean statusPermissionES(){
        int response = ContextCompat.checkSelfPermission(this.context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(response == PackageManager.PERMISSION_GRANTED)return true;
        return false;
    }

    private void requestPermissionES(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !statusPermissionES()){
            ActivityCompat.requestPermissions(this.activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            Log.i("Storage Permission","I have access to external storage");
        }
    }



    private void createDir(File file){
        if(!file.exists()){
            file.mkdir();
        }
    }

    public void saveFile(String fileName, String fileInfo){
        File directory = null;
        requestPermissionES();
        String toast;
        Toast.makeText(context,""+statusPermissionES(),Toast.LENGTH_LONG).show();
        if(statusPermissionES()){
            try{
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
                    directory = new File(Environment.getExternalStorageDirectory(),"FileAPP");
                }
                else{
                    directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM),"FileAPP");
                }
                createDir(directory);
                Toast.makeText(context,"Ruta:"+directory, Toast.LENGTH_LONG).show();
                if(directory!=null){
                    File file = new File(directory,fileName);
                    FileWriter writer = new FileWriter(file);
                    writer.append(fileInfo);
                    writer.flush();
                    writer.close();
                    toast = "Se ha guardado el archivo";
                    return;
                }
                else toast = "No se pudo crear el directorio";
                return;
            }
            catch (Exception e){

            }
            toast = "No hay permiso";
            Toast.makeText(this.context,toast,Toast.LENGTH_LONG);
        }
    }
}
