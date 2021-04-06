package com.ncnf.event;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.organizer.PublicOrganizer;

import java.util.Date;

public class EventActivity extends AppCompatActivity {

    private final Event event1 = new PublicEvent("EPFL", "EPFL event", new Date(2021, 03, 11), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "Event description goes here", Event.Type.Conference, 0, 0);
    private final Event event2 = new PublicEvent("Opéra de Lausanne", "Carmen", new Date(2021, 04, 24), new GeoPoint(46.517789, 6.636917), "Avenue du Théâtre 12, 1002 Lausanne", "Carmen opera function", Event.Type.Opera, 0, 0);
    private final Event[] events = {event1, event2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Event event = events[getIntent().getIntExtra("EVENT_NUM", 0)];

        TextView name = findViewById(R.id.eventName);
        name.setText(event.getName());

        TextView date = findViewById(R.id.eventDate);
        date.setText("Event takes place on : " + event.getDate().toString());

        TextView loc = findViewById(R.id.eventLocation);
        loc.setText("Event held at : " + event.getAddress());

        TextView desc = findViewById(R.id.eventDescription);
        desc.setText(event.getDescription());

        TextView owner = findViewById(R.id.eventOwner);
        owner.setText("Event hosted by " + event.getOwnerId());
    }
}