package com.example.jesus.cryptochat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The mainActivity centrally manages the application. Holds references to the sender and receiver
 * objects, and handles the applications GUI.
 */
public class MainActivity extends AppCompatActivity {

    private EditText etMessage;  // The editText containing the message to send
    private ListView lvMessages; // The listView used to display the chat
    private String displayName = "";
    private String password = "";
    private TextView tvCharCount;
    private MessageReceiver messageReceiver; // The thread to receive messages
    private MessageSender messageSender;     // The thread to send messages
    private WifiManager.MulticastLock multiCastLock; // Need to acquire a multicast lock in order to preform multicast network operations


    // When switching back to main activity from settings, calls this to return the result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                displayName = bundle.getString("displayName");
                password = bundle.getString("password");
                messageReceiver.updatePassword(password);
            }
        }
    }

    // Switch case depending on which item from the overflow menu on the action bar the user selects
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingsIntent, 1);
                return true;
            case R.id.action_about:
                Toast.makeText(this, "Created by Justin DeCunha", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Standard initializations
        etMessage = (EditText) findViewById(R.id.etMessage);

        lvMessages = (ListView) findViewById(R.id.lvMessages);
        tvCharCount = (TextView) findViewById(R.id.tvCharCount);
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        multiCastLock = wifi.createMulticastLock("CryptoChat");

        listen(); // starts listening for messages
        editTextHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu); // Adds action bar components
        return super.onCreateOptionsMenu(menu);
    }

    /**
     *  This method checks to ensure the wifi is connected. If so, it then acquires a multicast lock,
     *  and starts a new MessageReceiver thread
     */
    private void listen() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            multiCastLock.acquire();
            messageReceiver = new MessageReceiver(this, lvMessages);
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
    protected void send(View view) {
        if(etMessage.getText().length() < Constants.MSG_MAX_BYTES) {

            messageSender = new MessageSender(this, displayName, password, etMessage.getText().toString());
            messageSender.start();
            etMessage.setText("");

        } else {
            Toast.makeText(this, "Message is too large!", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Limits the amount of characters the user can send in a message - too long risks packet being split by router
     * Because multicast isn't a TCP connection, packets being received isn't entirely guaranteed. Risk of this is minimized
     * by using a small packet size.
     */
    private void editTextHandler() {
        etMessage.setText("");
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int x = charSequence.length();
                tvCharCount.setText(String.valueOf(x) + "/" + Constants.MSG_MAX_BYTES);
                if(x >= Constants.MSG_MAX_BYTES) {
                    etMessage.setKeyListener(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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
