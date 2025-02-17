package com.example.saediscord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseHelper {
    //  Connexion à la BDD MySQL
    private static final String DB_URL = "jdbc:mysql://10.3.122.108:3306/Discord?useSSL=false";
    private static final String DB_USER = "root"; // User BDD
    private static final String DB_PASS = "root"; //  MDP BDD

    //  Exécuter les req SQL en arrière-plan pr pas bloquer l'UI
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     *  Interface pr gérer les retours async (succès / erreur)
     */
    public interface Callback<T> {
        void onSuccess(T result); //  Callback OK
        void onFailure(Exception e); //  Callback erreur
    }

    /**
     *  Récup ts les users de la BDD (sans doublons)
     */
    public static void getUsers(Callback<List<String>> callback) {
        executor.execute(() -> { //  Exécute en tâche de fond
            List<String> users = new ArrayList<>();
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT DISTINCT nom_utilisateur FROM messages")) {

                while (rs.next()) {
                    String user = rs.getString("nom_utilisateur");
                    users.add(user);
                }
                callback.onSuccess(users); //  Retourne la liste
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure(e); //  Erreur SQL
            }
        });
    }

    /**
     * Récup ts les msgs d’un user donné
     */
    public static void getMessages(String user, Callback<List<Message>> callback) {
        executor.execute(() -> { //  Exécute en tâche de fond
            List<Message> messages = new ArrayList<>();
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(
                         "SELECT nom_utilisateur, message, receveur, date, message_length, toxicity_score " +
                                 "FROM messages WHERE nom_utilisateur = '" + user + "'")) {

                while (rs.next()) {
                    messages.add(new Message(
                            rs.getString("nom_utilisateur"),
                            rs.getString("message"),
                            rs.getString("date"),
                            rs.getString("receveur"),
                            rs.getInt("message_length"),
                            rs.getInt("toxicity_score")
                    ));
                }
                callback.onSuccess(messages); //  Retourne les msgs
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure(e); //  Erreur SQL
            }
        });
    }

    /**
     *  Crée la connexion à la BDD MySQL
     */
    private static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //  Charge le driver MySQL
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); // Connexion
        } catch (Exception e) {
            e.printStackTrace();
            return null; //  Erreur de connexion
        }
    }
}
