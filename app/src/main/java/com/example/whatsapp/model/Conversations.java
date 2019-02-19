package com.example.whatsapp.model;

import com.example.whatsapp.util.SettingsFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Conversations {
    private String idRementente;
    private String idDestinatario;
    private String ultimaMessagem;
    private Usuario usuario;
    private String isGroup;
    private Group group;
    private String codMessage;

    public Conversations() {
        this.setIsGroup("false");
    }

    public void salvar(){
        DatabaseReference databaseReference = SettingsFirebase
                .getFirebaseDatabase();

        DatabaseReference conversaRef = databaseReference
                .child("conversas");

        conversaRef.child(this.idRementente)
                .child(this.idDestinatario)
                .setValue(this);
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String group) {
        isGroup = group;
    }

    public String getCodMessage() {
        return codMessage;
    }

    public void setCodMessage(String codMessage) {
        this.codMessage = codMessage;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getIdRementente() {
        return idRementente;
    }

    public void setIdRementente(String idRementente) {
        this.idRementente = idRementente;
    }

    public String getIdDestinatario() {
        return idDestinatario;
    }

    public void setIdDestinatario(String idDestinatario) {
        this.idDestinatario = idDestinatario;
    }

    public String getUltimaMessagem() {
        return ultimaMessagem;
    }

    public void setUltimaMessagem(String ultimaMessagem) {
        this.ultimaMessagem = ultimaMessagem;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
