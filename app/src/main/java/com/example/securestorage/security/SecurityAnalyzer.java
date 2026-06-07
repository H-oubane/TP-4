package com.example.securestorage.security;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SecurityAnalyzer {

    private static final String TAG = "SecurityAnalyzer";
    private final Context ctx;

    public SecurityAnalyzer(Context context) {
        this.ctx = context;
    }

    public List<SecurityRisk> runAnalysis() {
        List<SecurityRisk> risks = new ArrayList<>();

        verifyBackupPolicy(risks);
        verifyStrongBox(risks);
        verifyDebugFlag(risks);
        verifyEmulator(risks);

        return risks;
    }

    private void verifyBackupPolicy(List<SecurityRisk> risks) {
        try {
            ApplicationInfo info = ctx.getPackageManager()
                    .getApplicationInfo(ctx.getPackageName(), 0);

            boolean backupOn = (info.flags & ApplicationInfo.FLAG_ALLOW_BACKUP) != 0;
            if (backupOn) {
                risks.add(new SecurityRisk(
                        "Sauvegardes ADB activées",
                        "Les données de l'app peuvent être extraites via adb backup.",
                        "Définir android:allowBackup=\"false\" dans le manifeste.",
                        SecurityRisk.Level.HIGH
                ));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Erreur vérification backup : " + e.getMessage());
        }
    }

    private void verifyStrongBox(List<SecurityRisk> risks) {
        KeystoreManager km = new KeystoreManager(ctx);
        if (!km.isStrongBoxAvailable()) {
            risks.add(new SecurityRisk(
                    "StrongBox absent",
                    "Aucun module de sécurité matériel détecté sur cet appareil.",
                    "Utiliser PBKDF2 ou un HSM logiciel comme alternative.",
                    SecurityRisk.Level.MEDIUM
            ));
        }
    }

    private void verifyDebugFlag(List<SecurityRisk> risks) {
        try {
            ApplicationInfo info = ctx.getPackageManager()
                    .getApplicationInfo(ctx.getPackageName(), 0);

            boolean debugOn = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            if (debugOn) {
                risks.add(new SecurityRisk(
                        "Mode debug actif",
                        "Un attaquant peut attacher un débogueur et inspecter la mémoire.",
                        "Désactiver debuggable en production via buildTypes dans build.gradle.",
                        SecurityRisk.Level.HIGH
                ));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Erreur vérification debug : " + e.getMessage());
        }
    }

    private void verifyEmulator(List<SecurityRisk> risks) {
        boolean onEmulator =
                Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")
                        || Build.MANUFACTURER.contains("Genymotion")
                        || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                        || "google_sdk".equals(Build.PRODUCT);

        if (onEmulator) {
            risks.add(new SecurityRisk(
                    "Exécution sur émulateur",
                    "L'environnement émulé offre moins de garanties de sécurité qu'un vrai appareil.",
                    "Tester sur un appareil physique avant tout déploiement.",
                    SecurityRisk.Level.LOW
            ));
        }
    }

    // ── Modèle de risque ─────────────────────────────────────────

    public static class SecurityRisk {

        public enum Level { LOW, MEDIUM, HIGH }

        private final String title;
        private final String description;
        private final String recommendation;
        private final Level  level;

        public SecurityRisk(String title, String description,
                            String recommendation, Level level) {
            this.title          = title;
            this.description    = description;
            this.recommendation = recommendation;
            this.level          = level;
        }

        public String getTitle()          { return title; }
        public String getDescription()    { return description; }
        public String getRecommendation() { return recommendation; }
        public Level  getLevel()          { return level; }

        public String getLevelLabel() {
            switch (level) {
                case HIGH:   return "🔴 ÉLEVÉ";
                case MEDIUM: return "🟠 MOYEN";
                default:     return "🟡 FAIBLE";
            }
        }

        @Override
        public String toString() {
            return getLevelLabel() + " — " + title + "\n"
                    + "Description : " + description + "\n"
                    + "Recommandation : " + recommendation;
        }
    }
}