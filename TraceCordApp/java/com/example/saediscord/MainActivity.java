package com.example.saediscord;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private ListView listView; //  Liste pr afficher les users
    private SearchView searchView; //  Barre de recherche
    private ArrayAdapter<String> adapter; //  Adaptateur pr lier la liste aux données
    private List<String> userList = new ArrayList<>(); //  Stocke les users récupérés


    private ConstraintLayout backgroundLayout; // Déclaration de backgroundLayout
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Init UI
        listView = findViewById(R.id.listView);
        searchView = findViewById(R.id.searchView);
        TextView titleText = findViewById(R.id.titleText);

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
        ConstraintLayout constraintLayout = findViewById(R.id.rootLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), Color.BLACK, Color.WHITE);
        colorAnimation.addUpdateListener(animator -> {
            int color = (int) animator.getAnimatedValue();
            titleText.setTextColor(invertColor(color));

        });
        colorAnimation.setDuration(3000); // Durée de l'animation
        colorAnimation.start();


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
    public int invertColor(int color) {
        int r = 255 - Color.red(color);
        int g = 255 - Color.green(color);
        int b = 255 - Color.blue(color);
        return Color.rgb(r, g, b);
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
