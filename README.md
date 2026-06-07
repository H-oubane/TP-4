# SecureStorageApp - Android Security : TP 4

## Description

Application Android demonstrant les bonnes pratiques de stockage securise :

- Stockage Interne (openFileInput / openFileOutput)
- Stockage Externe (Scoped Storage Android 10+)
- Base de donnees chiffree avec Room + SQLCipher (AES-256)
- Fichiers chiffres avec Jetpack Security Crypto (AES-256-GCM)
- Authentification utilisateur avec PBKDF2 (hachage de mot de passe)
- Journal d'audit chiffre

## Technologies utilisees

| Technologie | Usage |
|-------------|-------|
| Room | ORM pour SQLite |
| SQLCipher | Chiffrement de la base de donnees |
| Jetpack Security Crypto | Chiffrement de fichiers |
| Android Keystore | Gestion securisee des cles |
| PBKDF2 | Hachage des mots de passe |

## Structure du projet
app/src/main/java/com/example/securestorage/

├── MainActivity.java # Interface avec 4 onglets

├── LoginActivity.java # Ecran d'authentification

├── NotesActivity.java # Gestion des notes

├── database/ # Room + SQLCipher

    ├── AppDatabase.java

    ├── User.java / Note.java

    └── UserDao.java / NoteDao.java

├── storage/ # Stockage Interne/Externe

├── security/ # Chiffrement + Keystore

├── fragments/ # Fragments pour les onglets

└── adapters/ # RecyclerView adapter


## Tests effectues

| Numero | Fonctionnalite | Statut |
|--------|----------------|--------|
| 1 | Stockage Interne | VALIDE |
| 2 | Stockage Externe | VALIDE |
| 3 | Room + SQLCipher (base chiffree) | VALIDE |
| 4 | Jetpack Security Crypto (fichiers chiffres) | VALIDE |
| 5 | Authentification PBKDF2 + Mini-SGBD | VALIDE |

## Details des tests

### Test 1 - Stockage Interne
- Creation d'un fichier test.txt avec contenu
- Lecture du fichier
- Listing des fichiers presents
- Suppression du fichier

### Test 2 - Stockage Externe
- Creation d'un fichier dans /Android/data/package/files/Documents
- Verification de la persistance
- Compatibilite Android 10+ (Scoped Storage)

### Test 3 - Base de donnees chiffree
- Creation d'un utilisateur (nom + mot de passe)
- Ajout de notes liees a l'utilisateur
- Verification que la base .db est illisible sans la cle SQLCipher

### Test 4 - Fichiers chiffres
- Chiffrement d'un fichier avec AES-256-GCM via Jetpack Security
- Dechiffrement du fichier
- Verification que le fichier stocke est binaire (illisible)

### Test 5 - Authentification + Mini-SGBD
- Inscription avec hachage PBKDF2 (sel + 10000 iterations)
- Connexion avec verification du hash
- Persistance des notes apres fermeture / reouverture de l'application


## capture video

https://github.com/user-attachments/assets/dd9c96f9-2d14-4edc-ada7-f8553096c714



## Auteur
**H-oubane**

