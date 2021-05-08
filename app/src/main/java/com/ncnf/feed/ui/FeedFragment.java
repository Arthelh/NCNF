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
import com.ncnf.socialObject.EventDB;
import com.ncnf.socialObject.SocialObjActivity;
import com.ncnf.socialObject.SocialObject;

import java.util.List;
import java.util.Objects;

import static com.ncnf.utilities.StringCodes.UUID_KEY;


public class FeedFragment extends Fragment {

    private RecyclerView.LayoutManager lManager;
    private SocialObjAdapter adapter;
    private List<SocialObject> eventList;
    private static final String CHANNEL_NAME = "events_to_be_shown";

    public FeedFragment(EventDB eventDB){
        super();

        this.eventList = eventDB.toList();
    }

    public FeedFragment(List<SocialObject> eventList){
        super();

        Objects.requireNonNull(eventList);
        this.eventList = eventList;
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
        adapter = new SocialObjAdapter(eventList, this::onEventClick, SocialObjAdapter.SortingMethod.DATE);
        recycler.setAdapter(adapter);
    }

    private void onEventClick(SocialObject e) {
        Intent intent = new Intent(getActivity(), SocialObjActivity.class);
        intent.putExtra(UUID_KEY, e.getUuid().toString());
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
                adapter.orderBy(SocialObjAdapter.SortingMethod.DATE);
                break;
            case R.id.relevanceOrder :
                adapter.orderBy(SocialObjAdapter.SortingMethod.RELEVANCE);
                break;
        }
        return true;
    }
}
