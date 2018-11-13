package com.example.jesus.cryptochat.networking;

import android.content.SharedPreferences;

import com.example.jesus.cryptochat.activities.MainActivity;
import com.example.jesus.cryptochat.messaging.Message;
import com.example.jesus.cryptochat.messaging.MessageSerializer;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Listens for incoming messages from the multicast group.
 * When a message is received, it's processed by the handler, and added to the messageList.
 * The adapter is then notified of the change to the messageList, which prompts it to update the
 * messageDisplayer for the user to see.
 */

public class MessageReceiver extends MulticastThread {

    private MainActivity ctx;
    private SharedPreferences prefs;
    private Message receivedMessage;

    public MessageReceiver(SharedPreferences prefs, MainActivity ctx,
                           InetAddress ipAddress, int port) {
        super("MessageReceiver", ipAddress, port);
        this.ctx = ctx;
        this.prefs = prefs;
    }

    @Override
    public void run() {
        super.run();
        //creates a new packet to fill with received data
        DatagramPacket packet;
        while (running.get()) {

            byte[] buf = new byte[1024];
            packet = new DatagramPacket(buf, buf.length);
            try {
                if (socket != null) {
                    socket.receive(packet); // Receive packet from network

                    // Deserialize the byte array into a message object
                    receivedMessage = MessageSerializer.deserialize(packet.getData());

                    // If the message did not deserialize properly, discard it rather than crashing the app
                    if (receivedMessage == null) {
                        continue;
                    }

                    // Get the password required for decryption - a blank password is equivalent to no decryption
                    if (prefs.getBoolean("encryption_enabled", false)) {
                        String password = prefs.getString("encryption_password", "");
                        receivedMessage.decrypt(password);
                    }

                } else {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Process the decrypted message
            ctx.doMessage(receivedMessage);

        }

        // Closes the connection upon exiting the while loop
        if (socket != null) {
            socket.close();
        }
    }
}
