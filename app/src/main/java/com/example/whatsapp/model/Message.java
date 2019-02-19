package com.example.whatsapp.model;

public class Message {
    private String idUser;
    private String image;
    private String message;
    private String nome;
    private Usuario userRemetente;

    public Message() {
        this.setNome("");
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Usuario getUserRemetente() {
        return userRemetente;
    }

    public void setUserRemetente(Usuario userRemetente) {
        this.userRemetente = userRemetente;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
