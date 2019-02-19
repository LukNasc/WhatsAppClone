package com.example.whatsapp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.model.Usuario;
import com.example.whatsapp.util.Action;
import com.example.whatsapp.util.Base64Custom;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.UsuarioFirebase;
import com.example.whatsapp.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class CheckNumberActivity extends AppCompatActivity implements Serializable{
    private EditText fieldCode;
    private  Usuario usuario;
    private FirebaseAuth auth = SettingsFirebase.getFirebaseAuth();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_number);

        fieldCode = findViewById(R.id.editTextCod);
        fieldCode.setEnabled(false);
        final Bundle dados = getIntent().getExtras();
        usuario = (Usuario) getIntent().getSerializableExtra("dados");
        auth.setLanguageCode("pt-br");
        doValidation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Util.checkUser(auth, new Action() {
            @Override
            public void openAct() {
                startActivity(new Intent(CheckNumberActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    public void doValidation(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(usuario.getNumber(),
                30,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        LinearLayout linearLayout = findViewById(R.id.linearProgress);
                        linearLayout.setVisibility(View.INVISIBLE);
                        fieldCode.setText("");
                        fieldCode.setText(phoneAuthCredential.getSmsCode());

                        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    String numberFormat = usuario.getNumber()
                                            .replaceAll("( | )","")
                                            .replaceAll("[( | )]","")
                                            .replaceAll("[-]","");

                                    UsuarioFirebase.updateNameUser(usuario.getNome());

                                    try{

                                        usuario.setIdUser(Base64Custom.encodeBase64(numberFormat.substring(numberFormat.length()-4)));
                                        usuario.setNumber(numberFormat);

                                        usuario.salvar();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        LinearLayout linearLayout = findViewById(R.id.linearProgress);
                        linearLayout.setVisibility(View.INVISIBLE);
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(CheckNumberActivity.this, R.string.inavlid_number,Toast.LENGTH_LONG).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            Toast.makeText(CheckNumberActivity.this, R.string.number_exceeded,Toast.LENGTH_LONG).show();
                        }
                        finish();
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        ProgressBar linearLayout = findViewById(R.id.progressBar);
                        linearLayout.setVisibility(View.INVISIBLE);
                        final AlertDialog.Builder alert = new AlertDialog.Builder(CheckNumberActivity.this);
                        alert.setTitle("Atenção");
                        alert.setMessage(R.string.msg_warning);
                        alert.setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                         alert.setCancelable(false);
                        alert.show();
                    }
                });
    }
}
