package com.example.jesus.cryptochat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jesus.cryptochat.R;
import com.example.jesus.cryptochat.encryption.SimpleCrypt;
import com.example.jesus.cryptochat.messaging.Message;
import com.example.jesus.cryptochat.messaging.MessageAdapter;
import com.example.jesus.cryptochat.messaging.MessageSerializer;
import com.example.jesus.cryptochat.networking.MessageReceiver;
import com.example.jesus.cryptochat.networking.MessageSender;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The mainActivity centrally manages the application. Holds references to the sender and receiver
 * objects, and handles the application's GUI.
 */
public class MainActivity extends AppCompatActivity {

    private List<Message> messageList;
    private EditText etMessage;  // The editText containing the message to send
    private ListView messageDisplayer; // The listView used to display the chat

    private Handler handler;
    private MessageAdapter messageAdapter;
    private MessageReceiver messageReceiver; // The thread to receive messages
    private MessageSender messageSender;     // The thread to send messages

    private WifiManager.MulticastLock multiCastLock; // Need to acquire a multicast lock in order to preform multicast network operations
    private SharedPreferences sharedPreferences;
    private int msgId; // A counter used to differentiate duplicate messages

    private static String generateDefaultName() {
        Random rand = new Random();

        String[] adjective = {"Funny", "Strange", "Clever", "Brave", "Lazy", "Melancholy",
                "Intelligent", "Quick", "Confused", "Angry"};

        String[] animal = {"Moose", "Sheep", "Hamster", "Chicken", "Frog",
                "Lizard", "Gopher", "Raccoon", "Weasel", "Penguin"};

        return adjective[rand.nextInt(10)] + " " + animal[rand.nextInt(10)];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Standard initializations
        msgId = 0;
        etMessage = findViewById(R.id.etMessage);
        messageList = new ArrayList<>();
        messageDisplayer = findViewById(R.id.lvMessages);
        messageAdapter = new MessageAdapter(this, R.layout.chat_bubble_view, messageList);
        messageDisplayer.setAdapter(messageAdapter);
        handler = new Handler();


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (!sharedPreferences.getBoolean(PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, false)) {
            PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref_general, true);
            PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref_advanced, true);
        }


        String displayName = sharedPreferences.getString("display_name", "DEFAULT_DISPLAY_NAME");

        if (displayName.equals("DEFAULT_DISPLAY_NAME")) {
            sharedPreferences.edit().putString("display_name", generateDefaultName()).apply();
        }

    }

    // Android operating system will close socket/remove multicast lock when application is minimized,
    // This restores these upon resuming the app
    @Override
    protected void onResume() {
        super.onResume();

        if (multiCastLock == null || !multiCastLock.isHeld()) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            multiCastLock = wifi.createMulticastLock("CryptoChat");
        }

        listen(); // starts listening for messages
    }

    public void doMessage(final Message message) {
        if (message != null) {
            handler.post(() -> {
                if (!messageList.contains(message)) {

                    messageList.add(message);
                    messageAdapter.notifyDataSetChanged(); // Update the messageDisplayer view
                    messageDisplayer.setSelection(messageAdapter.getCount() - 1); // Scroll to bottom
                }
            });
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Message> message_list = savedInstanceState.getParcelableArrayList("message_list");
        if (!message_list.isEmpty()) {
            messageList = message_list;
            messageAdapter = new MessageAdapter(this, R.layout.chat_bubble_view, messageList);
            messageDisplayer.setAdapter(messageAdapter);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("message_list", (ArrayList<? extends Parcelable>) messageList);

        super.onSaveInstanceState(outState);
    }

    // Adds settings button to the action bar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Launches the settings page when the settings button is clicked.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }


    /**
     *  This method checks to ensure the wifi is connected. If so, it then acquires a multicast lock,
     *  and starts a new MessageReceiver thread
     */
    private void listen() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            multiCastLock.acquire();
            InetAddress ipAddress = null;
            try {
                ipAddress = InetAddress.getByName(sharedPreferences.getString("ip_address", "127.0.0.1"));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            int port = Integer.valueOf((sharedPreferences.getString("port", "1337")));

            if (messageReceiver != null) {
                messageReceiver.stopRunning();
            }

            messageReceiver = new MessageReceiver(sharedPreferences, this, ipAddress, port);
            messageReceiver.start();
        } else {
            Toast.makeText(this, "Please connect wifi", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *  Sends a message by passing the message to be sent to a new message sender object
     *  Ensures message size is less than the max msg size, as if messages are too large
     *  packets will sometimes get lost as UDP doesn't guarantee packet arrival
     */
    public void send(View view) throws UnknownHostException {
        if (etMessage.getText().length() == 0) {
            Toast.makeText(this, "Enter a message to send", Toast.LENGTH_SHORT).show();
            return;
        }

        String displayName = sharedPreferences.getString("display_name", "ERROR");
        String messageBody = etMessage.getText().toString();

        if (sharedPreferences.getBoolean("encryption_enabled", false)) {
            String password = sharedPreferences.getString("encryption_password", "");
            try {
                messageBody = SimpleCrypt.encrypt(messageBody, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Pack message, serialize, then send
        Message messageToSend = new Message(++msgId, displayName, messageBody);
        int port = Integer.valueOf(sharedPreferences.getString("port", "1337"));
        InetAddress ipAddress = InetAddress.getByName(sharedPreferences.getString("ip_address", "127.0.0.1"));
        byte[] serializedMessage = MessageSerializer.serialize(messageToSend);

        messageSender = new MessageSender(ipAddress, port, serializedMessage);
        messageSender.start();
        etMessage.setText("");
    }

    /**
     * Ensures everything cleans up nicely when the application closes, as it can cause problems
     * if it remains running on subsequent application launches
     */
    protected void onDestroy() {
        super.onDestroy();
        if(multiCastLock.isHeld()) multiCastLock.release();
        if(messageReceiver != null) messageReceiver.stopRunning();
        if(messageSender != null) messageSender.interrupt();

    }
}
