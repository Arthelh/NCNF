package com.ncnf.socialObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;
import com.ncnf.storage.CacheFileStore;
import com.ncnf.storage.FileStore;
import com.ncnf.utilities.DateAdapter;

import static com.ncnf.utilities.StringCodes.UUID_KEY;

public class SocialObjActivity extends AppCompatActivity {

    private static final EventDB db = new EventDB();
    private SocialObject socialObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        String event_uid = getIntent().getStringExtra(UUID_KEY);
        socialObject = db.getSocialObj(event_uid);
        if (socialObject == null) {
            finish();
        }
        initView();
        Button addToCalendar = findViewById(R.id.button_calendar);
        addToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarIntent = Add2Calendar.createCalendarIntent(socialObject);
                if (calendarIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(calendarIntent);
                }
            }
        });
    }

    private void initView(){
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