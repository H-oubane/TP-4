package com.example.securestorage.storage;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExternalStorageManager {

    private static final String TAG = "ExternalStorageManager";
    private final Context ctx;

    public ExternalStorageManager(Context context) {
        this.ctx = context;
    }

    public boolean isWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public boolean isReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private File getTargetDir(String type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ : répertoire privé à l'app, aucune permission requise
            return ctx.getExternalFilesDir(type);
        } else {
            return (type == null)
                    ? Environment.getExternalStorageDirectory()
                    : Environment.getExternalStoragePublicDirectory(type);
        }
    }

    public boolean saveFile(String filename, String data, String type) {
        if (!isWritable()) {
            Log.e(TAG, "Stockage externe non accessible en écriture");
            return false;
        }

        File dir = getTargetDir(type);
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            Log.e(TAG, "Impossible de créer le dossier : " + dir.getAbsolutePath());
            return false;
        }

        File file = new File(dir, filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data.getBytes());
            Log.d(TAG, "Fichier écrit : " + file.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Erreur écriture : " + e.getMessage());
            return false;
        }
    }

    public String loadFile(String filename, String type) {
        if (!isReadable()) {
            Log.e(TAG, "Stockage externe non accessible en lecture");
            return null;
        }

        File file = new File(getTargetDir(type), filename);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file)))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            Log.d(TAG, "Fichier lu : " + file.getAbsolutePath());
            return sb.toString();

        } catch (IOException e) {
            Log.e(TAG, "Erreur lecture : " + e.getMessage());
            return null;
        }
    }

    public boolean removeFile(String filename, String type) {
        File file = new File(getTargetDir(type), filename);
        boolean ok = file.delete();
        Log.d(TAG, ok ? "Suppression OK" : "Échec suppression : " + file.getAbsolutePath());
        return ok;
    }

    public String[] getAllFiles(String type) {
        File dir = getTargetDir(type);
        if (dir != null && dir.isDirectory()) return dir.list();
        return new String[0];
    }
}
