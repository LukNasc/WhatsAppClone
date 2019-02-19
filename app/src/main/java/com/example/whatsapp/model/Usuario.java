package com.example.whatsapp.model;

import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {
    private String nome;
    private String number;
    private String idUser;
    private String foto;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
        DatabaseReference usuarioChield = firebaseRef.child("usuarios").child(getIdUser());

        usuarioChield.setValue(this);

    }

    public void update(){
        String userId = UsuarioFirebase.getIdUser();
        DatabaseReference database = SettingsFirebase.getFirebaseDatabase();

        DatabaseReference userReference = database.child("usuarios")
                .child(userId);

        Map<String, Object> valueUser = convertMap();

        userReference.updateChildren(valueUser);

    }


    @Exclude
    public Map<String, Object> convertMap(){

        HashMap<String, Object> usrMap = new HashMap<>();

        usrMap.put("number",getNumber());
        usrMap.put("nome", getNome());
        usrMap.put("foto", getFoto());

        return usrMap;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Exclude
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
