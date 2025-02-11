package com.example.saediscord;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast; // 🔥 Ajout de l'import pour Toast
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesActivity extends AppCompatActivity {
    private ListView listView;
    private MessagesAdapter adapter;
    private String selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        listView = findViewById(R.id.listViewMessages);
        TextView userTitle = findViewById(R.id.textUserTitle);

        // 🔥 Récupérer le nom de l'utilisateur passé par Intent
        selectedUser = getIntent().getStringExtra("username");

        if (selectedUser != null) {
            Toast.makeText(this, "Utilisateur sélectionné : " + selectedUser, Toast.LENGTH_SHORT).show();
            userTitle.setText("Messages de " + selectedUser);
            fetchMessages(selectedUser); // ✅ Correction ici
        } else {
            Toast.makeText(this, "Aucun utilisateur reçu", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchMessages(String username) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Message>> call = apiService.getMessagesByUser(username);

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new MessagesAdapter(MessagesActivity.this, response.body());
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
