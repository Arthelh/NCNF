package com.ncnf.views.fragments.group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.ncnf.R;
import com.ncnf.adapters.UserListAdapter;
import com.ncnf.models.Group;
import com.ncnf.models.User;
import com.ncnf.repositories.GroupRepository;
import com.ncnf.repositories.UserRepository;
import com.ncnf.utilities.Helpers;
import com.ncnf.views.activities.group.FriendsTrackerActivity;
import com.ncnf.views.activities.main.MainActivity;
import com.ncnf.views.fragments.user.PublicProfileFragment;
import com.ncnf.views.fragments.user.UserProfileTabFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.content.ContentValues.TAG;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class GroupDisplayFragmentOwner extends Fragment {

    @Inject
    public User user;

    @Inject
    public GroupRepository repository;

    @Inject
    public UserRepository userRepository;

    private String groupID;
    private Group group;

    private EditText groupName;
    private TextView groupAddress;
    private EditText groupDescription;
    private TextView groupOwner;

    private RecyclerView recycler;
    private UserListAdapter adapter;

    private Button button;
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

        groupName.setText(group.getName());
        groupDescription.setText(group.getDescription());
        groupAddress.setText(group.getAddress());

        groupName.setEnabled(false);
        groupDescription.setEnabled(false);

        editGroupButton.setOnClickListener(editGroupListener());

        button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FriendsTrackerActivity.class);
            intent.putExtra("GROUP_ID", group.getUuid().toString());
            startActivity(intent);
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

        List<String> uuids = new ArrayList<>(group.getAttendees());
        CompletableFuture<List<User>> users = userRepository.loadMultipleUsers(uuids);
        users.thenAccept(users1 -> {
            adapter = new UserListAdapter(getContext(), users1, this::showUserPublicProfile);
            recycler.setAdapter(adapter);
        });


        deleteGroupButton.setOnClickListener(deleteGroupListener());

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

            userRepository.loadMultipleUsers(group.getAttendees()).thenAccept(users -> {
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

        };
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

        repository.storeGroup(group).thenAccept(aBoolean -> {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Changes successfully saved", LENGTH_SHORT).show();
            groupName.setEnabled(false);
            groupDescription.setEnabled(false);

            groupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            groupDescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


            editGroupButton.setIcon(requireActivity().getDrawable(R.drawable.ic_outline_edit_24));
            editGroupButton.setOnClickListener(editGroupListener());

        }).exceptionally(exception -> {
            failurePopUp("Error", "Fail to save group changes.\n Please disconnect, reconnect and try again" + exception.getMessage());
            return null;
        });
    }


}
