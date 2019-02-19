package com.example.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsapp.R;
import com.example.whatsapp.adapter.AdapterMessage;
import com.example.whatsapp.dialog.CustomAlertDiailog;
import com.example.whatsapp.model.Conversations;
import com.example.whatsapp.model.Group;
import com.example.whatsapp.model.Message;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.util.Base64Custom;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.UsuarioFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView tvNome;
    private CircleImageView profile;
    private Usuario destinUser;
    private Group groupDestin;
    private EditText fieldMessage;
    private RecyclerView recyclerMessage;
    private String idUserRemetente, idUserDestin;
    private AdapterMessage adapterMessage;
    private List<Message> lstMessage = new ArrayList<>();
    private DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
    private DatabaseReference messageRef;
    private StorageReference storage = SettingsFirebase.getFirebaseStorage();
    private ChildEventListener eventListenerMessage;
    private CustomAlertDiailog customAlertDiailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNome = findViewById(R.id.textViewNomeChat);
        profile = findViewById(R.id.circleIvProfileChat);
        fieldMessage = findViewById(R.id.editMessage);
        recyclerMessage = findViewById(R.id.recyclerMessagesList);

        idUserRemetente = UsuarioFirebase.getIdUser();
        adapterMessage = new AdapterMessage(lstMessage, this);

        lstMessage.clear();
        Bundle dataUser = getIntent().getExtras();
        if (dataUser != null) {
            if (dataUser.containsKey("chatGrupo")) {
                groupDestin = (Group) dataUser.getSerializable("chatGrupo");
                tvNome.setText(groupDestin.getNome());
                String photo = groupDestin.getFoto();

                if (photo != null) {
                    Glide.with(this)
                            .load(photo)
                            .into(profile);
                } else {
                    profile.setImageResource(R.drawable.padrao);
                }
                idUserDestin = groupDestin.getId();
            } else {
                destinUser = (Usuario) dataUser.getSerializable("chatContato");
                tvNome.setText(destinUser.getNome());
                String photo = destinUser.getFoto();

                if (photo != null) {
                    Glide.with(this).load(Uri.parse(photo)).into(profile);
                } else {
                    profile.setImageResource(R.drawable.padrao);
                }
                idUserDestin = Base64Custom.encodeBase64(destinUser.getNumber().substring(destinUser.getNumber().length() - 4));
            }
        }

        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
        recyclerMessage.setLayoutManager(lm);
        recyclerMessage.setHasFixedSize(true);
        recyclerMessage.setAdapter(adapterMessage);


        messageRef = database.child("message")
                .child(idUserRemetente)
                .child(idUserDestin);

        adapterMessage.notifyDataSetChanged();
    }

    public void sendMessage(View view) {
        String message = fieldMessage.getText().toString();
        if (!message.isEmpty()) {
            if (destinUser != null) {
                Message msg = new Message();
                msg.setIdUser(idUserRemetente);
                msg.setMessage(message);
                msg.setUserRemetente(UsuarioFirebase.getDateUserCurrent());
                //SalvarMessage
                saveMessage(idUserRemetente, idUserDestin, msg);

                saveMessage(idUserDestin, idUserRemetente, msg);


                saveConversation(msg, idUserDestin, idUserRemetente, destinUser);
                Usuario usuario = new Usuario();
                usuario.setNome(UsuarioFirebase.getDateUserCurrent().getNome());
                usuario.setNumber(UsuarioFirebase.getCurrentUser().getPhoneNumber());
                usuario.setFoto(UsuarioFirebase.getDateUserCurrent().getFoto());


                saveConversation(msg, idUserRemetente, idUserDestin, usuario);
            } else {
                for (Usuario membro : groupDestin.getMembros()) {
                    String idRemetente = Base64Custom.encodeBase64(membro.getNumber().substring(membro.getNumber().length() - 4));
                    String idUserCurrent = UsuarioFirebase.getIdUser();

                    Message msg = new Message();
                    msg.setIdUser(idUserCurrent);
                    msg.setMessage(message);
                    msg.setNome(UsuarioFirebase.getDateUserCurrent().getNome());

                    saveMessage(idRemetente, groupDestin.getId(), msg);
                    saveConversation(msg, groupDestin.getId(), idRemetente, groupDestin);

                }
            }
            fieldMessage.setText("");
        }
    }

    private void saveMessage(String userRemetente, String userDestin, Message message) {
        DatabaseReference messageRef = database.child("message");

        messageRef.child(userRemetente)
                .child(userDestin)
                .push()
                .setValue(message);

    }

    private void saveConversation(Message message, String userDestin, String userRemetente, Usuario user) {
        Conversations conversationsRemetente = new Conversations();
        conversationsRemetente.setIdDestinatario(userDestin);
        conversationsRemetente.setIdRementente(userRemetente);
        conversationsRemetente.setUltimaMessagem(message.getMessage());
        conversationsRemetente.setUsuario(user);
        conversationsRemetente.setIsGroup("false");
        String cod  = String.valueOf(UUID.randomUUID());
        conversationsRemetente.setCodMessage(cod);

        conversationsRemetente.salvar();

    }

    private void saveConversation(Message message, String userDestin, String userRemetente, Group group) {
        Conversations conversationsRemetente = new Conversations();
        conversationsRemetente.setIdDestinatario(userDestin);
        conversationsRemetente.setIdRementente(userRemetente);
        conversationsRemetente.setUltimaMessagem(message.getMessage());
        conversationsRemetente.setGroup(group);
        conversationsRemetente.setIsGroup("true");
        String cod  = String.valueOf(UUID.randomUUID());
        conversationsRemetente.setCodMessage(cod);
        conversationsRemetente.salvar();

    }

    private void getMessages() {
        lstMessage.clear();
        eventListenerMessage = messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                lstMessage.add(message);
                adapterMessage.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void selectedImage(View view) {
        customAlertDiailog = new CustomAlertDiailog(this, this);
        customAlertDiailog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messageRef.removeEventListener(eventListenerMessage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMessages();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case SettingsActivity.REQUESTCODECAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                    case SettingsActivity.REQUESTCODEGALERIA:
                        Uri url = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), url);
                        break;
                }
                String imagem = UUID.randomUUID().toString();
                if (image != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);

                    byte[] dataImage = baos.toByteArray();

                    StorageReference imageRef = storage.child("imagens")
                            .child("fotos")
                            .child(idUserRemetente)
                            .child(imagem + ".jpg");

                    UploadTask uploadTask = imageRef.putBytes(dataImage);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro", "Erro ao fazer upload");
                            Toast.makeText(ChatActivity.this, R.string.erro_save_img, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String url = taskSnapshot.getDownloadUrl().toString();

                            Message message = new Message();
                            message.setIdUser(idUserRemetente);
                            message.setMessage("");
                            message.setImage(url);

                            saveMessage(idUserRemetente, idUserDestin, message);
                            saveMessage(idUserDestin, idUserRemetente, message);
                        }
                    });

                    customAlertDiailog.dismiss();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
