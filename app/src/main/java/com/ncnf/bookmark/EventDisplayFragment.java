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
import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.EventActivity;
import com.ncnf.event.EventBuilder;
import com.ncnf.feed.ui.EventAdapter;
import com.ncnf.feed.ui.EventListener;
import com.ncnf.user.PrivateUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.ncnf.Utils.DEBUG_TAG;

public class EventDisplayFragment extends Fragment implements EventAdapter.OnEventListener{

    private List<Event> eventsToDisplay;
    private EventAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private final String eventCollection;

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
        getEventList(view.findViewById(R.id.SavedEventsRecyclerView));


        eventsToDisplay = new ArrayList<>();
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.SavedEventsRecyclerView);
        // Use LinearLayout as the layout manager
        lManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        adapter = new EventAdapter(eventsToDisplay, this);
        recycler.setAdapter(adapter);

        EventListener eventListener = event -> getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Received event with data");
                adapter.addEvent(event);
                ((LinearLayoutManager)lManager).scrollToPositionWithOffset(0, 0);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getEventList(View view){
        PrivateUser user = new PrivateUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getEmail());
        CompletableFuture<CompletableFuture<List<Event>>> listEvent = user.getAllEvents(eventCollection);

        listEvent.thenAccept(task1 -> {
            task1.thenAccept(events -> {
                if(events != null){
                    eventsToDisplay = events;
                    adapter = new EventAdapter(eventsToDisplay, this);
                    ((RecyclerView) view.findViewById(R.id.SavedEventsRecyclerView)).setAdapter(adapter);
                }
            });


        });
    }

    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("event_uid", event.getUuid());
        startActivity(intent);
    }
}