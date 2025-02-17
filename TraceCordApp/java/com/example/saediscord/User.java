package com.example.saediscord;

public class User {
    private String nom_utilisateur; // Pseudo du user

    // Constructeur, prend le pseudo en param
    public User(String nom_utilisateur) {
        this.nom_utilisateur = nom_utilisateur;
    }

    // Retourne le pseudo du user
    public String getNomUtilisateur() {
        return nom_utilisateur;
    }
}
