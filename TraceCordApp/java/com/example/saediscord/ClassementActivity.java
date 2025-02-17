package com.example.saediscord;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassementActivity extends AppCompatActivity {
    private ListView classementListView;
    private ArrayAdapter<String> adapter;
    private List<String> classementList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classement);

        classementListView = findViewById(R.id.classementListView);

        //  Init Retrofit
        apiService = ApiClient.getClient().create(ApiService.class);

        //  Récup users et afficher classement
        fetchUsers();

        //  Setup adaptateur pr afficher la liste
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, classementList);
        classementListView.setAdapter(adapter);

        //  Quand on clique sur un user -> ouvre sa page de stats
        classementListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEntry = (String) parent.getItemAtPosition(position);

            //  Choppe le pseudo (avant le "- XX%")
            String username = selectedEntry.split(" - ")[0].trim();

            //  Lance MessagesActivity avec le bon user
            Intent intent = new Intent(ClassementActivity.this, MessagesActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        //  Btn retour (ferme l'activité)
        Button btnBack = findViewById(R.id.btnRetour);
        btnBack.setOnClickListener(v -> finish());
    }

    /**
     *  Récup ts les users via API et lance `afficherClassement()`
     */
    private void fetchUsers() {
        apiService.getUsers().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> users = response.body();
                    Log.d("DEBUG", "Utilisateurs récupérés : " + users);
                    afficherClassement(users);
                } else {
                    Log.e("ERROR", "Réponse vide ou erreur");
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("ERROR", "Erreur de connexion : " + t.getMessage());
            }
        });
    }

    /**
     *  Calcule le % de toxicité pr chaque user et met à jour la liste
     */
    private void afficherClassement(List<String> users) {
        classementList.clear();

        for (String user : users) {
            apiService.getMessages(user).enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Message> messages = response.body();
                        int totalMessages = messages.size();
                        int totalToxicity = 0;

                        //  Calcule la somme des toxicité
                        for (Message msg : messages) {
                            totalToxicity += msg.getToxicityScore();
                        }

                        //  Moyenne des toxicités (même formule que MessagesActivity)
                        float avgToxicityScore = (totalMessages > 0) ? ((float) totalToxicity / totalMessages) * 100 : 0;

                        String classementEntry = String.format("%s - %.2f%% ", user, avgToxicityScore);

                        //  Ajoute user ds la liste + tri
                        runOnUiThread(() -> {
                            classementList.add(classementEntry);

                            //  Trie du + toxique au - toxique
                            Collections.sort(classementList, (a, b) -> {
                                float toxA = Float.parseFloat(a.split(" - ")[1].replace("% ", ""));
                                float toxB = Float.parseFloat(b.split(" - ")[1].replace("% ", ""));
                                return Float.compare(toxB, toxA); // Tri décroissant
                            });

                            //  MAJ affichage
                            adapter.notifyDataSetChanged();
                            afficherMedaille();
                        });
                    }
                }

                @Override
                public void onFailure(Call<List<Message>> call, Throwable t) {
                    Log.e("ERROR", "Erreur de récupération des messages de " + user + " : " + t.getMessage());
                }
            });
        }
    }

    /**
     *  Affiche les icônes pr les 3 premiers
     */
    private void afficherMedaille() {
        ImageView iconClassement = findViewById(R.id.iconClassement1);
        ImageView iconClassement2 = findViewById(R.id.iconClassement2);
        ImageView iconClassement3 = findViewById(R.id.iconClassement3);

        if (classementList.size() >= 1) iconClassement.setVisibility(View.VISIBLE);
        if (classementList.size() >= 2) iconClassement2.setVisibility(View.VISIBLE);
        if (classementList.size() >= 3) iconClassement3.setVisibility(View.VISIBLE);
    }
}
