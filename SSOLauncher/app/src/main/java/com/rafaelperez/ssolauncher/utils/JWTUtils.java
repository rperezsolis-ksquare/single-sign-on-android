package com.rafaelperez.ssolauncher.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

public class JWTUtils {

    public static String decode(String jwtEncoded) {
        try {
            String[] split = jwtEncoded.split("\\.");
            return getJson(split[1]);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
