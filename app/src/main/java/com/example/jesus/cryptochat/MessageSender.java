package com.example.jesus.cryptochat;

import android.os.Handler;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * MessageSender sends an encrypted message over the network
 */

public class MessageSender extends MulticastThread {

    //The message to send
    private String message;

    public MessageSender(MainActivity mainActivity, String displayName, String password, String message) {
        super("MessageSender", mainActivity, new Handler());
        try {
            if(!displayName.equals("")) {
                message = displayName + Constants.PARSE_TOKEN + message;
            }
            this.message = SimpleCrypt.encrypt(message, password); //encrypts the message to send
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        try {
            final byte[] bytesToSend = message.getBytes();
            socket.send(new DatagramPacket(bytesToSend, bytesToSend.length, InetAddress.getByName(Constants.NETWORK_IP), Constants.PORT)); //sends the datagram packet to the multicast group
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
