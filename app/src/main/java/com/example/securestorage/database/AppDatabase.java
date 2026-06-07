package com.example.securestorage.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import java.security.SecureRandom;

@Database(entities = {User.class, Note.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "secure_app.db";
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract NoteDao noteDao();

    public static synchronized AppDatabase getInstance(Context ctx, char[] passphrase) {
        if (instance == null) {
            // Conversion du passphrase pour SQLCipher
            byte[] passphraseBytes = SQLiteDatabase.getBytes(passphrase);
            SupportFactory factory = new SupportFactory(passphraseBytes);

            instance = Room.databaseBuilder(ctx.getApplicationContext(),
                            AppDatabase.class, DB_NAME)
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public static char[] buildRandomPassphrase(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom rng = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = chars.charAt(rng.nextInt(chars.length()));
        }
        return result;
    }
}