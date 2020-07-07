package com.rafaelperez.singlesignon1.utils;

import android.annotation.SuppressLint;
import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCryptor {
    private static final String ALGORITHM = "AES";
    // 16-bit Key for encryption (Change to yours)
    private static final String KEY = "XXXXXXXXXXXXXXX";

    public static String encrypt(String value) throws Exception {
        Key key = generateKey();
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(AESCryptor.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        return Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);

    }

    public static String decrypt(String value) throws Exception {
        Key key = generateKey();
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(AESCryptor.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        return new String(decryptedByteValue,"utf-8");

    }

    private static Key generateKey() {
        return new SecretKeySpec(AESCryptor.KEY.getBytes(),AESCryptor.ALGORITHM);
    }
}
