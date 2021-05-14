package com.ncnf.socialObject.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ncnf.R;
import com.ncnf.database.DatabaseService;
import com.ncnf.socialObject.Event;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.storage.CacheFileStore;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.SaveToCalendar;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.FIRST_NAME_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;

@AndroidEntryPoint
public class EventFragment extends Fragment {

    @Inject
    public CacheFileStore fileStore;

    @Inject
    public DatabaseService dbs;

    private SocialObject event;

    public EventFragment(SocialObject event){
        this.event = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initSaveCalendar(view);

    }

    private void initSaveCalendar(View view){
        Button addToCalendar = view.findViewById(R.id.button_calendar);
        addToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarIntent = SaveToCalendar.createCalendarIntent(event);
                if (calendarIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(calendarIntent);
                }
            }
        });
    }

    private void initViews(View view){
        ImageView imageView = view.findViewById(R.id.eventImage);
        fileStore.setContext(this.getContext());
        fileStore.setPath(SocialObject.IMAGE_PATH, String.format(SocialObject.IMAGE_NAME, event.getUuid()));
        fileStore.downloadImage(imageView, BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.default_event_bg));

        TextView name = view.findViewById(R.id.eventName);
        name.setText(event.getName());

        TextView date = view.findViewById(R.id.eventDate);
        date.setText("Event takes place on : " + new DateAdapter(event.getDate()).toString());

        CalendarView calendar = view.findViewById(R.id.show_event_date);
        calendar.setDate(event.getDate().getTime());

        TextView loc = view.findViewById(R.id.eventLocation);
        loc.setText("Event held at : " + event.getAddress());

        TextView desc = view.findViewById(R.id.eventDescription);
        desc.setText(event.getDescription());

        TextView owner = view.findViewById(R.id.eventOwner);

        CompletableFuture<String> ownerName = dbs.getField(USERS_COLLECTION_KEY + event.getOwnerId(), FIRST_NAME_KEY);
        if(ownerName != null) {
            ownerName.thenAccept(s -> {
                if (s != null) {
                    owner.setText("Event hosted by " + s);
                } else {
                    Event e = (Event) event;
                    owner.setText("Event hosted by " + e.getEmail());
                }
            });
        }
    }
}