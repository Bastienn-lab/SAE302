package com.example.saediscord;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.Color;
import com.google.gson.Gson;

public class MessagesActivity extends AppCompatActivity {
    private ListView messagesListView; // Liste pr afficher les msgs
    private MessagesAdapter adapter; // Adaptateur pr remplir la liste
    private String username; // User sélectionné
    private List<Message> listeMessages; // Liste des msgs récup

    private TextView userNameText, statsMessagesSent, statsToxicityScore, statsAvgMessageLength, statsMostRepliedUser;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        // Init UI
        messagesListView = findViewById(R.id.messagesListView);
        userNameText = findViewById(R.id.userNameText);
        statsMessagesSent = findViewById(R.id.statsMessagesSent);
        statsToxicityScore = findViewById(R.id.statsToxicityScore);
        statsAvgMessageLength = findViewById(R.id.statsAvgMessageLength);
        statsMostRepliedUser = findViewById(R.id.statsMostRepliedUser);
        btnBack = findViewById(R.id.btnBack);

        username = getIntent().getStringExtra("username");
        userNameText.setText(username); // Affiche le nom du user

        fetchMessages(); // Récup ses msgs

        // Btn voir classement
        Button btnClassement = findViewById(R.id.btnClassement);
        btnClassement.setOnClickListener(v -> {
            Intent intent = new Intent(MessagesActivity.this, ClassementActivity.class);
            startActivity(intent);
        });

        // Btn retour
        btnBack.setOnClickListener(v -> finish());
    }

    // Envoie les stats de toxicité au classement
    private void envoyerClassement() {
        if (listeMessages == null || listeMessages.isEmpty()) {
            Log.e("ClassementActivity", "Impossible d'envoyer : Aucun message");
            return;
        }

        // Calcule les scores de tox
        HashMap<String, Integer> toxiciteMap = calculerToxiciteMoyenne(listeMessages);

        if (toxiciteMap.isEmpty()) {
            Log.e("ClassementActivity", "Aucun score de toxicité calculé");
            return;
        }

        // Convertit en JSON
        Gson gson = new Gson();
        String toxiciteJson = gson.toJson(toxiciteMap);

        Log.d("ClassementActivity", "Envoi des scores de toxicité : " + toxiciteJson);

        // Lance ClassementActivity avec les scores
        Intent intent = new Intent(MessagesActivity.this, ClassementActivity.class);
        intent.putExtra("toxiciteData", toxiciteJson);
        startActivity(intent);
    }

    // Récup ts les msgs du user via API
    private void fetchMessages() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Message>> call = apiService.getMessages(username);

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listeMessages = response.body(); // Stocke les msgs récup
                    adapter = new MessagesAdapter(MessagesActivity.this, listeMessages);
                    messagesListView.setAdapter(adapter);

                    // Calcule les stats
                    calculateStatistics(listeMessages);

                } else {
                    Log.e("API_ERROR", "Réponse vide ou erreur");
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e("API_ERROR", "Erreur de connexion : " + t.getMessage());
            }
        });
    }

    // Calcule les stats sur les msgs du user
    private void calculateStatistics(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            Log.e("STATS_ERROR", "Aucun message trouvé");
            statsMessagesSent.setText("Messages envoyés : 0");
            statsToxicityScore.setText("Score de toxicité : 0.00%");
            statsAvgMessageLength.setText("Taille moyenne : 0.0 caractères");
            statsMostRepliedUser.setText("Personne la plus mentionnée : Aucun");
            return;
        }

        int totalMessages = messages.size();
        int totalToxicity = 0;
        int totalMessageLength = 0;
        HashMap<String, Integer> repliedUserCount = new HashMap<>();

        for (Message msg : messages) {
            int toxicity = msg.getToxicityScore();
            int length = msg.getMessageLength();
            String receiver = msg.getReceveur();

            Log.d("DEBUG_TOXICITY", "Toxicité: " + toxicity);
            Log.d("DEBUG_LENGTH", "Longueur msg: " + length);
            Log.d("DEBUG_RECEIVER", "Receveur: " + receiver);

            totalToxicity += toxicity;
            totalMessageLength += length;

            if (receiver != null && !receiver.isEmpty()) {
                repliedUserCount.put(receiver, repliedUserCount.getOrDefault(receiver, 0) + 1);
            }
        }

        double avgMessageLength = (totalMessages > 0) ? (double) totalMessageLength / totalMessages : 0;
        double avgToxicityScore = (totalMessages > 0) ? ((double) totalToxicity / totalMessages) * 100 : 0;

        String mostRepliedUser = "Aucune";
        int maxReplies = 0;
        for (String user : repliedUserCount.keySet()) {
            if (repliedUserCount.get(user) > maxReplies) {
                maxReplies = repliedUserCount.get(user);
                mostRepliedUser = user;
            }
        }

        TextView toxicityScoreText = findViewById(R.id.statsToxicityScore);
        if (avgToxicityScore > 50) {
            toxicityScoreText.setTextColor(Color.RED); // Si > 50%, rouge
        } else {
            toxicityScoreText.setTextColor(Color.GREEN); // Sinon, vert
        }

        statsMessagesSent.setText("Messages envoyés : " + totalMessages);
        statsToxicityScore.setText("Score de toxicité : " + String.format("%.2f", avgToxicityScore) + "%");
        statsAvgMessageLength.setText("Taille moyenne : " + String.format("%.1f", avgMessageLength) + " caractères");
        statsMostRepliedUser.setText("Personne la plus mentionnée : " + mostRepliedUser);
    }

    // Calcule la moyenne de toxicité par user
    private HashMap<String, Integer> calculerToxiciteMoyenne(List<Message> messages) {
        HashMap<String, Integer> toxiciteMap = new HashMap<>();
        HashMap<String, Integer> nombreMessages = new HashMap<>();

        for (Message message : messages) {
            String user = message.getNomUtilisateur();
            int toxicite = message.getToxicityScore();

            // Ajoute la toxicité au total du user
            toxiciteMap.put(user, toxiciteMap.getOrDefault(user, 0) + toxicite);
            nombreMessages.put(user, nombreMessages.getOrDefault(user, 0) + 1);
        }

        // Calcule la toxicité moyenne en pourcentage
        for (String user : toxiciteMap.keySet()) {
            int totalToxicite = toxiciteMap.get(user);
            int totalMessages = nombreMessages.get(user);
            int toxiciteMoyenne = (totalMessages > 0) ? (totalToxicite / totalMessages) : 0;
            toxiciteMap.put(user, toxiciteMoyenne);
        }

        return toxiciteMap;
    }
}
