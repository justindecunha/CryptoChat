package com.example.jesus.cryptochat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Handles the settings screen
 */
public class SettingsActivity extends AppCompatActivity {

    private EditText etDisplayName; // The editText containing the display name
    private EditText etPassword; // The editText containing the password to encrypt/decrypt with

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        etDisplayName = (EditText) findViewById(R.id.etDisplayName);
        etPassword = (EditText) findViewById(R.id.etPassword);
    }

    // Returns the result to the mainActivity
    protected void update(View view) {
        Intent returnIntent = new Intent();
        Bundle extras = new Bundle();
        extras.putString("displayName", etDisplayName.getText().toString());
        extras.putString("password", etPassword.getText().toString());
        returnIntent.putExtras(extras);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
