package com.ncnf.feed;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;


public class EventActivity extends AppCompatActivity {

    ImageView imageView;
    TextView name, tag;
    String alphaName, alphaTag;
    int alphaImage;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_page);

        imageView = findViewById(R.id.image);
        name = findViewById(R.id.name);
        tag = findViewById(R.id.tag);

        //get the intent
        alphaName = getIntent().getStringExtra("name");
        alphaTag = getIntent().getStringExtra("tag");
        alphaImage = getIntent().getIntExtra("image", 0);

        //now set the get values in the respective widgets
        name.setText(alphaName);
        tag.setText(alphaTag);
        imageView.setImageResource(alphaImage);

        getIncomingIntent();

    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("event_uid")) {
            String event_uid = getIntent().getStringExtra("event_uid");
            name.setText(event_uid);
        }
    }

}

