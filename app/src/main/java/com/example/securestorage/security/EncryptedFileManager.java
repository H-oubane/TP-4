package com.example.securestorage.security;

import android.content.Context;
import android.util.Log;

import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKey;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class EncryptedFileManager {

    private static final String TAG = "EncryptedFileManager";
    private final Context ctx;
    private MasterKey masterKey;

    public EncryptedFileManager(Context context) {
        this.ctx = context;
        initMasterKey();
    }

    private void initMasterKey() {
        try {
            masterKey = new MasterKey.Builder(ctx)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .setUserAuthenticationRequired(false)
                    .build();
            Log.d(TAG, "MasterKey initialisée");
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Échec init MasterKey : " + e.getMessage());
        }
    }

    public boolean writeEncrypted(String filename, String content) {
        if (masterKey == null) { Log.e(TAG, "MasterKey null"); return false; }

        try {
            File target = new File(ctx.getFilesDir(), filename);

            // Si le fichier existe déjà, on le supprime avant réécriture
            if (target.exists()) target.delete();

            EncryptedFile ef = new EncryptedFile.Builder(
                    ctx, target, masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB)
                    .build();

            try (OutputStream os = ef.openFileOutput()) {
                os.write(content.getBytes(StandardCharsets.UTF_8));
            }

            Log.d(TAG, "Fichier chiffré écrit : " + filename);
            return true;

        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Erreur écriture chiffrée : " + e.getMessage());
            return false;
        }
    }

    public String readEncrypted(String filename) {
        if (masterKey == null) { Log.e(TAG, "MasterKey null"); return null; }

        try {
            File target = new File(ctx.getFilesDir(), filename);
            if (!target.exists()) { Log.e(TAG, "Fichier absent : " + filename); return null; }

            EncryptedFile ef = new EncryptedFile.Builder(
                    ctx, target, masterKey,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB)
                    .build();

            try (InputStream is = ef.openFileInput();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buf = new byte[1024];
                int read;
                while ((read = is.read(buf)) != -1) baos.write(buf, 0, read);

                Log.d(TAG, "Fichier chiffré lu : " + filename);
                return baos.toString(StandardCharsets.UTF_8.name());
            }

        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Erreur lecture chiffrée : " + e.getMessage());
            return null;
        }
    }

    public boolean deleteEncrypted(String filename) {
        File target = new File(ctx.getFilesDir(), filename);
        boolean ok = target.delete();
        Log.d(TAG, ok ? "Fichier supprimé : " + filename : "Échec suppression : " + filename);
        return ok;
    }

    public String[] listEncryptedFiles() {
        return ctx.fileList();
    }
}