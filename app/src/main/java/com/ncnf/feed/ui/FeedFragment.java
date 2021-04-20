package com.ncnf.feed.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.EventActivity;
import com.ncnf.event.EventDB;

import java.util.ArrayList;
import java.util.List;


public class FeedFragment extends Fragment {

    private RecyclerView.LayoutManager lManager;
    private EventAdapter adapter;
    private List<Event> eventList = new ArrayList<>();
    private EventDB eventDB;
    private static final String CHANNEL_NAME = "events_to_be_shown";

    public FeedFragment(EventDB eventDB){
        super();
        this.eventDB = eventDB;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        adapter = new EventAdapter(eventDB.toList(), this::onEventClick, EventAdapter.SortingMethod.DATE);
        recycler.setAdapter(adapter);
    }

    private void onEventClick(Event e) {
        Intent intent = new Intent(getActivity(), EventActivity.class);
        intent.putExtra("event_uid", e.getUuid().toString());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_menu, menu);


        MenuItem searchItem = menu.findItem(R.id.action_filterEventsByTag);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.dateOrder :
                adapter.orderBy(EventAdapter.SortingMethod.DATE);
                break;
            case R.id.relevanceOrder :
                adapter.orderBy(EventAdapter.SortingMethod.RELEVANCE);
                break;
        }
        return true;
    }
}
