package com.example.whatsapp.activity;

import android.content.Intent;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.util.Action;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.Util;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vicmikhailau.maskededittext.MaskedEditText;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity{
    private MaskedEditText fieldNumber;
    private EditText fieldName;
    private Button buttonLogin;
    private Usuario usuario;
    private FirebaseAuth auth = SettingsFirebase.getFirebaseAuth();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        fieldNumber = findViewById(R.id.editTextNumero);
        fieldName = findViewById(R.id.editTextNome);
        buttonLogin = findViewById(R.id.btnEntrar2);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!fieldNumber.getText().toString().equals("")){
                   if(!fieldName.getText().toString().equals("")){
                       usuario = new Usuario();
                       usuario.setNome(fieldName.getText().toString());
                       usuario.setNumber(fieldNumber.getText().toString());

                       Bundle bundle = new Bundle();
                       bundle.putSerializable("dados", usuario);

                       Intent intent = new Intent(LoginActivity.this, CheckNumberActivity.class);
                       intent.putExtras(bundle);
                       startActivity(intent);
                   }else{
                       Toast.makeText(getApplicationContext(),
                               getResources().getString(R.string.msg_error, "\"nome\""),
                               Toast.LENGTH_SHORT).show();
                   }
               }else{
                   Toast.makeText(getApplicationContext(),
                           getResources().getString(R.string.msg_error, "\"número\""),
                           Toast.LENGTH_SHORT).show();

               }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.checkUser(auth, new Action() {
            @Override
            public void openAct() {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
        });
    }
}
