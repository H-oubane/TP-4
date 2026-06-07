package com.example.securestorage.database;

import android.content.Context;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatabaseManager {

    private static final String TAG          = "DatabaseManager";
    private static final String PREFS_NAME   = "db_secure_prefs";
    private static final String PREFS_KEY    = "passphrase";
    private static final int    PASS_LENGTH  = 32;

    private final Context  ctx;
    private final Executor executor;
    private AppDatabase    db;

    public DatabaseManager(Context context) {
        this.ctx      = context;
        this.executor = Executors.newSingleThreadExecutor();
        setup();
    }

    private void setup() {
        try {
            char[] pass = fetchOrCreatePassphrase();
            db = AppDatabase.getInstance(ctx, pass);
            Log.d(TAG, "Base de données prête");
        } catch (Exception e) {
            Log.e(TAG, "Échec initialisation BDD : " + e.getMessage());
        }
    }

    private char[] fetchOrCreatePassphrase() throws GeneralSecurityException, IOException {
        MasterKey masterKey = new MasterKey.Builder(ctx)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        EncryptedSharedPreferences prefs = (EncryptedSharedPreferences)
                EncryptedSharedPreferences.create(
                        ctx, PREFS_NAME, masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

        String stored = prefs.getString(PREFS_KEY, null);
        if (stored == null) {
            char[] fresh = AppDatabase.buildRandomPassphrase(PASS_LENGTH);
            prefs.edit().putString(PREFS_KEY, new String(fresh)).apply();
            Log.d(TAG, "Nouveau passphrase généré");
            return fresh;
        }
        Log.d(TAG, "Passphrase existant récupéré");
        return stored.toCharArray();
    }

    // ── Utilisateurs ──────────────────────────────────────────────

    public void addUser(User user, DbCallback<Long> cb) {
        executor.execute(() -> {
            try { cb.onSuccess(db.userDao().insert(user)); }
            catch (Exception e) { cb.onFailure(e); }
        });
    }

    public void findUser(String username, DbCallback<User> cb) {
        executor.execute(() -> {
            try { cb.onSuccess(db.userDao().findByUsername(username)); }
            catch (Exception e) { cb.onFailure(e); }
        });
    }

    public void listUsers(DbCallback<List<User>> cb) {
        executor.execute(() -> {
            try { cb.onSuccess(db.userDao().fetchAll()); }
            catch (Exception e) { cb.onFailure(e); }
        });
    }

    // ── Notes ─────────────────────────────────────────────────────

    public void addNote(Note note, DbCallback<Long> cb) {
        executor.execute(() -> {
            try { cb.onSuccess(db.noteDao().insert(note)); }
            catch (Exception e) { cb.onFailure(e); }
        });
    }

    public void getNotesForUser(int userId, DbCallback<List<Note>> cb) {
        executor.execute(() -> {
            try { cb.onSuccess(db.noteDao().findByUser(userId)); }
            catch (Exception e) { cb.onFailure(e); }
        });
    }

    // ── Callback générique ────────────────────────────────────────

    public interface DbCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}