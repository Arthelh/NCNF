package com.ncnf.event;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;
import com.ncnf.storage.CacheFileStore;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.storage.FileStore;

import static com.ncnf.Utils.UUID_KEY;

public class EventActivity extends AppCompatActivity {

    private static final EventDB db = new EventDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        String event_uid = getIntent().getStringExtra(UUID_KEY);
        Social social = db.getEvent(event_uid);
        if (social == null) {
            finish();
            return;
        }

        // Change with social UUID
        ImageView imageView = findViewById(R.id.eventImage);
        FileStore file = new CacheFileStore(this, Social.IMAGE_PATH, String.format(Social.IMAGE_NAME, "PLEASE_REPLACE_WITH_UUID"));
        file.downloadImage(imageView);

        TextView name = findViewById(R.id.eventName);
        name.setText(social.getName());

        TextView date = findViewById(R.id.eventDate);
        date.setText("Social takes place on : " + new DateAdapter(social.getDate()).toString());

        TextView loc = findViewById(R.id.eventLocation);
        loc.setText("Social held at : " + social.getAddress());

        TextView desc = findViewById(R.id.eventDescription);
        desc.setText(social.getDescription());

        TextView owner = findViewById(R.id.eventOwner);
        owner.setText("Social hosted by " + social.getOwnerId());
    }
}