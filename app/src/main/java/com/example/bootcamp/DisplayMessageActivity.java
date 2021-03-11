package com.example.bootcamp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String[] messages = message.split("/");
        String user = messages[0];
        String id = messages[1];

        Log.d(MainActivity.TAG, "got the following strings :"+ user + " " + id);

        // Capture the layout's TextView and set the string as its text
        TextView name = findViewById(R.id.name_display);
        name.setText("Hello there! Oh General " + user);

        TextView id_field = findViewById(R.id.id_field);
        id_field.setText("This is your generated id : " + id);
    }
}