package com.example.saediscord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import java.util.List;

public class ClassementAdapter extends ArrayAdapter<Player> {
    private Context context; //  Contexte pr accéder aux ressources
    private List<Player> classementList; //  Liste des joueurs classés

    /**
     *  Constructeur pr init l'adapter avec la liste des joueurs
     */
    public ClassementAdapter(Context context, List<Player> classementList) {
        super(context, 0, classementList); // 0 = pas de layout prédéfini, on le fait à la main
        this.context = context;
        this.classementList = classementList;
    }

}
