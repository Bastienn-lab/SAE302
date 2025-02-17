package com.example.saediscord;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    /**
     *  Chope ts les users depuis l'API
     * Retourne une liste de noms en str
     */
    @GET("get_users.php")
    Call<List<String>> getUsers();

    /**
     *  Chope ts les msgs d’un user
     * Prend `username` en param, envoyé en GET ds l’URL
     */
    @GET("get_messages.php")
    Call<List<Message>> getMessages(@Query("username") String username);
}
