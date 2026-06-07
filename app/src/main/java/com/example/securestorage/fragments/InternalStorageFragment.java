package com.example.securestorage.fragments;

import android.os.Bundle;
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
import com.example.securestorage.storage.InternalStorageManager;

public class InternalStorageFragment extends Fragment {

    private static final String DEFAULT_FILE = "notes_internes.txt";

    private EditText etFilename, etContent;
    private TextView tvFileContent;
    private InternalStorageManager storageManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_internal_storage, container, false);

        storageManager = new InternalStorageManager(requireContext());

        etFilename    = view.findViewById(R.id.etFilename);
        etContent     = view.findViewById(R.id.etContent);
        tvFileContent = view.findViewById(R.id.tvFileContent);

        Button btnSave   = view.findViewById(R.id.btnSave);
        Button btnLoad   = view.findViewById(R.id.btnLoad);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnList   = view.findViewById(R.id.btnList);

        etFilename.setText(DEFAULT_FILE);

        btnSave.setOnClickListener(v   -> handleSave());
        btnLoad.setOnClickListener(v   -> handleLoad());
        btnDelete.setOnClickListener(v -> handleDelete());
        btnList.setOnClickListener(v   -> handleList());

        return view;
    }

    private void handleSave() {
        String name = etFilename.getText().toString().trim();
        String data = etContent.getText().toString().trim();

        if (name.isEmpty() || data.isEmpty()) {
            toast("Remplissez les deux champs");
            return;
        }

        if (storageManager.saveFile(name, data)) {
            toast("Fichier enregistré ✓");
            etContent.setText("");
        } else {
            toast("Erreur lors de l'enregistrement");
        }
    }

    private void handleLoad() {
        String name = etFilename.getText().toString().trim();
        if (name.isEmpty()) { toast("Entrez un nom de fichier"); return; }

        String result = storageManager.loadFile(name);
        tvFileContent.setText(result != null ? result : "Fichier introuvable");
    }

    private void handleDelete() {
        String name = etFilename.getText().toString().trim();
        if (name.isEmpty()) { toast("Entrez un nom de fichier"); return; }

        if (storageManager.removeFile(name)) {
            toast("Fichier supprimé ✓");
            tvFileContent.setText("");
        } else {
            toast("Échec suppression");
        }
    }

    private void handleList() {
        String[] files = storageManager.getAllFiles();
        StringBuilder sb = new StringBuilder("Fichiers présents :\n");

        if (files.length == 0) {
            sb.append("— Aucun fichier trouvé");
        } else {
            for (String f : files) sb.append("• ").append(f).append("\n");
        }
        tvFileContent.setText(sb.toString());
    }

    private void toast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }


}
