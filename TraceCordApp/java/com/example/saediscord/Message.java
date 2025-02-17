package com.example.saediscord;

public class Message {
    private String nomUtilisateur; // User qui a envoyé le msg
    private String message; // Contenu du msg
    private String date; // Date d'envoi
    private String receveur; // Dest du msg
    private int message_length; // Nb de caractères du msg
    private int toxicity_score; // Score de tox du msg

    // Constructeur, init ts les champs
    public Message(String nom_utilisateur, String message, String date, String receveur, int message_length, int toxicity_score) {
        this.nomUtilisateur = nom_utilisateur;
        this.message = message;
        this.date = date;
        this.receveur = receveur;
        this.message_length = message_length;
        this.toxicity_score = toxicity_score;
    }

    // Retourne le user qui a envoyé le msg
    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    // Retourne le contenu du msg
    public String getMessage() {
        return message;
    }

    // Retourne la date d'envoi
    public String getDate() {
        return date;
    }

    // Retourne le dest du msg
    public String getReceveur() {
        return receveur;
    }

    // Retourne la taille du msg
    public int getMessageLength() {
        return message_length;
    }

    // Retourne le score de tox du msg
    public int getToxicityScore() {
        return toxicity_score;
    }

    // Modifie le score de tox
    public void setToxicityScore(int toxicityScore) {
        this.toxicity_score = toxicityScore;
    }
}
