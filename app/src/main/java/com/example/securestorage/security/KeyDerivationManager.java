package com.example.securestorage.security;

import android.util.Log;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyDerivationManager {

    private static final String TAG = "KeyDerivationManager";

    public static byte[] deriveKey(char[] password, byte[] salt,
                                   int iterations, int keyBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] derived = skf.generateSecret(spec).getEncoded();
            Log.d(TAG, "Clé dérivée via PBKDF2");
            return derived;
        } catch (GeneralSecurityException e) {
            Log.e(TAG, "Échec dérivation : " + e.getMessage());
            return null;
        }
    }

    public static byte[] generateSalt(int byteLength) {
        byte[] salt = new byte[byteLength];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    public static SecretKey toAesKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static SecretKey deriveAesKey(char[] password, byte[] salt) {
        byte[] raw = deriveKey(password, salt, 10000, 256);
        return (raw != null) ? toAesKey(raw) : null;
    }
}