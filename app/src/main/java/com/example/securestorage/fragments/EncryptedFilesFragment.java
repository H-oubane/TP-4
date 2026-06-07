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
import com.example.securestorage.security.EncryptedFileManager;
import com.example.securestorage.security.KeystoreManager;

public class EncryptedFilesFragment extends Fragment {

    private static final String DEFAULT_FILE = "secret_data.enc";

    private EditText etFilename, etContent;
    private TextView tvResult, tvStrongBox;
    private EncryptedFileManager fileManager;
    private KeystoreManager      keystoreManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_encrypted_files, container, false);

        fileManager     = new EncryptedFileManager(requireContext());
        keystoreManager = new KeystoreManager(requireContext());

        etFilename  = view.findViewById(R.id.etFilename);
        etContent   = view.findViewById(R.id.etContent);
        tvResult    = view.findViewById(R.id.tvResult);
        tvStrongBox = view.findViewById(R.id.tvStrongBox);

        Button btnWrite  = view.findViewById(R.id.btnWrite);
        Button btnRead   = view.findViewById(R.id.btnRead);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnList   = view.findViewById(R.id.btnList);

        etFilename.setText(DEFAULT_FILE);

        // Affichage du statut StrongBox
        boolean sbAvailable = keystoreManager.isStrongBoxAvailable();
        tvStrongBox.setText(sbAvailable
                ? "🔒 StrongBox disponible — clés protégées hardware"
                : "⚠️ StrongBox indisponible — protection logicielle uniquement");
        tvStrongBox.setBackgroundColor(sbAvailable
                ? 0xFFE8F5E9   // vert clair
                : 0xFFFFF9C4); // jaune clair

        btnWrite.setOnClickListener(v  -> handleWrite());
        btnRead.setOnClickListener(v   -> handleRead());
        btnDelete.setOnClickListener(v -> handleDelete());
        btnList.setOnClickListener(v   -> handleList());

        return view;
    }

    private void handleWrite() {
        String name    = etFilename.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (name.isEmpty() || content.isEmpty()) {
            toast("Remplissez les deux champs"); return;
        }

        if (fileManager.writeEncrypted(name, content)) {
            toast("Fichier chiffré ✓");
            etContent.setText("");
            tvResult.setText("Fichier \"" + name + "\" chiffré et stocké.");
        } else {
            toast("Erreur lors du chiffrement");
        }
    }

    private void handleRead() {
        String name = etFilename.getText().toString().trim();
        if (name.isEmpty()) { toast("Entrez un nom de fichier"); return; }

        String content = fileManager.readEncrypted(name);
        tvResult.setText(content != null ? content : "Fichier introuvable ou erreur de déchiffrement");
    }

    private void handleDelete() {
        String name = etFilename.getText().toString().trim();
        if (name.isEmpty()) { toast("Entrez un nom de fichier"); return; }

        if (fileManager.deleteEncrypted(name)) {
            toast("Fichier supprimé ✓");
            tvResult.setText("");
        } else {
            toast("Échec suppression");
        }
    }

    private void handleList() {
        String[] files = fileManager.listEncryptedFiles();
        StringBuilder sb = new StringBuilder("Fichiers présents :\n");

        if (files == null || files.length == 0) {
            sb.append("— Aucun fichier trouvé");
        } else {
            for (String f : files) sb.append("• ").append(f).append("\n");
        }
        tvResult.setText(sb.toString());
    }

    private void toast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}