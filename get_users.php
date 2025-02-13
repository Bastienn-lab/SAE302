<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

$servername = "localhost";
$username = "root";
$password = "root";
$dbname = "Discord";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(["error" => "Échec de connexion : " . $conn->connect_error]));
}

$sql = "SELECT DISTINCT nom_utilisateur FROM messages";
$result = $conn->query($sql);

$users = [];
while ($row = $result->fetch_assoc()) {
    $users[] = $row["nom_utilisateur"];
}

echo json_encode($users);
$conn->close();
?>
