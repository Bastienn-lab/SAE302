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
│──│── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/example/saediscord/
│   │   │   │   │   ├── MainActivity.java            # Liste des utilisateurs  
│   │   │   │   │   ├── MessagesActivity.java        # Liste des messages d’un utilisateur  
│   │   │   │   │   ├── ApiService.java              # Interface Retrofit  
│   │   │   │   │   ├── ApiClient.java               # Initialisation Retrofit  
│   │   │   │   │   ├── DatabaseHelper.java          # Gestion des requêtes SQLite/MySQL  
│   │   │   │   │   ├── Message.java                 # Modèle de données
│   │   │   │   │   ├── User.java                    # Modèle de données
│   │   │   │   │   ├── MessagesAdapter.java         # Adapter pour afficher les messages  
│   │   │   │   ├── res/
│   │   │   │   │   ├── layout/
│   │   │   │   │   │   ├── activity_main.xml        # UI liste des utilisateurs  
│   │   │   │   │   │   ├── activity_messages.xml    # UI liste des messages
│   │   │   │   │   │   ├── item_message.xml         # 
│   │   │   │   │   ├── values/
│   │   │   │   │   │   ├── strings.xml              # Textes de l’UI
│   │   │   │   │   │   ├── colors.xml               #
│   │   │   │   │   │   ├── themes.xml               #
│── TraceCordserver/                                      # Serveur API PHP  
│   ├── get_users.php                            # Récupération des utilisateurs  
│   ├── get_messages.php                         # Récupération des messages d’un utilisateur  
│   ├── config.php                               # Configuration MySQL
|   ├── bot_discord.py                           # Programme du Bot
|   ├── DiscordDataProcessor.java                # Code java pour exporter les infos dans la BDD
|   ├── selected_messages.txt                    # Fichier texte contenant les messages à exporter
│── README.md                                    # Documentation
│── Installation.pdf                             # Document d'installation
│── Présentation-TraceCord.pdf                   # Diaporama de présentation
│── Fonctionnement.pdf                           # Document décrivant l'architecture du projet
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
