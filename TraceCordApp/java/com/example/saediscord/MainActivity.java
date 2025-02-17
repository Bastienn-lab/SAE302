package com.example.saediscord;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private ListView listView; //  Liste pr afficher les users
    private SearchView searchView; //  Barre de recherche
    private ArrayAdapter<String> adapter; //  Adaptateur pr lier la liste aux données
    private List<String> userList = new ArrayList<>(); //  Stocke les users récupérés

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Init UI
        listView = findViewById(R.id.listView);
        searchView = findViewById(R.id.searchView);

        //  Setup adaptateur pr afficher les users ds la liste
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(adapter);

        Log.d("DEBUG", "Adapter initialisé avec " + userList.size() + " utilisateurs");

        fetchUsers(); //  Récupère la liste des users via API

        //  Ajoute un filtre de recherche sur la liste
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText); //  Filtre dynamiquement la liste
                return true;
            }
        });

        //  Clic sur un user -> ouvre sa page de stats
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUser = adapter.getItem(position);
            if (selectedUser != null) {
                Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                intent.putExtra("username", selectedUser); //  Envoie le pseudo au nouvel écran
                startActivity(intent);
            }
        });

        //  Btn pr voir le classement
        Button btnClassement = findViewById(R.id.btnClassement);
        btnClassement.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ClassementActivity.class);
            startActivity(intent);
        });
    }

    /**
     *  Récupère ts les users via l'API et met à jour la liste
     */
    private void fetchUsers() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<String>> call = apiService.getUsers();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.clear();
                    userList.addAll(response.body()); //  Ajoute les users récupérés
                    adapter.notifyDataSetChanged(); //  MAJ liste affichée
                    Log.d("DEBUG", "Utilisateurs récupérés : " + userList);
                } else {
                    Log.e("API_ERROR", "Réponse vide ou erreur");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("API_ERROR", "Erreur de connexion : " + t.getMessage());
            }
        });
    }
}
