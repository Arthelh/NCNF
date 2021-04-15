package com.ncnf.event;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;
import com.ncnf.utilities.DateAdapter;

import java.lang.reflect.Field;

import static com.ncnf.Utils.UUID_KEY;

public class EventActivity extends AppCompatActivity {

    private static final EventDB db = new EventDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        String event_uid = getIntent().getStringExtra(UUID_KEY);
        Event event = db.getEvent(event_uid);
        if (event == null) {
            finish();
            return;
        }

        ImageView imageView = findViewById(R.id.eventImage);
        try {
            Field id = R.drawable.class.getDeclaredField("rolex");
            imageView.setImageResource(id.getInt(id));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        TextView name = findViewById(R.id.eventName);
        name.setText(event.getName());

        TextView date = findViewById(R.id.eventDate);
        date.setText("Event takes place on : " + new DateAdapter(event.getDate()).toString());

        TextView loc = findViewById(R.id.eventLocation);
        loc.setText("Event held at : " + event.getAddress());

        TextView desc = findViewById(R.id.eventDescription);
        desc.setText(event.getDescription());

        TextView owner = findViewById(R.id.eventOwner);
        owner.setText("Event hosted by " + event.getOwnerId());
    }
}