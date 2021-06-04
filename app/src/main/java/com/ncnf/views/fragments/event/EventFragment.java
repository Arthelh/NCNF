package com.ncnf.views.fragments.event;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;
import com.ncnf.models.SocialObject;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.repositories.UserRepository;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.SaveToCalendar;
import com.ncnf.utilities.StringCodes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.UUID_KEY;

@AndroidEntryPoint
public class EventFragment extends Fragment {

    @Inject
    public FirebaseCacheFileStore fileStore;

    @Inject
    public FirebaseUser user;

    @Inject
    public UserRepository userRepository;

    private final Event event;

    @Inject
    public OrganizationRepository organizationRepository;

    public EventFragment(Event event){
        this.event = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initSaveCalendar(view);
        initPublishButtons(view);
        initSaveToBookmarkButton(view);
    }

    private void initSaveCalendar(View view){
        Button addToCalendar = view.findViewById(R.id.eventAddToCalendarButton);
        addToCalendar.setOnClickListener(v -> {
            Intent calendarIntent = SaveToCalendar.createCalendarIntent(event);
            if (calendarIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(calendarIntent);
            }
        });
    }

    private void initSaveToBookmarkButton(View view){
        Button saveToBookmarkButton = view.findViewById(R.id.eventSaveToBookmarkButton);

        saveToBookmarkButton.setOnClickListener(v -> {
            userRepository.addBookmarkEventToUser(this.user.getUid(), this.event.getUuid().toString()).thenAccept(res -> {
                Toast.makeText(getActivity(), "Saved to bookmark", Toast.LENGTH_SHORT).show();
            }).exceptionally(e -> {
                Toast.makeText(getActivity(), "Error saving the Event", Toast.LENGTH_SHORT).show();
                return null;
            });
        });
    }

    private void initViews(View view){
        // Set Header Image
        ImageView headerImageView = view.findViewById(R.id.eventHeaderPicture);
        fileStore.setContext(this.getContext());
        fileStore.setPath(SocialObject.IMAGE_PATH, String.format(SocialObject.IMAGE_NAME, event.getUuid()));
        fileStore.downloadImage(headerImageView, BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.default_event_header_picture));

        // Set Organization Image
        ImageView organizationImageView = view.findViewById(R.id.personal_profile_picture);
        fileStore.setContext(this.getContext());
        fileStore.setPath(SocialObject.IMAGE_PATH, String.format(SocialObject.IMAGE_NAME, event.getUuid()));
        fileStore.downloadImage(organizationImageView, BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.default_profile_picture));

        // Set Event name
        TextView name = view.findViewById(R.id.eventName);
        name.setText(event.getName());

        // Set Event Date
        TextView date = view.findViewById(R.id.eventDate);
        LocalDateTime eventDate = event.getDate();
        date.setText(eventDate.format(DateTimeFormatter.ofPattern("EEE'.' dd MMMM yyyy")));

        // Set Event Time
        TextView time = view.findViewById(R.id.eventTime);
        time.setText(eventDate.format(DateTimeFormatter.ofPattern("HH:mm")));

        // Set Event address
        TextView address = view.findViewById(R.id.eventAddress);
        address.setText(event.getAddress());

        // Set Event description
        TextView description = view.findViewById(R.id.eventDescription);
        description.setText(event.getDescription());

        // Set Organization Name
        TextView organizationName = view.findViewById(R.id.eventOrganization);
        organizationRepository.loadOrganization(event.getOwnerId()).thenAccept(organization -> {
            organizationName.setText(organization.getName());
        }).exceptionally(e -> {
            Log.d(StringCodes.DEBUG_TAG, e.getMessage());
            return null;
        });

    }

    private void initPublishButtons(View view) {
        // By default, the button is gone
        Button button = view.findViewById(R.id.button_publish_event_news);
        button.setVisibility(View.GONE);

        // On button click, open the news publish form
        button.setOnClickListener(b -> {
            Bundle args = new Bundle();
            args.putString(UUID_KEY, event.getUuid().toString());

            Fragment frag = new EventNewsFragment();
            frag.setArguments(args);

            getParentFragmentManager().beginTransaction()
                .replace(((ViewGroup) requireView().getParent()).getId(), frag, null)
                .addToBackStack(null)
                .commit();
        });

        // The button is visible only to admin of the organization
        organizationRepository.getByUUID(event.getOwnerId()).thenAccept(organizations -> {
            Organization organization = organizations.get(0);
            if (organization.getAdminIds().contains(user.getUid())) {
                button.setVisibility(View.VISIBLE);
            }
        });
    }
}