# 📌 README - TraceCord  
Un projet Android permettant d'afficher la liste des utilisateurs et leurs messages depuis une base de données MySQL puis d'analyser les messages afin d'afficher les détails associés : contenu,destinataire, taille du message et score de toxicité.

---

## 🚀 Présentation  
TraceCord est une application Android qui récupère et affiche :  
✔️ La liste des utilisateurs stockés dans une base de données MySQL.  
✔️ Les messages envoyés par un utilisateur lorsqu'on clique sur son nom.  
✔️ Des détails sur chaque message (contenu, destinataire, score de toxicité, etc.).  
✔️ Des détails généraux sur l'utilisateur ( longueur moyenne des messages, destinataire le plus courant, score de toxicité moyen et nombre de messages envoyés).  
✔️ Un classement de la personne la moins toxique à la plus toxique en fonction de différents critères.  

L’application communique avec un serveur PHP via des requêtes API REST et utilise Retrofit pour gérer les appels réseau.  

---

## 🛠️ Technologies utilisées  
- **Java** (Développement Android)  
- **Retrofit 2** (Appels API)  
- **MySQL** (Base de données)  
- **PHP** (Backend API)  
- **JSON** (Format d’échange des données)  
- **Android Studio** (IDE de développement)
- **Python** (Bot discord)

---

## 📂 Structure du projet  
```plaintext
TraceCord/
│── TraceCordApp/
│── │── java/com/example/saediscord/
│   |   ├── MainActivity.java            # Liste des utilisateurs  
│   |   ├── MessagesActivity.java        # Liste des messages d’un utilisateur  
│   |   ├── ApiService.java              # Interface Retrofit  
│   |   ├── ApiClient.java               # Initialisation Retrofit  
│   |   ├── DatabaseHelper.java          # Gestion des requêtes SQLite/MySQL  
│   |   ├── Message.java                 # Modèle de données (message)  
│   |   ├── User.java                    # Modèle de données (utilisateur)  
│   |   ├── MessagesAdapter.java         # Adapter pour afficher les messages  
│   |   ├── ClassementActivity.java      # Affichage du classement des utilisateurs  
│   |   ├── ClassementAdapter.java       # Adapter pour afficher le classement  
│   |   ├── Player.java                  # Modèle de données pour un joueur (classement)  
│   |   ├── UsersAdapter.java            # Adapter pour afficher la liste des utilisateurs  
│── │── layout/
│   |   ├── activity_main.xml        # UI liste des utilisateurs  
│   |   ├── activity_messages.xml    # UI liste des messages  
│   |   ├── item_message.xml         # UI pour un message individuel dans la liste  
│── │── drawable/
│   |   ├── crown.png                # Image pour le premier du classement  
│   |   ├── medal_silver.png         # Image pour le deuxième du classement  
│   |   ├── medal_bronze.png         # Image pour le troisième du classement  
│   |   ├── logo.png                 # Logo de l’application  
│   |   ├── ...                      # Autres ressources graphiques  
│── │── manifests/
│   |   ├── AndroidManifest.xml      # Déclaration des composants et permissions de l’application  
│── │── Gradle Scripts/
│   |   ├── build.gradle.kts         # Configuration des dépendances et du projet Gradle  
│── TraceCordServer/                                     
│   ├── get_users.php                            # Récupération des utilisateurs depuis la BDD  
│   ├── get_messages.php                         # Récupération des messages d’un utilisateur depuis la BDD  
|   ├── bot_discord.py                           # Programme du Bot Discord pour analyser les messages  
|   ├── DiscordDataProcessor.java                # Exportation des messages vers la BDD  
|   ├── DiscordDataProcessor.class               # Fichier compilé du programme Java  
|   ├── selected_messages.txt                    # Fichier texte contenant les messages à exporter  
│── README.md                                    # Documentation du projet  
│── Installation.pdf                             # Guide d’installation du projet  
│── Présentation-TraceCord.pdf                   # Diaporama de présentation du projet  
│── Fonctionnement.pdf                           # Explication de l’architecture et des interactions du projet  
│── RACI.pdf                                     # Rôles et responsabilités de l’équipe (RACI)  
```
---


## ⚙️ Installation & Configuration  

Pour installer et configurer le projet, veuillez consulter le document :  
📄 **[Installation.pdf](./Installation.pdf)**  

Ce document contient toutes les instructions détaillées pour :  
✔️ Installer la base de données MySQL  
✔️ Configurer le serveur PHP  
✔️ Déployer l'API PHP et le bot Python  
✔️ Tester l'API  et le bot  
✔️ Lancer l'application Android  
✔️ Déboguer les erreurs courantes  


🚀 Développé par
Bastien Labeste, Robin Kwiatkowski et Quentin Chambelland

📅 Dernière mise à jour : 16 Février 2025
📧 Contact : bastienlabeste@gmail.com
