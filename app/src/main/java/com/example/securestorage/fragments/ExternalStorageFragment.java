package com.example.securestorage.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.securestorage.R;
import com.example.securestorage.storage.ExternalStorageManager;

public class ExternalStorageFragment extends Fragment {

    private static final String DEFAULT_FILE = "notes_externes.txt";
    private static final int PERM_CODE = 200;

    private EditText etFilename, etContent;
    private TextView tvFileContent;
    private ExternalStorageManager storageManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_external_storage, container, false);

        storageManager = new ExternalStorageManager(requireContext());

        etFilename    = view.findViewById(R.id.etFilename);
        etContent     = view.findViewById(R.id.etContent);
        tvFileContent = view.findViewById(R.id.tvFileContent);

        Button btnSave   = view.findViewById(R.id.btnSave);
        Button btnLoad   = view.findViewById(R.id.btnLoad);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnList   = view.findViewById(R.id.btnList);

        etFilename.setText(DEFAULT_FILE);

        btnSave.setOnClickListener(v   -> { if (hasPermission()) handleSave(); });
        btnLoad.setOnClickListener(v   -> { if (hasPermission()) handleLoad(); });
        btnDelete.setOnClickListener(v -> { if (hasPermission()) handleDelete(); });
        btnList.setOnClickListener(v   -> { if (hasPermission()) handleList(); });

        return view;
    }

    private boolean hasPermission() {
        // Android 10+ : scoped storage, aucune permission nécessaire
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return true;

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int code,
                                           @NonNull String[] perms,
                                           @NonNull int[] results) {
        if (code == PERM_CODE) {
            toast(results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED
                    ? "Permission accordée ✓"
                    : "Permission refusée ✗");
        }
    }

    private void handleSave() {
        String name = etFilename.getText().toString().trim();
        String data = etContent.getText().toString().trim();

        if (name.isEmpty() || data.isEmpty()) { toast("Remplissez les deux champs"); return; }

        if (storageManager.saveFile(name, data, Environment.DIRECTORY_DOCUMENTS)) {
            toast("Fichier enregistré ✓");
            etContent.setText("");
        } else {
            toast("Erreur lors de l'enregistrement");
        }
    }

    private void handleLoad() {
        String name = etFilename.getText().toString().trim();
        if (name.isEmpty()) { toast("Entrez un nom de fichier"); return; }

        String result = storageManager.loadFile(name, Environment.DIRECTORY_DOCUMENTS);
        tvFileContent.setText(result != null ? result : "Fichier introuvable");
    }

    private void handleDelete() {
        String name = etFilename.getText().toString().trim();
        if (name.isEmpty()) { toast("Entrez un nom de fichier"); return; }

        if (storageManager.removeFile(name, Environment.DIRECTORY_DOCUMENTS)) {
            toast("Fichier supprimé ✓");
            tvFileContent.setText("");
        } else {
            toast("Échec suppression");
        }
    }

    private void handleList() {
        String[] files = storageManager.getAllFiles(Environment.DIRECTORY_DOCUMENTS);
        StringBuilder sb = new StringBuilder("Fichiers présents :\n");

        if (files == null || files.length == 0) {
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
