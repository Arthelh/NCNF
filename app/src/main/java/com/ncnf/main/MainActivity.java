package com.ncnf.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;

<<<<<<< HEAD:app/src/main/java/ncnf/main/MainActivity.java
import ncnf.eventCreation.EventCreationActivity;
import ncnf.feed.FeedActivity;
import ncnf.main.map.MapActivity;
=======
import com.ncnf.feed.FeedActivity;
import com.ncnf.map.MapActivity;
>>>>>>> main:app/src/main/java/com/ncnf/main/MainActivity.java

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launchFeed = (Button) findViewById(R.id.feed_view_button);
        Button launchEventCreation = (Button) findViewById(R.id.create_event_button);

        launchFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedView();
            }
        });

        launchEventCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventCreationView();
            }
        });
    }

    /*Called when the user clicks the button*/
    public void goToMap(View view){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void feedView() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }

    public void eventCreationView(){
        Intent intent = new Intent(this, EventCreationActivity.class);
        startActivity(intent);
    }
}