package com.example.securestorage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.securestorage.R;
import com.example.securestorage.security.SecurityAnalyzer;

import java.util.List;

public class SecurityRisksFragment extends Fragment {

    private TextView tvRisks, tvSummary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_security_risks, container, false);

        tvRisks   = view.findViewById(R.id.tvRisks);
        tvSummary = view.findViewById(R.id.tvSummary);
        Button btnAnalyze = view.findViewById(R.id.btnAnalyze);

        btnAnalyze.setOnClickListener(v -> runAnalysis());

        return view;
    }

    private void runAnalysis() {
        SecurityAnalyzer analyzer = new SecurityAnalyzer(requireContext());
        List<SecurityAnalyzer.SecurityRisk> risks = analyzer.runAnalysis();

        if (risks.isEmpty()) {
            tvSummary.setVisibility(View.VISIBLE);
            tvSummary.setText("✅ Aucun risque détecté");
            tvSummary.setBackgroundColor(0xFFE8F5E9);
            tvRisks.setText("");
            return;
        }

        // Comptage par niveau
        int high = 0, medium = 0, low = 0;
        for (SecurityAnalyzer.SecurityRisk r : risks) {
            switch (r.getLevel()) {
                case HIGH:   high++;   break;
                case MEDIUM: medium++; break;
                default:     low++;    break;
            }
        }

        // Résumé
        tvSummary.setVisibility(View.VISIBLE);
        tvSummary.setText(risks.size() + " risque(s) détecté(s) — "
                + "🔴 " + high + "  🟠 " + medium + "  🟡 " + low);
        tvSummary.setBackgroundColor(high > 0 ? 0xFFFFCDD2 : 0xFFFFF9C4);

        // Détail de chaque risque
        StringBuilder sb = new StringBuilder();
        for (SecurityAnalyzer.SecurityRisk risk : risks) {
            sb.append("━━━━━━━━━━━━━━━━━━━━━━\n")
                    .append(risk.getLevelLabel()).append(" — ").append(risk.getTitle()).append("\n\n")
                    .append("📋 ").append(risk.getDescription()).append("\n\n")
                    .append("💡 ").append(risk.getRecommendation()).append("\n\n");
        }

        tvRisks.setText(sb.toString());
    }
}