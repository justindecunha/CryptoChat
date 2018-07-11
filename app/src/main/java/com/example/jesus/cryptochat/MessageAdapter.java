package com.example.jesus.cryptochat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.jesus.cryptochat.models.MessageModel;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

 /**
  * Adapter class used to fill the list view
  * Reads the username and message from the row.xml file and writes it to text views which will then be placed
  * into each row of a list
  */
 class MessageAdapter extends ArrayAdapter {
    private final List<MessageModel> messageModelList;
    private final LayoutInflater inflater;
    public MessageAdapter(final Context context, final int resource, final List<MessageModel> objects) {
        super(context, resource, objects);
        messageModelList = objects;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.row, null);
        }
        final TextView user;
        final TextView message;

        user = (TextView)convertView.findViewById(R.id.txt_username);
        message = (TextView)convertView.findViewById(R.id.txt_message);

        user.setText("From: " + messageModelList.get(position).getDisplayName());
        message.setText("Message: " + messageModelList.get(position).getMessage());
        return convertView;
    }
}

