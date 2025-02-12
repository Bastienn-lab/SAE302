import java.io.*;
import java.sql.*;
import java.util.*;

public class DiscordDataProcessor {

    // Infos de co à la BDD MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Discord"; 
    private static final String DB_USER = "root"; 
    private static final String DB_PASS = "root"; 

    public static void main(String[] args) {
        // Chemin du fichier contenant les msgs extraits de Discord
        String inputFilePath = "/home/rt/Téléchargements/SAE-JAVADISCORD/selected_messages.txt";

        // Vérifie si le fichier existe avant de lancer le process
        File file = new File(inputFilePath);
        if (!file.exists()) {
            System.err.println("Le fichier " + inputFilePath + " est introuvable.");
            return; // Stoppe le prog si fichier absent
        }

        // Connexion à la BDD
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Charge le driver MySQL (obligatoire pour certaines versions de Java)
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connexion à la base de données réussie.");

            // Lecture du fichier ligne par ligne
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
                String line;
                Map<String, String> messageData = new LinkedHashMap<>(); // Stocke temporairement les infos du msg

                while ((line = reader.readLine()) != null) {
                    // Détecte un nv msg grâce au timestamp qui commence par [
                    if (line.startsWith("[")) {
                        // Si un msg était déjà en mémoire, on l'insère en BDD avant de traiter le suivant
                        if (!messageData.isEmpty()) {
                            insertMessageDataIntoDB(conn, messageData);
                            messageData.clear(); // Reset pour le msg suivant
                        }

                        // Extraction du timestamp, de l'auteur et du msg
                        int closingBracketIndex = line.indexOf("]");
                        String timestamp = line.substring(1, closingBracketIndex);
                        String rest = line.substring(closingBracketIndex + 2);
                        int colonIndex = rest.indexOf(": ");
                        String author = rest.substring(0, colonIndex);
                        String message = rest.substring(colonIndex + 2);

                        // Ajout des données récupérées dans le Map
                        messageData.put("Timestamp", timestamp);
                        messageData.put("Author", author);
                        messageData.put("Message", message);
                        messageData.put("Message Length", String.valueOf(message.length())); // Stocke la longueur du msg

                    } else if (line.startsWith("Replies to: ")) {
                        // Cas où le msg est une réponse à un autre
                        String[] parts = line.substring(11).split(": ", 2);
                        if (parts.length == 2) {
                            messageData.put("Receveur", parts[0]); // Pers qui reçoit la réponse
                            messageData.put("Replies To", parts[1]); // Contenu du msg cité
                        }
                    }
                }

                // Insère le dernier msg lu (si présent) en BDD
                if (!messageData.isEmpty()) {
                    insertMessageDataIntoDB(conn, messageData);
                }

                System.out.println("Les données ont été traitées et insérées dans la base de données.");

            } catch (IOException e) {
                System.err.println("Erreur lors du traitement du fichier : " + e.getMessage());
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Pilote JDBC introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur de connexion ou d'insertion en BDD : " + e.getMessage());
        }
    }

    private static void insertMessageDataIntoDB(Connection conn, Map<String, String> messageData) {
        // Requête SQL d'insertion des msgs
        String sql = "INSERT INTO messages (nom_utilisateur, message, date, receveur, message_length, replies_to) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Récup des données extraites
            String author = messageData.getOrDefault("Author", ""); // Auteur du msg
            String msg = messageData.getOrDefault("Message", ""); // Contenu du msg
            String timestamp = messageData.getOrDefault("Timestamp", ""); // Date et heure du msg
            String receveur = messageData.getOrDefault("Receveur", null); // Pers mentionnée si réponse
            int messageLength = msg.length(); // Longueur du msg
            String repliesTo = messageData.getOrDefault("Replies To", null); // Contenu du msg cité si réponse

            // Associe les valeurs récupérées aux ?
            pstmt.setString(1, author);
            pstmt.setString(2, msg);
            pstmt.setString(3, timestamp);
            pstmt.setString(4, receveur);
            pstmt.setInt(5, messageLength);
            pstmt.setString(6, repliesTo);

            // Exécute la requête pour insérer le msg
            pstmt.executeUpdate();
            System.out.println("Insertion réussie pour : " + author);

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du msg en BDD : " + e.getMessage());
        }
    }
}
