package com.example.jesus.cryptochat.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * MessageSender sends a datagram packet over the network, containing a byte array of data
 */

public class MessageSender extends MulticastThread {

    private final byte[] data;

    public MessageSender(InetAddress ipAddress, int port, byte[] data) {
        super("MessageSender", ipAddress, port);
        this.data = data;
    }

    @Override
    public void run() {
        super.run();
        try {
            socket.send(new DatagramPacket(data, data.length, ipAddress, port)); //sends the datagram packet to the multicast group
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
