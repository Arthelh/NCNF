package com.ncnf.socialObject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;
import com.ncnf.storage.CacheFileStore;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.storage.FileStore;

import static com.ncnf.Utils.UUID_KEY;

public class SocialObjActivity extends AppCompatActivity {

    private static final EventDB db = new EventDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        String event_uid = getIntent().getStringExtra(UUID_KEY);
        SocialObject socialObject = db.getSocialObj(event_uid);
        if (socialObject == null) {
            finish();
            return;
        }

        // Change with socialObject UUID
        ImageView imageView = findViewById(R.id.eventImage);
        FileStore file = new CacheFileStore(this, SocialObject.IMAGE_PATH, String.format(SocialObject.IMAGE_NAME, "PLEASE_REPLACE_WITH_UUID"));
        file.downloadImage(imageView);

        TextView name = findViewById(R.id.eventName);
        name.setText(socialObject.getName());

        TextView date = findViewById(R.id.eventDate);
        date.setText("SocialObject takes place on : " + new DateAdapter(socialObject.getDate()).toString());

        TextView loc = findViewById(R.id.eventLocation);
        loc.setText("SocialObject held at : " + socialObject.getAddress());

        TextView desc = findViewById(R.id.eventDescription);
        desc.setText(socialObject.getDescription());

        TextView owner = findViewById(R.id.eventOwner);
        owner.setText("SocialObject hosted by " + socialObject.getOwnerId());
    }
}