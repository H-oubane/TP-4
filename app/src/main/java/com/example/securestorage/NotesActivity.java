package com.example.securestorage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.securestorage.adapters.NoteAdapter;
import com.example.securestorage.database.DatabaseManager;
import com.example.securestorage.database.Note;
import com.example.securestorage.security.EncryptedFileManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesActivity extends AppCompatActivity {

    private EditText etNoteTitle, etNoteContent;
    private DatabaseManager      dbManager;
    private EncryptedFileManager fileManager;
    private NoteAdapter          adapter;
    private List<Note>           notesList;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private int    userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        userId   = getIntent().getIntExtra("USER_ID", -1);
        username = getIntent().getStringExtra("USERNAME");

        if (userId == -1) { finish(); return; }

        dbManager   = new DatabaseManager(this);
        fileManager = new EncryptedFileManager(this);

        TextView tvUsername = findViewById(R.id.tvUsername);
        tvUsername.setText("Notes de " + username);

        etNoteTitle   = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        Button btnSave = findViewById(R.id.btnSaveNote);

        notesList = new ArrayList<>();
        adapter   = new NoteAdapter(notesList);

        RecyclerView rv = findViewById(R.id.rvNotes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        btnSave.setOnClickListener(v -> handleSave());
        loadNotes();
    }

    private void handleSave() {
        String title   = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            toast("Remplissez titre et contenu"); return;
        }

        Note note = new Note(userId, title, content);
        dbManager.addNote(note, new DatabaseManager.DbCallback<Long>() {
            @Override public void onSuccess(Long id) {
                uiHandler.post(() -> {
                    note.setId(id.intValue());
                    notesList.add(0, note);
                    adapter.notifyItemInserted(0);
                    etNoteTitle.setText("");
                    etNoteContent.setText("");
                    writeLog("Note créée : " + title);
                    toast("Note enregistrée ✓");
                });
            }
            @Override public void onFailure(Exception e) {
                uiHandler.post(() -> toast("Erreur : " + e.getMessage()));
            }
        });
    }

    private void loadNotes() {
        dbManager.getNotesForUser(userId, new DatabaseManager.DbCallback<List<Note>>() {
            @Override public void onSuccess(List<Note> notes) {
                uiHandler.post(() -> {
                    notesList.clear();
                    notesList.addAll(notes);
                    adapter.notifyDataSetChanged();
                    writeLog("Chargement : " + notes.size() + " note(s)");
                });
            }
            @Override public void onFailure(Exception e) {
                uiHandler.post(() -> toast("Erreur chargement : " + e.getMessage()));
            }
        });
    }

    private void writeLog(String action) {
        String ts  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        String log = ts + " | " + username + " | " + action + "\n";

        String filename = "journal_" + userId + ".enc";
        String existing = fileManager.readEncrypted(filename);
        fileManager.writeEncrypted(filename, log + (existing != null ? existing : ""));
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}