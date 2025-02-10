# 📌 README - SAEdiscord  
Un projet Android permettant d'afficher la liste des utilisateurs et leurs messages depuis une base de données MySQL.  

---

## 🚀 Présentation  
SAEdiscord est une application Android qui récupère et affiche :  
✔️ La liste des utilisateurs stockés dans une base de données MySQL.  
✔️ Les messages envoyés par un utilisateur lorsqu'on clique sur son nom.  
✔️ Des détails sur chaque message (contenu, destinataire, score de toxicité, etc.).  

L’application communique avec un serveur PHP via des requêtes API REST et utilise Retrofit pour gérer les appels réseau.  

---

## 🛠️ Technologies utilisées  
- **Java** (Développement Android)  
- **Retrofit 2** (Appels API)  
- **MySQL** (Base de données)  
- **PHP** (Backend API)  
- **JSON** (Format d’échange des données)  
- **Android Studio** (IDE de développement)  

---

## 📂 Structure du projet  
```plaintext
SAEdiscord/
│── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/saediscord/
│   │   │   │   ├── MainActivity.java            # Liste des utilisateurs  
│   │   │   │   ├── MessagesActivity.java        # Liste des messages d’un utilisateur  
│   │   │   │   ├── ApiService.java              # Interface Retrofit  
│   │   │   │   ├── ApiClient.java               # Initialisation Retrofit  
│   │   │   │   ├── DatabaseHelper.java          # Gestion des requêtes SQLite/MySQL  
│   │   │   │   ├── Message.java                 # Modèle de données  
│   │   │   │   ├── MessagesAdapter.java         # Adapter pour afficher les messages  
│   │   │   │   ├── UsersAdapter.java            # Adapter pour afficher les utilisateurs  
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml        # UI liste des utilisateurs  
│   │   │   │   │   ├── activity_messages.xml    # UI liste des messages  
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml              # Textes de l’UI  
│── server/                                      # Serveur API PHP  
│   ├── get_users.php                            # Récupération des utilisateurs  
│   ├── get_messages.php                         # Récupération des messages d’un utilisateur  
│   ├── config.php                               # Configuration MySQL  
│── README.md                                    # Documentation  
```
---


## ⚙️ Installation & Configuration  

Pour installer et configurer le projet, veuillez consulter le document :  
📄 **[Installation.docx](./Installation.docx)**  

Ce document contient toutes les instructions détaillées pour :  
✔️ Installer la base de données MySQL  
✔️ Configurer le serveur PHP  
✔️ Tester l'API  
✔️ Lancer l'application Android  
✔️ Déboguer les erreurs courantes  


🚀 Développé par
Bastien Labeste, Robin Kwiatkowski et Quentin Chambelland

📅 Dernière mise à jour : Février 2025
📧 Contact : bastienlabeste@gmail.com
