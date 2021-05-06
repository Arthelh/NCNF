package com.ncnf.socialObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;
import com.ncnf.database.DatabaseService;
import com.ncnf.storage.CacheFileStore;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.storage.FileStore;

import javax.inject.Inject;

import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.EVENTS_COLLECTION_KEY;
import static com.ncnf.Utils.UUID_KEY;

public class SocialObjActivity extends AppCompatActivity {

    private static DatabaseService db = new DatabaseService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        String event_uid = getIntent().getStringExtra(UUID_KEY);
        db.getDocument(EVENTS_COLLECTION_KEY + event_uid, Event.class).thenAccept(
                event -> {
                    ImageView imageView = findViewById(R.id.eventImage);
                    FileStore file = new CacheFileStore(this, Event.IMAGE_PATH, String.format(Event.IMAGE_NAME, "PLEASE_REPLACE_WITH_UUID"));
                    file.downloadImage(imageView);

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
                }).exceptionally(e -> {
            Log.d(DEBUG_TAG, "Event Activity creation failed, event null");
            finish();
            return null;
        });
    }
}