package com.example.saediscord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.BaseAdapter;
import java.util.List;

public class MessagesAdapter extends BaseAdapter {
    private Context context; // Contexte pr gérer l'affichage
    private List<Message> messages; // Liste des msgs récup

    // Constructeur, prend le contexte + la liste des msgs
    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    // Retourne le nb total de msgs
    @Override
    public int getCount() {
        return messages.size();
    }

    // Retourne un msg à une pos donnée
    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    // Retourne l'id de l'item (ici = pos)
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Crée ou recycle une vue pr afficher un msg
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        }

        // Récup les vues du layout item_message.xml
        TextView userTextView = convertView.findViewById(R.id.nomUtilisateur);
        TextView messageTextView = convertView.findViewById(R.id.message);
        TextView dateTextView = convertView.findViewById(R.id.date);
        TextView toxiciteTextView = convertView.findViewById(R.id.toxicite);

        // Récup le msg à la pos actuelle
        Message message = messages.get(position);

        // Associe les valeurs aux vues
        userTextView.setText(message.getNomUtilisateur());
        messageTextView.setText(message.getMessage());
        dateTextView.setText(message.getDate());
        toxiciteTextView.setText("Toxicité: " + message.getToxicityScore());

        return convertView;
    }
}
