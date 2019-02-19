package com.example.whatsapp.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {
    public static boolean validationPermission(String[] p, Activity activity, int requestCode){
        if(Build.VERSION.SDK_INT >= 23){
            List<String> lstPermission = new ArrayList<>();

            /*Percorre as permissões passadas
            verificando uma a uma
            se ja tem permissão liberada
             */

            for(String permission:p){
               boolean permissionGranted = ContextCompat
                       .checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;

               //Se não tiver permissões adiciona no ArrayList
               if(!permissionGranted){
                    lstPermission.add(permission);
               }
            }

            //verifica se o array list é vazio
            if(lstPermission.isEmpty()) return true;


            //se não for, pede permissão
            String[] newPermission = new String[lstPermission.size()];
            lstPermission.toArray(newPermission);

            ActivityCompat.requestPermissions(activity, newPermission, requestCode);
        }

        return true;
    }
}
