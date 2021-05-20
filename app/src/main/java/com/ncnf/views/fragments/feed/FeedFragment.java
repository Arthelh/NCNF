package com.ncnf.views.fragments.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.ncnf.R;

import com.ncnf.adapters.EventListAdapter;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.utilities.map.MapUtilities;
import com.ncnf.utilities.settings.Settings;
import com.ncnf.models.Event;
import com.ncnf.views.fragments.event.EventFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.DEBUG_TAG;
import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;

@AndroidEntryPoint
public class FeedFragment extends Fragment {

    @Inject
    public FirebaseDatabase firebaseDatabase;

    private RecyclerView.LayoutManager lManager;
    private RecyclerView recycler;
    private EventListAdapter adapter;
    private List<Event> eventList = new ArrayList<>();

    private static final String CHANNEL_NAME = "events_to_be_shown";

    public FeedFragment(){
        super();
    }

    public FeedFragment(List<Event> eventList){
        super();

        Objects.requireNonNull(eventList);
        this.eventList = eventList;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (eventList.isEmpty())
            actualizeEvents();
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
        recycler = (RecyclerView) requireView().findViewById(R.id.feed_recycler_view);

        // Use LinearLayout as the layout manager
        lManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        if (eventList.isEmpty()){
            actualizeEvents();
        } else {
            adapter = new EventListAdapter(getContext(), eventList, this::onEventClick, EventListAdapter.SortingMethod.DATE);
            recycler.setAdapter(adapter);
        }
    }

    protected void onEventClick(Event e) {
        Fragment fragment = new EventFragment(e);
        Window globalWindow = requireActivity().getWindow();
        FragmentManager fragmentManager = getChildFragmentManager();

        ConstraintLayout feedContainer = globalWindow.findViewById(R.id.feed_event_container);
        FrameLayout feedFrame = globalWindow.findViewById(R.id.feed_event_fragment);
        //Button feedButton = globalWindow.findViewById(R.id.feed_event_button);

        recycler.setVisibility(View.INVISIBLE);

        feedContainer.setBackgroundResource(R.drawable.main_background_gradient);
        feedContainer.setVisibility(View.VISIBLE);
        feedFrame.setVisibility(View.VISIBLE);
        //feedButton.setVisibility(View.VISIBLE);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.feed_event_fragment, fragment).commit();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                destroyChildFragment(fragmentManager, fragment, feedContainer, this);
            }
        };

        /**
        feedButton.setOnClickListener(v -> {
            destroyChildFragment(fragmentManager, fragment, feedContainer, callback);
        });
         **/

        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
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
                adapter.orderBy(EventListAdapter.SortingMethod.DATE);
                break;
            case R.id.relevanceOrder :
                adapter.orderBy(EventListAdapter.SortingMethod.RELEVANCE);
                break;
        }
        return true;
    }

    private void destroyChildFragment(FragmentManager fragmentManager, Fragment fragment, ConstraintLayout feedContainer, OnBackPressedCallback callback){
        fragmentManager.beginTransaction().remove(fragment).commit();
        feedContainer.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);
        callback.setEnabled(false);
    }

    private void actualizeEvents(){
        final List<Event> result = new ArrayList<>();

        CompletableFuture<List<Event>> completableFuture = firebaseDatabase.geoQuery(Settings.getUserPosition(), Settings.getCurrentMaxDistance() * 1000, EVENTS_COLLECTION_KEY, Event.class);
        completableFuture.thenAccept(eventList -> {

            for (Event e : eventList){
                LatLng eventPosition = new LatLng(e.getLocation().getLatitude(), e.getLocation().getLongitude());
                if (MapUtilities.position_in_range(Settings.getUserPosition(), eventPosition))
                    result.add(e);
            }
            adapter = new EventListAdapter(getContext(), result, this::onEventClick, EventListAdapter.SortingMethod.DATE);
            recycler.setAdapter(adapter);

        }).exceptionally(e -> {

            Log.d(DEBUG_TAG, e.getMessage());
            return null;

        });
    }
}
