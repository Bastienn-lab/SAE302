import java.io.*;
import java.sql.*;
import java.util.*;

public class DiscordDataProcessor {

    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/Discord"; 
    private static final String DB_USER = "root"; 
    private static final String DB_PASS = "root"; 

    public static void main(String[] args) {
        
        String inputFilePath = "/home/rt/Téléchargements/SAE-JAVADISCORD/selected_messages.txt";

        
        File file = new File(inputFilePath);
        if (!file.exists()) {
            System.err.println("Le fichier " + inputFilePath + " est introuvable.");
            return;
        }

        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connexion à la base de données réussie.");

           
            try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
                String line;
                Map<String, String> messageData = new LinkedHashMap<>();

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("[")) { 
                        if (!messageData.isEmpty()) {
                            insertMessageDataIntoDB(conn, messageData);
                            messageData.clear();
                        }

                       
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

                    } else if (line.startsWith("Replies to: ")) {
                        
                        String[] parts = line.substring(11).split(": ", 2);
                        if (parts.length == 2) {
                            messageData.put("Receveur", parts[0]); 
                            messageData.put("Replies To", parts[1]);
                        }
                    }
                }

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
            System.err.println("Erreur de connexion ou d'insertion en base de données : " + e.getMessage());
        }
    }

    private static void insertMessageDataIntoDB(Connection conn, Map<String, String> messageData) {
        
        String sql = "INSERT INTO messages (nom_utilisateur, message, date, receveur, message_length, replies_to) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String author = messageData.getOrDefault("Author", "");
            String msg = messageData.getOrDefault("Message", "");
            String timestamp = messageData.getOrDefault("Timestamp", "");
            String receveur = messageData.getOrDefault("Receveur", null); 
            int messageLength = msg.length();
            String repliesTo = messageData.getOrDefault("Replies To", null); 

            pstmt.setString(1, author);
            pstmt.setString(2, msg);
            pstmt.setString(3, timestamp);
            pstmt.setString(4, receveur);
            pstmt.setInt(5, messageLength);
            pstmt.setString(6, repliesTo);

            pstmt.executeUpdate();
            System.out.println("Insertion réussie pour : " + author);

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du message en base : " + e.getMessage());
        }
    }
}
