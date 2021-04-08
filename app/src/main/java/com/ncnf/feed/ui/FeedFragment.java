package com.ncnf.feed.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.PublicEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FeedFragment extends Fragment {

    private RecyclerView.LayoutManager lManager;
    private EventAdapter adapter;
    private final List<Event> eventList = new ArrayList<>();
    private static final String CHANNEL_NAME = "events_to_be_shown";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the RecyclerView
        RecyclerView recycler = (RecyclerView) getView().findViewById(R.id.recycler_view);

        // Use LinearLayout as the layout manager
        lManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        //TODO Connect DB events here
        eventList.add(new PublicEvent("testOrganizer", "testName", new Date(), new GeoPoint(0, 0), "testLoc", "testData", Event.Type.Museum, 0 , 0));
        adapter = new EventAdapter(eventList, this::onEventClick);
        recycler.setAdapter(adapter);
    }

    private void onEventClick(Event e) {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("event_uid", eventList.get(eventList.indexOf(e)).getUuid().toString());
        startActivity(intent);
    }
}
