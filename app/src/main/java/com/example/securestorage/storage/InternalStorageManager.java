package com.example.securestorage.storage;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class InternalStorageManager {

    private static final String TAG = "InternalStorageManager";
    private final Context ctx;

    public InternalStorageManager(Context context) {
        this.ctx = context;
    }

    public boolean saveFile(String filename, String data) {
        try (FileOutputStream fos = ctx.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(data.getBytes());
            Log.d(TAG, "Sauvegarde OK : " + filename);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Échec sauvegarde : " + e.getMessage());
            return false;
        }
    }

    public String loadFile(String filename) {
        try (FileInputStream fis = ctx.openFileInput(filename);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            Log.d(TAG, "Lecture OK : " + filename);
            return sb.toString();

        } catch (IOException e) {
            Log.e(TAG, "Échec lecture : " + e.getMessage());
            return null;
        }
    }

    public boolean removeFile(String filename) {
        boolean deleted = ctx.deleteFile(filename);
        Log.d(TAG, deleted ? "Suppression OK" : "Échec suppression");
        return deleted;
    }

    public String[] getAllFiles() {
        return ctx.fileList();
    }
}
