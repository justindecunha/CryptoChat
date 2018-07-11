package com.example.jesus.cryptochat;

import android.os.Handler;
import android.widget.ListView;

import com.example.jesus.cryptochat.models.MessageModel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

/**
 * Listens for incoming messages on the multicast group and processes them through the handler
 */
public class MessageReceiver extends MulticastThread {

    private ListView lvMessages;
    private DatagramPacket packet;
    private String displayName, message;
    private String password = "";
    private final List<MessageModel> messageModelList = new ArrayList<>();
    private final MessageAdapter adapter;

    public MessageReceiver(MainActivity mainActivity, final ListView lvMessages) {
        super("MessageReceiver", mainActivity, new Handler());
        this.lvMessages = lvMessages;
        adapter = new MessageAdapter(mainActivity.getApplicationContext(), R.layout.row, messageModelList);
    }

    @Override
    public void run() {
        super.run();

        //creates a new packet to fill with received data
        this.packet = new DatagramPacket(new byte[Constants.PACKET_SIZE], Constants.PACKET_SIZE);

        while(running.get()) {
            packet.setData(new byte[Constants.PACKET_SIZE * 2]); // clears any previous data

            try {
                if(socket != null) {
                    socket.receive(packet); // Receive packet from network
                } else {
                    break;
                }
            } catch (IOException ignored) {
                continue; //fairly safe to ignore this error
            }

            try {
                message = new String(packet.getData(), Constants.MESSAGE_CHARSET).trim();
                message = SimpleCrypt.decrypt(message, password);

                // Gets the displayName from the message if sender has specified one
                if(message.contains(Constants.PARSE_TOKEN)) {
                    String[] messageSplit = message.split(Constants.PARSE_TOKEN);
                    displayName = messageSplit[0];
                    message = messageSplit[1];
                } else {
                    displayName = packet.getAddress().getHostAddress();
                }
            } catch (UnsupportedEncodingException e) {
                continue;
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    messageModelList.add(new MessageModel(displayName, message));
                    lvMessages.setAdapter(adapter);
                }
            });
        }
        if(socket != null)
            socket.close();
    }

    public void updatePassword(final String password) {
        this.password = password;
    }
}
