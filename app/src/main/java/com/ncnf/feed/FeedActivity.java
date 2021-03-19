package com.ncnf.feed;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ncnf.event.Event;
import com.ncnf.event.EventType;
import com.ncnf.event.Location;
import com.ncnf.event.PublicEvent;
import com.ncnf.organizer.PublicOrganizer;

public class FeedActivity extends AppCompatActivity implements EventAdapter.OnEventListener {
    private RecyclerView.LayoutManager lManager;
    private EventAdapter adapter;
    private List<Event> eventList;
    private static final String CHANNEL_NAME = "events_to_be_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_feed);
        setContentView(R.layout.activity_feed);
        findViewById(R.id.recycler_view);
        // Get the RecyclerView
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view);

        // Use LinearLayout as the layout manager
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        eventList = new ArrayList<>();
        eventList.add(new PublicEvent("testName", new Date(), new Location(0, 0, "testLoc"), "testData", EventType.Museum, 0 , 0, new PublicOrganizer("testOrganizer")));
        adapter = new EventAdapter(eventList, this);
        recycler.setAdapter(adapter);

        EventListener eventListener = event -> runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Received event with data: " + event.getDescription());

                adapter.addEvent(event);
                ((LinearLayoutManager)lManager).scrollToPositionWithOffset(0, 0);
            }
        });
    }

    @Override
    public void onEventClick(int position) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("event_uid", eventList.get(position).getUuid().toString());
        startActivity(intent);
    }
}