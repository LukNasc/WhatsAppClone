package com.example.whatsapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.dialog.CustomAlertDiailog;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.util.Permission;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.UsuarioFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private String[] permission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private CustomAlertDiailog customDialog;
    private ProgressBar progressBar;
    private ImageView imageView, imageViewButtonSave,ivUpdateFoto;
    private View view;
    public static final int REQUESTCODECAMERA = 100;
    public static final int REQUESTCODEGALERIA = 200;
    private StorageReference referenceStorage;
    private DatabaseReference firebaseDatabase;
    private EditText editTextNome;
    private Usuario userCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        imageView = findViewById(R.id.circleIVProfile);
        progressBar = findViewById(R.id.progressBarProfile);
        referenceStorage = SettingsFirebase.getFirebaseStorage();
        editTextNome = findViewById(R.id.editTextNome);
        imageViewButtonSave = findViewById(R.id.imageViewButtonSave);
        ivUpdateFoto = findViewById(R.id.ivUpdateFoto);
        view = findViewById(R.id.viewSettings);
        userCurrent = UsuarioFirebase.getDateUserCurrent();

        //Validar Permission
        Permission.validationPermission(permission, this, 1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.titulo_config);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser user = UsuarioFirebase.getCurrentUser();
        Uri url = user.getPhotoUrl();



        imageView.setImageResource(R.drawable.padrao);

        if (url != null) {
            Glide.with(SettingsActivity.this)
                    .load(url)
                    .into(imageView);

            progressBar.setVisibility(View.INVISIBLE);
        } else {
            imageView.setImageResource(R.drawable.padrao);
            progressBar.setVisibility(View.INVISIBLE);

        }


        editTextNome.setText(UsuarioFirebase.getCurrentUser().getDisplayName());

        imageView.setOnClickListener(this);
        ivUpdateFoto.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivUpdateFoto:
                customDialog = new CustomAlertDiailog(this, this);
                customDialog.show();
                break;
            case R.id.imageViewButtonSave:
                String nome = editTextNome.getText().toString();
                boolean retorno = UsuarioFirebase.updateNameUser(nome);

                if (retorno) {
                    userCurrent.setNome(nome);
                    userCurrent.update();
                }

                Toast.makeText(this, getResources()
                                .getString(R.string.msg_succes_update, "Seu nome foi"),
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case REQUESTCODECAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case REQUESTCODEGALERIA:
                        Uri location = data.getData();
                        image = MediaStore.Images.Media.getBitmap(
                                getContentResolver(),
                                location
                        );
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (image != null) {
                imageView.setImageBitmap(image);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 70, baos);

                byte[] dataImage = baos.toByteArray();

                progressBar.setVisibility(View.VISIBLE);

                StorageReference imagemRef = referenceStorage
                        .child("imagens")
                        .child("perfil")
                        .child(UsuarioFirebase.getIdUser())
                        .child("perfil.jpeg");

                UploadTask uploadTask = imagemRef.putBytes(dataImage);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SettingsActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();

                        Uri url = taskSnapshot.getDownloadUrl();
                        updatePhotoUser(url);
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });
            }
            customDialog.dismiss();

        }
    }


    private void updatePhotoUser(Uri url) {
        UsuarioFirebase.updatePhotoUser(url);
        userCurrent.setFoto(url.toString());
        userCurrent.update();
    }
}
