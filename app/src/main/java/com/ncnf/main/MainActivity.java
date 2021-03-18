package com.ncnf.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;

import com.ncnf.event.EventActivity;
import com.ncnf.feed.FeedActivity;
import com.ncnf.map.MapActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView r = (RecyclerView)findViewById(R.id.recycler_view);

        Button launchFeed = (Button) findViewById(R.id.feed_view_button);

        launchFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                feedView();
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