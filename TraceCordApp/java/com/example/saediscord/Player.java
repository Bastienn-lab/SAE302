package com.example.saediscord;

public class Player {
    private String username; // Nom du joueur
    private double toxicity; // Score de tox du joueur

    // Constructeur, prend le pseudo + son score de tox
    public Player(String username, double toxicity) {
        this.username = username;
        this.toxicity = toxicity;
    }

    // Retourne le pseudo du joueur
    public String getUsername() {
        return username;
    }

    // Retourne son score de tox
    public double getToxicity() {
        return toxicity;
    }
}
