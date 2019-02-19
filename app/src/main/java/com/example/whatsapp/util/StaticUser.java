package com.example.whatsapp.util;

public class StaticUser {
    private static String ID_USER_DESTINO;

    public static String getIdUserDestino() {
        return ID_USER_DESTINO;
    }

    public static void setIdUserDestino(String idUserDestino) {
        ID_USER_DESTINO = idUserDestino;
    }
}
