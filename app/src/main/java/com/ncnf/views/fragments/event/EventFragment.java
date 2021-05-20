package com.ncnf.views.fragments.event;

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
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Event;
import com.ncnf.models.SocialObject;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.SaveToCalendar;

import java.time.ZoneId;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EventFragment extends Fragment {

    @Inject
    public FirebaseCacheFileStore fileStore;

    private final Event event;
    
    @Inject
    public FirebaseDatabase dbs;

    public EventFragment(Event event){
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
        addToCalendar.setOnClickListener(v -> {
            Intent calendarIntent = SaveToCalendar.createCalendarIntent(event);
            if (calendarIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(calendarIntent);
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
        calendar.setDate(event.getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        TextView loc = view.findViewById(R.id.eventLocation);
        loc.setText("Event held at : " + event.getAddress());

        TextView desc = view.findViewById(R.id.eventDescription);
        desc.setText(event.getDescription());

        TextView owner = view.findViewById(R.id.eventOwner);

        owner.setText("Event hosted by " + event.getEmail());
    }
}