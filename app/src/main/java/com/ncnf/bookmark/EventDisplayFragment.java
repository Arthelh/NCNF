package com.ncnf.bookmark;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;
import com.ncnf.feed.ui.EventActivity;
import com.ncnf.event.EventBuilder;
import com.ncnf.feed.ui.EventAdapter;
import com.ncnf.feed.ui.EventListener;
import com.ncnf.user.PrivateUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.Utils.DEBUG_TAG;

@AndroidEntryPoint
public class EventDisplayFragment extends Fragment implements EventAdapter.OnEventListener{

    private List<Event> eventsToDisplay;
    private EventAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private final String eventCollection;

    @Inject
    public PrivateUser user;

    public EventDisplayFragment(String eventCollection){
        this.eventCollection = eventCollection;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventsToDisplay = new ArrayList<>();
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.SavedEventsRecyclerView);
        // Use LinearLayout as the layout manager
        lManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        adapter = new EventAdapter(eventsToDisplay, this);
        recycler.setAdapter(adapter);
        getEventList(view.findViewById(R.id.SavedEventsRecyclerView));
    }

    private void getEventList(View view){
        if(user != null){
            CompletableFuture<CompletableFuture<List<Event>>> listEvent = user.getAllEvents(eventCollection);

            listEvent.thenAccept(task -> task.thenAccept(events -> {
                if(events != null){
                    for(Event e : events){
                        if (e != null) {
                            this.adapter.addEvent(e);
                        }
                    }
                }
            }));
        }
    }

    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("event_uid", event.getUuid().toString());
        startActivity(intent);
    }
}