package com.example.jesus.cryptochat.messaging;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jesus.cryptochat.R;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * This class is used to populate the listView from an arrayList of message objects.
 * It takes the display name and message from each message object and inflates it into a chat_bubble
 * which is then added to the listView UI component.
 * When ever the array is changed, the adapter is notified, which will then update the UI for the user
 * in order to visualize the change.
 */
public class MessageAdapter extends ArrayAdapter<Message> {
    private List<Message> messageList;
    private final LayoutInflater inflater;

    public MessageAdapter(final Context context, final int resource, final List<Message> messageList) {
        super(context, resource, messageList);
        this.messageList = messageList;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.chat_bubble_view, parent, false);
        }
        final TextView userField;
        final TextView messageField;

        userField = convertView.findViewById(R.id.txt_username);
        messageField = convertView.findViewById(R.id.txt_message);

        Message message = messageList.get(position);

        userField.setText(message.getDisplayName());
        messageField.setText(message.getMessage());
        return convertView;
    }
}

