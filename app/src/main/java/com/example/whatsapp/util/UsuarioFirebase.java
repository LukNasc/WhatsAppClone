package com.example.whatsapp.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {
    public static String getIdUser(){
        FirebaseAuth auth = SettingsFirebase.getFirebaseAuth();
        String idReturn = "";
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        String phoneNo = getCurrentUser().getPhoneNumber().substring(getCurrentUser().getPhoneNumber().length()-4);
        idReturn = Base64Custom.encodeBase64(phoneNo);

        return idReturn;
    }

    public static FirebaseUser getCurrentUser(){
        FirebaseAuth firebaseAuth = SettingsFirebase.getFirebaseAuth();
        return firebaseAuth.getCurrentUser();
    }

    public static boolean updatePhotoUser(Uri url){
        FirebaseUser user = getCurrentUser();
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(url)
                .build();


       try {
           user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if(!task.isSuccessful()){
                       Log.d("Perfil","Erro ao atualizar foto de perfil");
                   }

               }
           });
           return true;
       }catch (Exception e){
            e.printStackTrace();
            return false;
       }

    }

    public static boolean updateNameUser(String nome){
        FirebaseUser user = getCurrentUser();
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(nome)
                .build();


        try {
            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil","Erro ao atualizar nome de perfil");
                    }

                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public static Usuario getDateUserCurrent(){
        FirebaseUser firebaseUser = getCurrentUser();

        Usuario dataUser = new Usuario();
        dataUser.setNumber(getCurrentUser().getPhoneNumber());
        dataUser.setNome(getCurrentUser().getDisplayName());
        dataUser.setIdUser(getIdUser());

        if(firebaseUser.getPhotoUrl() == null){
            dataUser.setFoto("");
        }else{
            dataUser.setFoto(firebaseUser.getPhotoUrl().toString());
        }

        return dataUser;
    }
}
