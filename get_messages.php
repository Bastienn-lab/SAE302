<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);


$host = "localhost";  
$dbname = "Discord"; 
$user = "root";      
$pass = "root";    


if (!isset($_GET['username'])) {
    die(json_encode(["error" => "Paramètre 'username' manquant"]));
}


$username = $_GET['username'];

try {
  
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die(json_encode(["error" => "Erreur de connexion MySQL : " . $e->getMessage()]));
}


$sql = "SELECT nom_utilisateur, message, date, receveur FROM messages WHERE nom_utilisateur = :username";
$stmt = $pdo->prepare($sql);
$stmt->bindParam(":username", $username);
$stmt->execute();
$messages = $stmt->fetchAll(PDO::FETCH_ASSOC);


if ($messages) {
    echo json_encode($messages);
} else {
    echo json_encode(["message" => "Aucun message trouvé pour cet utilisateur"]);
}
?>

