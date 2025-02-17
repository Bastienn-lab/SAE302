package com.example.saediscord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends ArrayAdapter<String> {
    private List<String> originalUsers; // Liste complète des users
    private List<String> filteredUsers; // Liste filtrée selon la recherche

    // Constructeur, prend le contexte + la liste des users
    public UsersAdapter(Context context, List<String> users) {
        super(context, 0, users);
        this.originalUsers = new ArrayList<>(users); // Copie pr garder ts les users
        this.filteredUsers = users; // Liste utilisée pr affichage
    }

    // Crée ou recycle une vue pr afficher un user
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        TextView textUser = convertView.findViewById(R.id.textUser);
        textUser.setText(getItem(position)); // Associe le pseudo à la vue

        return convertView;
    }

    // Retourne le nb d'users affichés
    @Override
    public int getCount() {
        return filteredUsers.size();
    }

    // Retourne un user à une pos donnée
    @Override
    public String getItem(int position) {
        return filteredUsers.get(position);
    }

    // Gère le filtrage des users selon la recherche
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<String> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    // Si pas de recherche, affiche ts les users
                    filteredList.addAll(originalUsers);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (String user : originalUsers) {
                        if (user.toLowerCase().contains(filterPattern)) {
                            filteredList.add(user); // Ajoute les users qui matchent
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredUsers.clear();
                filteredUsers.addAll((List<String>) results.values);
                notifyDataSetChanged(); // MAJ de la liste affichée
            }
        };
    }
}
