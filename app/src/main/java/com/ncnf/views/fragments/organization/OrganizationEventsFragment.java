package com.ncnf.views.fragments.organization;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.ncnf.adapters.EventListAdapter;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.views.fragments.event.EventFragment;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.FRAGMENT_ORGANIZATION_TAG;

@AndroidEntryPoint
public class OrganizationEventsFragment extends Fragment {

    @Inject
    public OrganizationRepository organizationRepository;

    private Organization organization;
    private String uuid;

    private RecyclerView recyclerView;
    private EventListAdapter adapter;
    private final List<Event> eventsList = new LinkedList<>();

    public OrganizationEventsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        //if it is null it should fail
        this.uuid = this.getArguments().getString("organization_id");
        return inflater.inflate(R.layout.fragment_organization_events, container, false);
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.recyclerView = view.findViewById(R.id.organization_events_recyclerview);

        RecyclerView.LayoutManager lManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(lManager);

        fetchOrganizationEvents();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.organization_events_menu, menu);
        //addOrgButton = menu.findItem(R.id.add_organization_button);
        super.onCreateOptionsMenu(menu, inflater);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //No switch case because R.ids won't be supported in switch case stmts soon
        if (id == R.id.add_event_organization_button) {
            createEvent();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchOrganizationEvents() {
        //TODO check if uuid is corrected there
        organizationRepository.getByUUID(this.uuid).thenAccept(o -> {
            organizationRepository.getOrganizationEvents(o.get(0).getUuid().toString()).thenAccept(le -> {
                this.eventsList.clear();
                //TODO get all events
                this.eventsList.addAll(le);
                this.adapter = new EventListAdapter(getContext(), this.eventsList, this::onEventClick, EventListAdapter.SortingMethod.DATE);
                this.recyclerView.setAdapter(adapter);
            }).exceptionally(e -> {e.printStackTrace(); return null;});
        }).exceptionally(e -> {e.printStackTrace(); return null;});
    }

    private void createEvent() {
        Bundle args = new Bundle();
        args.putString("organization_id", this.uuid);

        EventCreateFragment eventCreateFrag = new EventCreateFragment();
        eventCreateFrag.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(((ViewGroup) requireView().getParent()).getId(), eventCreateFrag, null)
                .addToBackStack(FRAGMENT_ORGANIZATION_TAG)
                .commit();
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
        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
    }

    private void destroyChildFragment(FragmentManager fragmentManager, Fragment fragment, ConstraintLayout feedContainer, OnBackPressedCallback callback) {
        fragmentManager.beginTransaction().remove(fragment).commit();
        feedContainer.setVisibility(View.GONE);
        this.recyclerView.setVisibility(View.VISIBLE);
        callback.setEnabled(false);
    }


}
