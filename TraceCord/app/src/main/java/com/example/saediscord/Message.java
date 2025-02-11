package com.example.saediscord;

public class Message {
    private String message;
    private String receveur;
    private String date;
    private double toxicite;

    // 🔥 Ajouter ce constructeur
    public Message(String message, String receveur, String date, double toxicite) {
        this.message = message;
        this.receveur = receveur;
        this.date = date;
        this.toxicite = toxicite;
    }

    public String getMessage() { return message; }
    public String getReceveur() { return receveur; }
    public String getDate() { return date; }
    public double getToxicite() { return toxicite; }
}
