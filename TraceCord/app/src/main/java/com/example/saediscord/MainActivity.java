package com.example.saediscord;

import android.content.Intent; // 🔥 Ajout de l'import pour Intent
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView; // 🔥 Ajout de l'import pour AdapterView
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        fetchUsers();

        // ✅ Ajouter un écouteur de clics sur la liste
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedUser = (String) parent.getItemAtPosition(position);
                Log.d("DEBUG", "Utilisateur cliqué : " + selectedUser); // 🔥 Vérification dans les logs

                // 🔥 Ouvrir MessagesActivity avec l'utilisateur sélectionné
                Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                intent.putExtra("username", selectedUser);
                startActivity(intent);
            }
        });
    }

    private void fetchUsers() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<String>> call = apiService.getUsers();

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.clear();
                    adapter.addAll(response.body());
                    adapter.notifyDataSetChanged();
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
