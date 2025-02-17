<?php
// Activer l'affichage des erreurs pr debug
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Paramètres de connexion MySQL
$host = "localhost";  // Vérifie que c'est bien ton hôte MySQL
$dbname = "Discord";  // Nom de ta base
$user = "root";       // Utilisateur MySQL
$pass = "root";       // Mot de passe MySQL

// Vérif si le param "username" est bien envoyé
if (!isset($_GET['username'])) {
    die(json_encode(["error" => "Paramètre 'username' manquant"]));
}

// Récup du username depuis l'URL
$username = $_GET['username'];

try {
    // Connexion à MySQL avec PDO
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die(json_encode(["error" => "Erreur connexion MySQL : " . $e->getMessage()]));
}

// Requête pr récup les msgs du user avec le score de toxicité
$sql = "SELECT nom_utilisateur, message, message_length, date, receveur, toxicity_score 
        FROM messages 
        WHERE nom_utilisateur = :username";
        
$stmt = $pdo->prepare($sql);
$stmt->bindParam(":username", $username);
$stmt->execute();
$messages = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Vérif si des msgs ont été trouvés
if ($messages) {
    echo json_encode($messages);
} else {
    echo json_encode(["message" => "Aucun message trouvé pr cet utilisateur"]);
}
?>
