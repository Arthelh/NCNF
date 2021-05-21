package com.ncnf.views.fragments.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.adapters.EventAdapter;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.views.fragments.event.EventFragment;
import com.ncnf.views.fragments.feed.FeedFragment;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrganizationEventsFragment extends Fragment {

    @Inject
    public OrganizationRepository organizationRepository;

    private Organization organization;

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private final List<Event> eventsList = new LinkedList<>();

    public OrganizationEventsFragment() {
        String uuid = getArguments().getString("organization_id");
        organizationRepository.getByUUID(uuid).thenAccept(o ->
                this.organization = o.get(0)
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_organization_events, container, false);
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = recyclerView.findViewById(R.id.organization_events_recyclerview);

        RecyclerView.LayoutManager lManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(lManager);

        fetchOrganizationEvents();

        if(this.eventsList.isEmpty()){
            //TODO display empty message?
        } else {
            this.adapter = new EventAdapter(getContext(), this.eventsList, this::onEventClick, EventAdapter.SortingMethod.DATE);
            this.recyclerView.setAdapter(adapter);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //No switch case because R.ids won't be supported in switch case stmts soon
        if (id == R.id.add_event_organization_button) {
            createEvent();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchOrganizationEvents(){
        //TODO check if uuid is corrected there
        organizationRepository.getOrganizationEvents(organization.getUuid().toString()).thenAccept(lo ->{
                this.eventsList.clear();
                this.eventsList.addAll(lo);
        });
    }

    private void createEvent(){
        //TODO
    }

    //TODO put into another class
    private void onEventClick(Event e) {
        Fragment fragment = new EventFragment(e);
        Window globalWindow = requireActivity().getWindow();
        FragmentManager fragmentManager = getChildFragmentManager();

        ConstraintLayout feedContainer = globalWindow.findViewById(R.id.organization_events_container);
        FrameLayout feedFrame = globalWindow.findViewById(R.id.organization_event_fragment);
        //Button feedButton = globalWindow.findViewById(R.id.feed_event_button);

        this.recyclerView.setVisibility(View.INVISIBLE);

        feedContainer.setBackgroundResource(R.drawable.main_background_gradient);
        feedContainer.setVisibility(View.VISIBLE);
        feedFrame.setVisibility(View.VISIBLE);
        //feedButton.setVisibility(View.VISIBLE);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.organization_events_recyclerview, fragment).commit();

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

    private void destroyChildFragment(FragmentManager fragmentManager, Fragment fragment, ConstraintLayout feedContainer, OnBackPressedCallback callback){
        fragmentManager.beginTransaction().remove(fragment).commit();
        feedContainer.setVisibility(View.GONE);
        this.recyclerView.setVisibility(View.VISIBLE);
        callback.setEnabled(false);
    }


}
