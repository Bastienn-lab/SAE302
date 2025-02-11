package com.example.saediscord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://10.3.122.108:3306/Discord?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";


    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("✅ Connexion MySQL réussie !");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erreur de connexion à MySQL : " + e.getMessage());
        }
        return connection;
    }


    public static List<String> getUsers() {
        List<String> users = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT nom_utilisateur FROM messages")) {

            while (rs.next()) {
                String user = rs.getString("nom_utilisateur");
                System.out.println("👤 Utilisateur trouvé : " + user);
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return users;
    }




    public static List<Message> getMessages(String user) {
        List<Message> messages = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT message, receveur, message_length FROM messages WHERE nom_utilisateur = '" + user + "'")) {

            while (rs.next()) {
                messages.add(new Message(
                        rs.getString("message"),
                        rs.getString("receveur"),
                        rs.getString("date"), // Ajout de la date
                        rs.getDouble("toxicite") // Correction du type de toxicité
                ));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }
}
