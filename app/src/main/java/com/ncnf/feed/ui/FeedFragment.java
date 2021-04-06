package com.ncnf.feed.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.EventType;
import com.ncnf.event.Location;
import com.ncnf.event.PublicEvent;
import com.ncnf.organizer.PublicOrganizer;

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
        eventList.add(new PublicEvent("testName", new Date(), new Location(0, 0, "testLoc"), "testData", EventType.Museum, 0 , 0, new PublicOrganizer("testOrganizer")));
        adapter = new EventAdapter(eventList, this::onEventClick);
        recycler.setAdapter(adapter);

//        EventListener eventListener = event -> runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("Received event with data: " + event.getDescription());
//
//                adapter.addEvent(event);
//                ((LinearLayoutManager)lManager).scrollToPositionWithOffset(0, 0);
//            }
//        });
    }

    private void onEventClick(Event e) {
        Toast.makeText(FeedFragment.this.getContext(),"HELLO", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("event_uid", eventList.get(eventList.indexOf(e)).getUuid().toString());
        startActivity(intent);
    }
}