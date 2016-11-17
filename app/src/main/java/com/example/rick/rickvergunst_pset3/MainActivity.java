package com.example.rick.rickvergunst_pset3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // Create variables
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText mEdit;
    TextView welcomeMessage;

    public static final String myPreference = "mypreference";
    public static final String Name = "nameKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve preferences and assign an editor
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Check whether a username has been set
        if (sharedPreferences.contains(Name)) {
            // If so, start regurarly with a welcome screen
            setContentView(R.layout.activity_main);
            welcomeMessage = (TextView)findViewById(R.id.welcomeMessage);
            welcomeMessage.append(" " + sharedPreferences.getString(Name, ""));
        }
        else {
            // Else start with a screen to set a username
            setContentView(R.layout.set_user);
            mEdit = (EditText)findViewById(R.id.setUsername);
        }
    }

    // Retrieve username and put it inside the preferences
    public void setUser(View view) {
        String name = mEdit.getText().toString();
        editor.putString(Name, name);
        editor.commit();

        // Restart current activity to go to the welcome screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    // Three functions to navigate the app, consistent within the app
    public void toHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void toSearchPage(View view) {
        Intent intent = new Intent(this, SearchPage.class);
        startActivity(intent);
        this.finish();
    }

    public void toWatchList(View view) {
        Intent intent = new Intent(this, WatchList.class);
        startActivity(intent);
        this.finish();
    }
}
