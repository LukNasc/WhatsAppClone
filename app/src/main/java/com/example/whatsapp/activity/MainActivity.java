package com.example.whatsapp.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.util.Action;
import com.example.whatsapp.util.Permission;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.Util;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button btnEntrar;
    private FirebaseAuth auth = SettingsFirebase.getFirebaseAuth();
    private String[] permission = new String[]{
            Manifest.permission.READ_CONTACTS
    };
    public static final int REQUESTCODECONTACTS = 300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEntrar = findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int res:grantResults){
            if(res == PackageManager.PERMISSION_DENIED){
                alertValidationPermission();
            }
        }

    }

    private void alertValidationPermission() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.permission_dinied_title);
        alert.setMessage(R.string.permission_denied);
        alert.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.setCancelable(false);
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Toast.makeText(this, "vai listar os contatos que estão no banco de dados (MAS AGORA NÃO)", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.checkUser(auth, new Action() {
            @Override
            public void openAct() {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            }
        });

        Permission.validationPermission(permission, this, 2);
    }


}
