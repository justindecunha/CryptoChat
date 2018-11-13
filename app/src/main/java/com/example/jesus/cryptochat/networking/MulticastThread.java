package com.example.jesus.cryptochat.networking;


import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  This class acts as the base class for the messageSender/Receiver
 *  Loads a multicast socket and joins the multicast IP group specified in the preferences
 */
class MulticastThread extends Thread {

    // The data to send
    final InetAddress ipAddress;
    final int port;
    final AtomicBoolean running = new AtomicBoolean(true);

    MulticastSocket socket;

    MulticastThread(final String threadName, final InetAddress ipAddress, final int port) {
        super(threadName);
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void run() {
        try {
            socket = new MulticastSocket(port);
            socket.joinGroup(ipAddress);
        } catch (IOException e) {
            Log.d("cryptochatdbg", ipAddress.toString());
            e.printStackTrace();
        }

    }

    //Used to halt the message Receiver
    public void stopRunning() {
        running.set(false);
        socket.close();
    }

}
