------------------------------------📌 README - SAEdiscord ------------------------------------
Un projet Android permettant d'afficher la liste des utilisateurs et leurs messages depuis une base de données MySQL.

------------------------------------🚀 Présentation ------------------------------------
SAEdiscord est une application Android qui récupère et affiche :
✔️ La liste des utilisateurs stockés dans une base de données MySQL.
✔️ Les messages envoyés par un utilisateur lorsqu'on clique sur son nom.
✔️ Des détails sur chaque message (contenu, destinataire, score de toxicité, etc.).

L’application communique avec un serveur PHP via des requêtes API REST et utilise Retrofit pour gérer les appels réseau.
------------------------------------------------------------------------------------------

------------------------------------🛠️ Technologies utilisées------------------------------------
Java (Développement Android)
Retrofit 2 (Appels API)
MySQL (Base de données)
PHP (Backend API)
JSON (Format d’échange des données)
Android Studio (IDE de développement)
------------------------------------------------------------------------------------------

------------------------------------📂 Structure du projet------------------------------------
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
------------------------------------------------------------------------------------------

------------------------------------⚙️ Installation & Configuration------------------------------------
1️⃣ Installation de la base de données
CREATE DATABASE Discord;
USE Discord;

CREATE TABLE messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom_utilisateur VARCHAR(255),
    message TEXT,
    date DATETIME,
    receveur VARCHAR(255),
    message_length INT
);
------------------------------------------------------------------------------------------

------------------------------------📌 Vérifier que des données existent !------------------------------------
SELECT * FROM messages;

------------------------------------2️⃣ Configurer le serveur PHP------------------------------------
📌 Modifier config.php avec vos identifiants MySQL.
<?php
$host = "localhost";
$dbname = "Discord";
$user = "root";
$pass = "root";
?>
------------------------------------------------------------------------------------------

------------------------------------📌 Placer les fichiers PHP (get_users.php et get_messages.php) dans /var/www/html/.------------------------------------
sudo mv get_users.php /var/www/html/
sudo mv get_messages.php /var/www/html/
------------------------------------------------------------------------------------------

------------------------------------📌 Redémarrer Apache pour activer les changements.------------------------------------
sudo systemctl restart apache2
------------------------------------------------------------------------------------------

------------------------------------3️⃣ Tester l’API------------------------------------
Liste des utilisateurs
👉 http://10.3.122.108/get_users.php

Messages d’un utilisateur
👉 http://10.3.122.108/get_messages.php?username=lestuff

Si tout fonctionne bien, vous devriez voir des données en JSON.
------------------------------------------------------------------------------------------

------------------------------------📲 Lancer l’application Android------------------------------------
Ouvrir le projet dans Android Studio.
Vérifier la configuration du Gradle (app/build.gradle):
dependencies {
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
}

Lancer l’émulateur ou connecter un téléphone en mode développeur.
Exécuter le projet en cliquant sur ▶️ dans Android Studio.
------------------------------------------------------------------------------------------

------------------------------------🐞 Débogage et Problèmes Courants------------------------------------
Problème	Solution
Aucun utilisateur trouvé --> Vérifier que la base de données contient bien des utilisateurs (SELECT * FROM messages;)
Page blanche sur API -->	Activer les erreurs PHP (error_reporting(E_ALL);) et vérifier les logs Apache (sudo tail -f /var/log/apache2/error.log)
L'application crash -->	Regarder les logs dans Android Studio (Logcat) et vérifier que Retrofit reçoit bien une réponse
Impossible de se connecter à MySQL -->	Vérifier le bind-address (0.0.0.0 dans /etc/mysql/my.cnf) et autoriser les connexions externes
------------------------------------------------------------------------------------------

🚀 Développé par Bastien Labeste, Robin Kwiatkowski et Quentin Chambelland
📅 Dernière mise à jour : Février 2025
🔗 Contact : bastienlabeste@gmail.com
