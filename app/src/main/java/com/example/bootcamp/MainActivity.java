package com.example.bootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.bootcamp.EXTRA_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*Called when the user clicks the button*/
    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.name_field);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void seeEvent1(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("EVENT_NUM", 0);
        startActivity(intent);
    }

    public void seeEvent2(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("EVENT_NUM", 1);
        startActivity(intent);
    }
}