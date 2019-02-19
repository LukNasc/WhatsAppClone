package com.example.whatsapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.adapter.GroupSelectedAdapter;
import com.example.whatsapp.model.Group;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.util.OnClickRecyclerView;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.UsuarioFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.whatsapp.activity.SettingsActivity.REQUESTCODEGALERIA;

public class RegisterGroupActivity extends AppCompatActivity implements Serializable {
    private List<Usuario> lstMemberSelected = new ArrayList<>();
    private TextView txtNumberMember;
    private CircleImageView profileGroup;
    private EditText fieldNameGroup;
    private RecyclerView recyclerGroup;
    private GroupSelectedAdapter groupSelectedAdapter;
    private StorageReference storageReference = SettingsFirebase.getFirebaseStorage();
    private Group group;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo grupo");
        toolbar.setSubtitle("Adicionar nome");
        setSupportActionBar(toolbar);

        txtNumberMember = findViewById(R.id.tvTextNumberMember);
        recyclerGroup = findViewById(R.id.recyclerMemberGroup);
        profileGroup = findViewById(R.id.imageGroup);
        fieldNameGroup = findViewById(R.id.editNameGroup);
        fab = findViewById(R.id.fab);
        fieldNameGroup = findViewById(R.id.editNameGroup);


        group = new Group();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            List<Usuario> member = (List<Usuario>) getIntent().getExtras().getSerializable("member");
            lstMemberSelected.addAll(member);
        }
        txtNumberMember.setText("Participantes: " + lstMemberSelected.size());

        groupSelectedAdapter = new GroupSelectedAdapter(getApplicationContext(), lstMemberSelected, new OnClickRecyclerView() {
            @Override
            public void onClick(int position) {

            }
        });


        RecyclerView.LayoutManager lmH = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerGroup.setLayoutManager(lmH);
        recyclerGroup.setHasFixedSize(true);
        recyclerGroup.setAdapter(groupSelectedAdapter);


        profileGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent2.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent2, REQUESTCODEGALERIA);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameGroup = fieldNameGroup.getText().toString();
                lstMemberSelected.add(UsuarioFirebase.getDateUserCurrent());
                group.setMembros(lstMemberSelected);
                group.setNome(nameGroup);
                group.salvar();

                Intent i = new Intent(RegisterGroupActivity.this, ChatActivity.class);
                i.putExtra("chatGrupo", group);
                startActivity(i);
                finish();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap;
            try {
                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                if (bitmap != null) {
                    profileGroup.setImageBitmap(bitmap);

                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dataImage = baos.toByteArray();

                    StorageReference imageRef = storageReference.child("imagens")
                            .child("grupos")
                            .child(group.getId() + ".jpg");
                    final ProgressDialog builder = new ProgressDialog(RegisterGroupActivity.this);
                    builder.setCancelable(false);
                    builder.setMessage("Salvando imagem do grupo, aguarde...");
                    builder.show();
                    UploadTask uploadTask = imageRef.putBytes(dataImage);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            builder.dismiss();
                            Toast.makeText(RegisterGroupActivity.this,
                                    "Erro ao salvar imagem!"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String url = taskSnapshot.getDownloadUrl().toString();
                            group.setFoto(url);
                            builder.dismiss();
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
