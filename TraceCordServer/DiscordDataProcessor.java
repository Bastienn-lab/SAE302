import java.io.*;
import java.sql.*;
import java.util.*;

public class DiscordDataProcessor {

    // Connexion à la base MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Discord";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";

    public static void main(String[] args) {
        // Fichier généré par le bot Python
        String inputFilePath = "/home/rt/Téléchargements/SAE-JAVADISCORD/selected_messages.txt";

        // Vérif si le fichier existe
        File file = new File(inputFilePath);
        if (!file.exists()) {
            System.err.println("Le fichier " + inputFilePath + " est introuvable.");
            return;
        }

        // Connexion à la base
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connexion réussie.");

            // Lecture et traitement du fichier
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
                String line;
                Map<String, String> messageData = new LinkedHashMap<>();

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("[")) { // Début d'un msg
                        if (!messageData.isEmpty()) {
                            insertMessageDataIntoDB(conn, messageData);
                            messageData.clear();
                        }

                        // Extraction timestamp, auteur et msg
                        int closingBracketIndex = line.indexOf("]");
                        String timestamp = line.substring(1, closingBracketIndex);
                        String rest = line.substring(closingBracketIndex + 2);
                        int colonIndex = rest.indexOf(": ");
                        String author = rest.substring(0, colonIndex);
                        String message = rest.substring(colonIndex + 2);

                        messageData.put("Timestamp", timestamp);
                        messageData.put("Author", author);
                        messageData.put("Message", message);
                        messageData.put("Message Length", String.valueOf(message.length())); 

                    } else if (line.startsWith("Toxicity Score: ")) {
                        // Extraction du score de toxicité
                        String score = line.substring(15).trim();
                        messageData.put("Toxicity Score", score);

                    } else if (line.startsWith("Replies to: ")) {
                        // Extraction du receveur et du msg auquel il répond
                        String[] parts = line.substring(11).split(": ", 2);
                        if (parts.length == 2) {
                            messageData.put("Receveur", parts[0]);
                            messageData.put("Replies To", parts[1]);
                        }
                    }
                }

                // Insérer dernier msg si présent
                if (!messageData.isEmpty()) {
                    insertMessageDataIntoDB(conn, messageData);
                }

                System.out.println("Données traitées et insérées en base.");

            } catch (IOException e) {
                System.err.println("Erreur fichier : " + e.getMessage());
            }

        } catch (ClassNotFoundException e) {
            System.err.println("Pilote JDBC introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Erreur connexion/insertion : " + e.getMessage());
        }
    }

    private static void insertMessageDataIntoDB(Connection conn, Map<String, String> messageData) {
        // Requête SQL avec le score de toxicité
        String sql = "INSERT INTO messages (nom_utilisateur, message, date, receveur, message_length, replies_to, toxicity_score) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String author = messageData.getOrDefault("Author", "");
            String msg = messageData.getOrDefault("Message", "");
            String timestamp = messageData.getOrDefault("Timestamp", "");
            String receveur = messageData.getOrDefault("Receveur", null);
            int messageLength = msg.length();
            String repliesTo = messageData.getOrDefault("Replies To", null);
            int toxicityScore = Integer.parseInt(messageData.getOrDefault("Toxicity Score", "0")); // Par défaut 0

            pstmt.setString(1, author);
            pstmt.setString(2, msg);
            pstmt.setString(3, timestamp);
            pstmt.setString(4, receveur);
            pstmt.setInt(5, messageLength);
            pstmt.setString(6, repliesTo);
            pstmt.setInt(7, toxicityScore); // Ajout du score de toxicité

            pstmt.executeUpdate();
            System.out.println("Insertion réussie pr : " + author);

        } catch (SQLException e) {
            System.err.println("Erreur insertion : " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erreur conversion toxicité : " + e.getMessage());
        }
    }
}
