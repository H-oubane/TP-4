package com.example.securestorage.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.securestorage.R;
import com.example.securestorage.database.DatabaseManager;
import com.example.securestorage.database.Note;
import com.example.securestorage.database.User;

import java.util.List;

public class DatabaseFragment extends Fragment {

    private EditText  etUsername, etPassword, etNoteTitle, etNoteContent;
    private TextView  tvResult;
    private DatabaseManager dbManager;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_database, container, false);

        dbManager = new DatabaseManager(requireContext());

        etUsername   = view.findViewById(R.id.etUsername);
        etPassword   = view.findViewById(R.id.etPassword);
        etNoteTitle  = view.findViewById(R.id.etNoteTitle);
        etNoteContent= view.findViewById(R.id.etNoteContent);
        tvResult     = view.findViewById(R.id.tvResult);

        Button btnAddUser   = view.findViewById(R.id.btnAddUser);
        Button btnListUsers = view.findViewById(R.id.btnListUsers);
        Button btnAddNote   = view.findViewById(R.id.btnAddNote);
        Button btnListNotes = view.findViewById(R.id.btnListNotes);

        btnAddUser.setOnClickListener(v   -> handleAddUser());
        btnListUsers.setOnClickListener(v -> handleListUsers());
        btnAddNote.setOnClickListener(v   -> handleAddNote());
        btnListNotes.setOnClickListener(v -> handleListNotes());

        return view;
    }

    private void handleAddUser() {
        String name = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (name.isEmpty() || pass.isEmpty()) { toast("Remplissez les deux champs"); return; }

        User user = new User(name, name + "@secure.app", pass);
        dbManager.addUser(user, new DatabaseManager.DbCallback<Long>() {
            @Override public void onSuccess(Long id) {
                uiHandler.post(() -> {
                    tvResult.setText("Utilisateur ajouté — ID : " + id);
                    etUsername.setText("");
                    etPassword.setText("");
                });
            }
            @Override public void onFailure(Exception e) {
                uiHandler.post(() -> toast("Erreur : " + e.getMessage()));
            }
        });
    }

    private void handleListUsers() {
        dbManager.listUsers(new DatabaseManager.DbCallback<List<User>>() {
            @Override public void onSuccess(List<User> users) {
                uiHandler.post(() -> {
                    StringBuilder sb = new StringBuilder("Utilisateurs :\n");
                    if (users.isEmpty()) sb.append("— Aucun");
                    else for (User u : users)
                        sb.append("• [").append(u.getId()).append("] ").append(u.getUsername()).append("\n");
                    tvResult.setText(sb.toString());
                });
            }
            @Override public void onFailure(Exception e) {
                uiHandler.post(() -> toast("Erreur : " + e.getMessage()));
            }
        });
    }

    private void handleAddNote() {
        String title   = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) { toast("Remplissez titre et contenu"); return; }

        // userId fixé à 1 pour la démonstration
        Note note = new Note(1, title, content);
        dbManager.addNote(note, new DatabaseManager.DbCallback<Long>() {
            @Override public void onSuccess(Long id) {
                uiHandler.post(() -> {
                    tvResult.setText("Note ajoutée — ID : " + id);
                    etNoteTitle.setText("");
                    etNoteContent.setText("");
                });
            }
            @Override public void onFailure(Exception e) {
                uiHandler.post(() -> toast("Erreur : " + e.getMessage()));
            }
        });
    }

    private void handleListNotes() {
        dbManager.getNotesForUser(1, new DatabaseManager.DbCallback<List<Note>>() {
            @Override public void onSuccess(List<Note> notes) {
                uiHandler.post(() -> {
                    StringBuilder sb = new StringBuilder("Notes (userId=1) :\n");
                    if (notes.isEmpty()) sb.append("— Aucune");
                    else for (Note n : notes)
                        sb.append("• [").append(n.getId()).append("] ").append(n.getTitle()).append("\n");
                    tvResult.setText(sb.toString());
                });
            }
            @Override public void onFailure(Exception e) {
                uiHandler.post(() -> toast("Erreur : " + e.getMessage()));
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}