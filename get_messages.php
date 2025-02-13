<?php
// 🔥 Activer l'affichage des erreurs pour le debug
error_reporting(E_ALL);
ini_set('display_errors', 1);

// 🔥 Paramètres de connexion à la base de données
$host = "localhost";  // Vérifie que c'est bien ton hôte MySQL
$dbname = "Discord";  // Nom de ta base de données
$user = "root";       // Ton utilisateur MySQL
$pass = "root";       // Ton mot de passe MySQL

// 🔥 Vérifier que le paramètre "username" est bien reçu
if (!isset($_GET['username'])) {
    die(json_encode(["error" => "Paramètre 'username' manquant"]));
}

// Récupérer le nom d'utilisateur depuis l'URL
$username = $_GET['username'];

try {
    // 🔥 Connexion à la base de données avec PDO
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die(json_encode(["error" => "Erreur de connexion MySQL : " . $e->getMessage()]));
}

// 🔥 Requête SQL pour récupérer les messages de l'utilisateur donné
$sql = "SELECT nom_utilisateur, message, date, receveur FROM messages WHERE nom_utilisateur = :username";
$stmt = $pdo->prepare($sql);
$stmt->bindParam(":username", $username);
$stmt->execute();
$messages = $stmt->fetchAll(PDO::FETCH_ASSOC);

// 🔥 Vérifier si des messages ont été trouvés
if ($messages) {
    echo json_encode($messages);
} else {
    echo json_encode(["message" => "Aucun message trouvé pour cet utilisateur"]);
}
?>

