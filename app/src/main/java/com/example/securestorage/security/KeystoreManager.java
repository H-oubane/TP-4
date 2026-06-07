package com.example.securestorage.security;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;

import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeystoreManager {

    private static final String TAG           = "KeystoreManager";
    private static final String KEYSTORE_NAME = "AndroidKeyStore";
    private static final String SB_KEY_ALIAS  = "strongbox_master_key";

    private final Context ctx;

    public KeystoreManager(Context context) {
        this.ctx = context;
    }

    public boolean isStrongBoxAvailable() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false;

        try {
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    "sb_probe_key",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .setIsStrongBoxBacked(true)
                    .build();

            KeyGenerator kg = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_NAME);
            kg.init(spec);
            kg.generateKey();

            KeyStore ks = KeyStore.getInstance(KEYSTORE_NAME);
            ks.load(null);
            ks.deleteEntry("sb_probe_key");

            return true;
        } catch (Exception e) {
            Log.d(TAG, "StrongBox indisponible : " + e.getMessage());
            return false;
        }
    }

    public MasterKey buildMasterKey() throws GeneralSecurityException, IOException {
        if (isStrongBoxAvailable() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                        SB_KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .setIsStrongBoxBacked(true)
                        .build();

                KeyGenerator kg = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_NAME);
                kg.init(spec);
                kg.generateKey();
                Log.d(TAG, "Clé StrongBox générée");
            } catch (Exception e) {
                Log.w(TAG, "Échec StrongBox, fallback standard : " + e.getMessage());
            }
        }

        MasterKey masterKey = new MasterKey.Builder(ctx, SB_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .setUserAuthenticationRequired(false)
                .build();

        Log.d(TAG, "MasterKey prête");
        return masterKey;
    }

    public boolean generateAesKey(String alias) {
        try {
            KeyGenParameterSpec spec = new KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build();

            KeyGenerator kg = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_NAME);
            kg.init(spec);
            kg.generateKey();

            Log.d(TAG, "Clé AES générée : " + alias);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erreur génération clé : " + e.getMessage());
            return false;
        }
    }

    public SecretKey retrieveKey(String alias) {
        try {
            KeyStore ks = KeyStore.getInstance(KEYSTORE_NAME);
            ks.load(null);
            KeyStore.SecretKeyEntry entry =
                    (KeyStore.SecretKeyEntry) ks.getEntry(alias, null);

            if (entry != null) {
                Log.d(TAG, "Clé récupérée : " + alias);
                return entry.getSecretKey();
            }
            Log.e(TAG, "Clé introuvable : " + alias);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Erreur récupération clé : " + e.getMessage());
            return null;
        }
    }

    public boolean removeKey(String alias) {
        try {
            KeyStore ks = KeyStore.getInstance(KEYSTORE_NAME);
            ks.load(null);
            ks.deleteEntry(alias);
            Log.d(TAG, "Clé supprimée : " + alias);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Erreur suppression clé : " + e.getMessage());
            return false;
        }
    }
}