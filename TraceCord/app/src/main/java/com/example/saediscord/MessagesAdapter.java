package com.example.saediscord;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.List;

public class MessagesAdapter extends ArrayAdapter<Message> {
    public MessagesAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_message, parent, false);
        }

        Message message = getItem(position);

        TextView textMessage = convertView.findViewById(R.id.textMessage);
        TextView textReceveur = convertView.findViewById(R.id.textReceveur);
        TextView textDate = convertView.findViewById(R.id.textDate);
        TextView textToxicite = convertView.findViewById(R.id.textToxicite);

        textMessage.setText(message.getMessage());
        textReceveur.setText("À : " + message.getReceveur());
        textDate.setText("Date : " + message.getDate());
        textToxicite.setText("Toxicité : " + message.getToxicite());

        return convertView;
    }
}
