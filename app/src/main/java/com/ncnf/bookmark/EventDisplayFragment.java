package com.ncnf.bookmark;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.EventActivity;
import com.ncnf.feed.ui.EventAdapter;
import com.ncnf.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.UUID_KEY;

@AndroidEntryPoint
public class EventDisplayFragment extends Fragment implements EventAdapter.OnEventListener{

    private List<Event> eventsToDisplay;
    private EventAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private final String eventCollection;

    @Inject
    public User user;

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
        adapter = new EventAdapter(eventsToDisplay, this, EventAdapter.SortingMethod.DATE);
        recycler.setAdapter(adapter);
        getEventList(view.findViewById(R.id.SavedEventsRecyclerView));
    }

    private void getEventList(View view){
        if(user != null){
            CompletableFuture<List<Event>> listEvent = user.getOwnedEvents();

            listEvent.thenAccept(events -> {
                if(events != null){
                    this.adapter.setEvents(events);
                }
            });
        }
    }

    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        Log.d(DEBUG_TAG, "Going on event activity");
        startActivity(intent);
    }
}