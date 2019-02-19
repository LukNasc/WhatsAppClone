package com.example.whatsapp.util;

import android.util.Base64;

public class Base64Custom {
    public static String encodeBase64(String s){
       return Base64.encodeToString(s.getBytes(), Base64.DEFAULT).replaceAll("[\\r | \\n]","");
    }

    public static String decodeBase64(String s){
        return new String( Base64.decode(s ,Base64.DEFAULT));
    }
}
