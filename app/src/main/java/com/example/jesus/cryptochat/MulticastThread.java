package com.example.jesus.cryptochat;

import android.os.Handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  This class acts as the base class for the messageSender/Receiver
 *  Loads a multicast socket and joins the multicast IP group specified in the Constants file
 */
class MulticastThread extends Thread {
    final AtomicBoolean running = new AtomicBoolean(true);
    private final MainActivity mainActivity;
    final Handler handler;

    MulticastSocket socket;

    MulticastThread(final String threadName, final MainActivity mainActivity, final Handler handler) {
        super(threadName);
        this.mainActivity =  mainActivity;
        this.handler = handler;
    }

    public void run() {
        try {
            socket = new MulticastSocket(Constants.PORT);
            socket.joinGroup(InetAddress.getByName(Constants.NETWORK_IP));
            socket.setTimeToLive(Constants.TTL);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Used to halt the message Receiver
    public void stopRunning() {running.set(false);}

}
