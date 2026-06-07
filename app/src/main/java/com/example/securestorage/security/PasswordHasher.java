package com.example.securestorage.security;

import android.util.Base64;
import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {

    private static final String TAG = "PasswordHasher";
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            new SecureRandom().nextBytes(salt);

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            // Format: salt + hash
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);

            String result = Base64.encodeToString(combined, Base64.NO_WRAP);
            Log.d(TAG, "Hash created for password: " + password + " -> " + result);
            return result;

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.e(TAG, "Hash error: " + e.getMessage());
            return null;
        }
    }

    public static boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] combined = Base64.decode(storedHash, Base64.NO_WRAP);

            // Extraire le sel
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, salt.length);

            // Extraire le hash stocké
            byte[] storedKey = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, SALT_LENGTH, storedKey, 0, storedKey.length);

            // Hasher le mot de passe fourni avec le même sel
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] testKey = factory.generateSecret(spec).getEncoded();

            // Comparaison sécurisée
            boolean valid = constantTimeEquals(storedKey, testKey);
            Log.d(TAG, "Password verification for '" + password + "': " + valid);
            return valid;

        } catch (Exception e) {
            Log.e(TAG, "Verify error: " + e.getMessage());
            return false;
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}