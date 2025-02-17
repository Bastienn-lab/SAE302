package com.example.saediscord;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //  URL de base pour l'API, doit être l'IP du serveur backend
    private static final String BASE_URL = "http://10.3.122.108/";

    //  Objet Retrofit qui va gérer les requêtes HTTP
    private static Retrofit retrofit = null;

    /**
     *  Récupère une instance unique de Retrofit (singleton)
     * Permet d'éviter de recréer un objet Retrofit à chaque requête
     */
    public static Retrofit getClient() {
        if (retrofit == null) { // Vérif si l'instance existe déjà
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // Définit l'URL de base pour toutes les requêtes
                    .addConverterFactory(GsonConverterFactory.create()) // Convertit JSON -> Objet Java
                    .build();
        }
        return retrofit; // Retourne l'instance Retrofit (créée ou existante)
    }
}
