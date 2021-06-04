package com.ncnf.views.fragments.group;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.adapters.UserListAdapter;
import com.ncnf.models.Group;
import com.ncnf.models.User;
import com.ncnf.repositories.FriendsRepository;
import com.ncnf.repositories.GroupRepository;
import com.ncnf.repositories.UserRepository;
import com.ncnf.utilities.Helpers;
import com.ncnf.views.activities.group.FriendsTrackerActivity;
import com.ncnf.views.activities.main.MainActivity;
import com.ncnf.views.fragments.user.PublicProfileFragment;
import com.ncnf.views.fragments.user.UserProfileTabFragment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;
import static java.lang.Math.max;
import static java.lang.Math.min;

@AndroidEntryPoint
public class GroupDisplayFragmentOwner extends Fragment {

    @Inject
    public User user;

    @Inject
    public GroupRepository repository;

    @Inject
    public FriendsRepository friendsRepository;

    @Inject
    public UserRepository userRepository;

    private String groupID;
    private Group group;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private EditText groupName;
    private TextView groupAddress;
    private EditText groupDescription;
    private TextView groupOwner;

    private TextView groupDate;
    private TextView groupTime;

    private GeoPoint meetingPointLocation;
    private GeoPoint userLocation;

    private ActivityResultLauncher<Intent> searchBarLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> updateGroupLocation(result));

    private RecyclerView recycler;
    private UserListAdapter adapter;

    private Button button;
    private Button addMembersButton;
    private Button deleteGroupButton;

    private MaterialButton editGroupButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display_group_owner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupName = view.findViewById(R.id.group_display_name_editable);
        groupDescription = view.findViewById(R.id.group_display_description_editable);
        groupAddress = view.findViewById(R.id.group_address_editable);
        groupOwner = view.findViewById(R.id.group_owner_editable);
        addMembersButton = view.findViewById(R.id.add_members_button);

        groupDate = view.findViewById(R.id.group_date_editable);
        groupTime = view.findViewById(R.id.group_time_editable);

        recycler = view.findViewById(R.id.group_attendees_view_editable);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        button = view.findViewById(R.id.open_map_button_editable);
        deleteGroupButton = view.findViewById(R.id.delete_group_button);
        editGroupButton = view.findViewById(R.id.edit_group_button);
        editGroupButton.setIcon(requireActivity().getDrawable(R.drawable.ic_outline_edit_24));

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            this.groupID = bundle.getString("GROUP_ID");

            CompletableFuture<Group> thisGroup = repository.loadGroup(groupID);

            thisGroup.thenAccept(group -> {
                this.group = group;
                prepareAllFields();
            });

        }
    }

    private void prepareAllFields() {

        this.userLocation = user.getLocation();
        this.meetingPointLocation = group.getLocation();

        groupName.setText(group.getName());
        groupDescription.setText(group.getDescription());
        groupAddress.setText(group.getAddress());

        LocalDate date = LocalDate.of(group.getDate().getYear(), group.getDate().getMonth(), group.getDate().getDayOfMonth());
        groupDate.setText(date.toString());

        LocalTime time = LocalTime.of(group.getDate().getHour(), group.getDate().getMinute());
        groupTime.setText(time.toString());

        groupName.setEnabled(false);
        groupDescription.setEnabled(false);
        groupAddress.setEnabled(false);
        groupDate.setEnabled(false);
        groupTime.setEnabled(false);

        groupDate.setOnClickListener(this::setDate);
        groupTime.setOnClickListener(this::setTime);

        editGroupButton.setOnClickListener(editGroupListener());
        groupAddress.setOnClickListener(v -> launchAddressSearchBar(v));

        this.timePickerDialog = new TimePickerDialog(requireActivity(), (view1, hourOfDay, minute) -> {
            LocalTime time2 = LocalTime.of(hourOfDay, minute);
            group.setDate(LocalDateTime.of(group.getDate().toLocalDate(), time2));
            this.groupTime.setText(time2.toString());
        }, group.getDate().getHour(), group.getDate().getMinute(), true);

        this.datePickerDialog = new DatePickerDialog(requireActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view1, year, month, day) -> {
            LocalDate date2 = LocalDate.of(year, month+1, day);
            group.setDate(LocalDateTime.of(date2, group.getDate().toLocalTime()));
            this.groupDate.setText(date2.toString());
        }, group.getDate().getYear(), group.getDate().getMonth().getValue()-1, group.getDate().getDayOfMonth());
        this.datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FriendsTrackerActivity.class);
            intent.putExtra("GROUP_ID", group.getUuid().toString());
            startActivity(intent);
        });

        addMembersButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();

            FriendSelectionGroupFragment friendSelectionGroupFragment = new FriendSelectionGroupFragment(user, friendsRepository, group, repository);
            fragmentManager.beginTransaction().replace(((ViewGroup) requireView().getParent()).getId(), friendSelectionGroupFragment)
                    .addToBackStack(null)
                    .commit();

        });

        userRepository.loadUser(group.getOwnerId()).thenAccept(user -> {
            if(user.getFullName() != null && user.getFullName().length() > 0) {
                if(user.getUsername() != null && user.getUsername().length() > 0) {
                    groupOwner.setText(user.getFullName() + " (@" + user.getUsername() + ")");
                }
                else {
                    groupOwner.setText(user.getFullName());
                }
            }
            else {
                if(user.getUsername() != null && user.getUsername().length() > 0) {
                    groupOwner.setText("@" + user.getUsername());
                }
                else {
                    groupOwner.setText(user.getEmail());
                }
            }
        });

        List<String> uuids = new ArrayList<>(group.getMembers());
        CompletableFuture<List<User>> users = userRepository.loadMultipleUsers(uuids);
        users.thenAccept(users1 -> {
            adapter = new UserListAdapter(getContext(), users1, this::showUserPublicProfile);
            recycler.setAdapter(adapter);
        });


        deleteGroupButton.setOnClickListener(deleteGroupListener());

    }

    private void setTime(View view){
        timePickerDialog.show();
    }

    private void setDate(View view){
        this.datePickerDialog.show();
    }

    private void showUserPublicProfile(User user) {

        FragmentManager fragmentManager = getParentFragmentManager();

        if(user.getUuid().equals(this.user.getUuid())) {
            UserProfileTabFragment userProfileTabFragment = new UserProfileTabFragment();
            fragmentManager.beginTransaction().replace(((ViewGroup) requireView().getParent()).getId(), userProfileTabFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else {
            PublicProfileFragment publicProfileFragment = new PublicProfileFragment(user);
            fragmentManager.beginTransaction().replace(((ViewGroup) requireView().getParent()).getId(), publicProfileFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private View.OnClickListener deleteGroupListener() {

        return v -> {

            List<String> ownedGroups = new ArrayList<>(user.getOwnedGroupsIds());
            ownedGroups.remove(groupID);
            user.setOwnedGroupsIds(ownedGroups);

            userRepository.loadMultipleUsers(group.getMembers()).thenAccept(users -> {
                CompletableFuture<Boolean> allChanged = user.saveUserToDB();
                for(int i = 0; i < users.size(); ++i) {

                    User thisUser = users.get(i);

                    if(!thisUser.getUuid().equals(group.getOwnerId())) {

                        List<String> participatingGroups = new ArrayList<>(thisUser.getParticipatingGroupsIds());
                        participatingGroups.remove(groupID);

                        allChanged = Helpers.combine(allChanged, userRepository.updateParticipatingGroups(thisUser.getUuid(), participatingGroups));

                    }
                }

                allChanged.thenAccept(aBoolean -> {
                    repository.deleteGroup(group);

                    FragmentManager fragmentManager = getParentFragmentManager();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    fragmentManager.popBackStack();
                    startActivity(intent);
                });

            });

        };
    }

    private View.OnClickListener editGroupListener() {
        return v -> {
            editGroupButton.setIcon(requireActivity().getDrawable(R.drawable.ic_outline_check_24));
            editGroupButton.setOnClickListener(this::saveChanges);

            groupName.setEnabled(true);
            groupName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_edit_24, 0, 0, 0);

            groupDescription.setEnabled(true);
            groupDescription.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_edit_24, 0, 0, 0);

            groupAddress.setEnabled(true);
            groupAddress.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_edit_24, 0, 0, 0);

            groupTime.setEnabled(true);
            groupTime.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_edit_24, 0, 0, 0);

            groupDate.setEnabled(true);
            groupDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_edit_24, 0, 0, 0);

        };
    }

    public void launchAddressSearchBar(View view){
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        Autocomplete.IntentBuilder intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields);

        if(user.getLocation() != null){
            LatLng northEast = new LatLng(min(this.userLocation.getLatitude() + 1, 180), min(this.userLocation.getLongitude() + 1, 180));
            LatLng southWest = new LatLng(max(this.userLocation.getLatitude() - 1, -180), max(this.userLocation.getLongitude() - 1, -180));

            intent.setLocationBias(RectangularBounds.newInstance(northEast, southWest));

        }
        searchBarLauncher.launch(intent.build(getActivity()));
    }

    private void updateGroupLocation(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(result.getData());
            String meetingPointAddress = place.getName() + ", " + place.getAddress();
            this.meetingPointLocation = new GeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
            this.groupAddress.setText(meetingPointAddress);
            group.setAddress(meetingPointAddress);
        }
    }

    private void failurePopUp(String title, String message) {
        new AlertDialog.Builder(requireActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Retry", ((dialog, which) -> dialog.cancel()))
                .show();
    }

    private void saveChanges(View view) {
        group.setName(groupName.getText().toString());
        group.setDescription(groupDescription.getText().toString());
        group.setLocation(meetingPointLocation);

        repository.storeGroup(group).thenAccept(aBoolean -> {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Changes successfully saved", LENGTH_SHORT).show();
            groupName.setEnabled(false);
            groupDescription.setEnabled(false);
            groupAddress.setEnabled(false);
            groupDate.setEnabled(false);
            groupTime.setEnabled(false);

            groupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            groupDescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            groupAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            groupDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            groupTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            editGroupButton.setIcon(requireActivity().getDrawable(R.drawable.ic_outline_edit_24));
            editGroupButton.setOnClickListener(editGroupListener());

        }).exceptionally(exception -> {
            failurePopUp("Error", "Fail to save group changes.\n Please disconnect, reconnect and try again" + exception.getMessage());
            return null;
        });
    }


}
