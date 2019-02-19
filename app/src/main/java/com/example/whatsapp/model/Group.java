package com.example.whatsapp.model;

import com.example.whatsapp.util.Base64Custom;
import com.example.whatsapp.util.SettingsFirebase;
import com.example.whatsapp.util.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    private String id;
    private String nome;
    private String foto;
    private String numberCreator;
    private List<Usuario> membros;

    public Group() {
        DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
        DatabaseReference groupReference = database.child("grupos");
        String idGroupFirebase = groupReference.push().getKey();
        setId(idGroupFirebase);

       this.setNumberCreator(UsuarioFirebase.getDateUserCurrent().getNumber());
    }



    public void salvar(){
        DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
        DatabaseReference groupReference = database.child("grupos");

        groupReference.child(getId())
                .setValue(this);



        for (Usuario membros:getMembros()){
            String idRemetente = Base64Custom.encodeBase64(membros.getNumber().substring(membros.getNumber().length()-4));
            String idDestin = getId();

            Conversations conversations = new Conversations();
            conversations.setIdRementente(idRemetente);
            conversations.setIdDestinatario(idDestin);
            conversations.setUltimaMessagem(UsuarioFirebase.getDateUserCurrent().getNumber()+" criou o grupo \""+getNome()+"\"");
            conversations.setIsGroup("true");
            conversations.setGroup(this);
            conversations.salvar();

        }
    }

    public String getNumberCreator() {
        return numberCreator;
    }

    public void setNumberCreator(String numberCreator) {
        this.numberCreator = numberCreator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }
}
