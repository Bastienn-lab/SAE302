<?php
header("Access-Control-Allow-Origin: *"); // Permet accès API depuis n'importe où
header("Content-Type: application/json; charset=UTF-8"); // Réponse en JSON

// Connexion à la base
$servername = "localhost";
$username = "root";
$password = "root";
$dbname = "Discord";

// Création de la connexion
$conn = new mysqli($servername, $username, $password, $dbname);

// Vérif si la connexion a échoué
if ($conn->connect_error) {
    die(json_encode(["error" => "Échec de connexion : " . $conn->connect_error]));
}

// Récup des users uniques
$sql = "SELECT DISTINCT nom_utilisateur FROM messages";
$result = $conn->query($sql);

$users = [];
while ($row = $result->fetch_assoc()) {
    $users[] = $row["nom_utilisateur"];
}

// Retourne les users en JSON
echo json_encode($users);
$conn->close();
?>
