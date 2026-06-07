package com.example.securestorage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.securestorage.database.DatabaseManager;
import com.example.securestorage.database.User;
import com.example.securestorage.security.PasswordHasher;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private DatabaseManager dbManager;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbManager  = new DatabaseManager(this);
        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);

        Button btnLogin    = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v    -> handleLogin());
        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleLogin() {
        String name = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (name.isEmpty() || pass.isEmpty()) {
            toast("Remplissez tous les champs"); return;
        }

        dbManager.findUser(name, new DatabaseManager.DbCallback<User>() {
            @Override public void onSuccess(User user) {
                uiHandler.post(() -> {
                    if (user == null) {
                        toast("Utilisateur introuvable");
                    } else if (PasswordHasher.verifyPassword(pass, user.getPassword())) {
                        toast("Connexion réussie ✓");
                        Intent intent = new Intent(LoginActivity.this, NotesActivity.class);
                        intent.putExtra("USER_ID",  user.getId());
                        intent.putExtra("USERNAME", user.getUsername());
                        startActivity(intent);
                        finish();
                    } else {
                        toast("Mot de passe incorrect");
                    }
                });
            }
            @Override public void onFailure(Exception e) {
                uiHandler.post(() -> toast("Erreur : " + e.getMessage()));
            }
        });
    }

    private void handleRegister() {
        String name = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (name.isEmpty() || pass.isEmpty()) {
            toast("Remplissez tous les champs"); return;
        }
        if (pass.length() < 8) {
            toast("Mot de passe trop court (min 8 caractères)"); return;
        }

        String hashed = PasswordHasher.hashPassword(pass);
        User newUser  = new User(name, name + "@secure.app", hashed);

        dbManager.addUser(newUser, new DatabaseManager.DbCallback<Long>() {
            @Override public void onSuccess(Long id) {
                uiHandler.post(() -> {
                    toast("Inscription réussie ✓");
                    handleLogin();
                });
            }
            @Override public void onFailure(Exception e) {
                uiHandler.post(() -> toast("Erreur inscription : " + e.getMessage()));
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}