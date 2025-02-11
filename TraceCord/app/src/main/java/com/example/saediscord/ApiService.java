package com.example.saediscord;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;
import retrofit2.http.Query;


public interface ApiService {
    @GET("get_users.php")
    Call<List<String>> getUsers();
    @GET("get_messages.php")
    Call<List<Message>> getMessagesByUser(@Query("username") String username);

}
