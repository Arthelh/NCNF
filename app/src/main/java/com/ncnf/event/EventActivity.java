package com.ncnf.event;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncnf.R;
import com.ncnf.organizer.PublicOrganizer;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.Location;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    private static final EventDB db = new EventDB();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        String event_uid = getIntent().getStringExtra("event_uid");
        Event event = db.getEvent(event_uid);

        ImageView imageView = findViewById(R.id.eventImage);

        try {
            Field id = R.drawable.class.getDeclaredField(event.getImageName());
            imageView.setImageResource(id.getInt(id));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        TextView name = findViewById(R.id.eventName);
        name.setText(event.getName());

        TextView date = findViewById(R.id.eventDate);
        date.setText("Event takes place on : " + new DateAdapter(event.getDate()).toString());

        TextView loc = findViewById(R.id.eventLocation);
        loc.setText("Event held at : " + event.getLocation().getAddress());

        TextView desc = findViewById(R.id.eventDescription);
        desc.setText(event.getDescription());

        TextView owner = findViewById(R.id.eventOwner);
        owner.setText("Event hosted by " + event.getOrganizer().getName());
    }
}