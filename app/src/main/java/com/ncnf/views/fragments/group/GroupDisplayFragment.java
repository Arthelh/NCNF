package com.ncnf.views.fragments.group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.adapters.GroupUserRecyclerAdapter;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Group;
import com.ncnf.models.SocialObject;
import com.ncnf.models.User;
import com.ncnf.views.activities.group.FriendsTrackerActivity;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.EMAIL_KEY;
import static com.ncnf.utilities.StringCodes.FULL_NAME_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;

@AndroidEntryPoint
public class GroupDisplayFragment extends Fragment {

    @Inject
    public User user;

    @Inject
    public FirebaseDatabase dbs;

    private String groupID;
    private Group group;

    private TextView groupName;
    private TextView groupDescription;
    private RecyclerView recycler;
    private GroupUserRecyclerAdapter adapter;

    private Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupName = view.findViewById(R.id.group_display_name);
        groupDescription = view.findViewById(R.id.group_display_description);

        recycler = view.findViewById(R.id.group_attendees_view);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        button = view.findViewById(R.id.open_map_button);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            this.groupID = bundle.getString("GROUP_ID");

            // FOR TESTING
            /**
            Group g2 = new Group(user.getUuid(), "Group 2 !", LocalDateTime.now(), new GeoPoint(0,0), "Ecublens", "test group", SocialObject.Type.Movie);
            g2.invite("oFqlaX7uxifmck6AByJ52ZAZqHh1");
            CompletableFuture<Group> thisGroup = CompletableFuture.completedFuture(g2);
             */

            if(user.getParticipatingGroupsIds().contains(groupID)) {

                CompletableFuture<Group> thisGroup = user.getParticipatingGroup(groupID);
                thisGroup.thenAccept(group -> {
                    this.group = group;
                    prepareAllFields();
                });
            }

            else if(user.getOwnedGroupsIds().contains(groupID)) {

                CompletableFuture<Group> thisGroup = user.getOwnedGroup(groupID);
                thisGroup.thenAccept(group -> {
                    this.group = group;
                    prepareAllFields();
                });
            }

        }
    }

    private void prepareAllFields() {
        adapter = new GroupUserRecyclerAdapter(new ArrayList<>());
        recycler.setAdapter(adapter);

        groupName.setText(group.getName());
        groupDescription.setText(group.getDescription());

        getUserName(group.getOwnerId());

        for(int i = 0; i < group.getAttendees().size(); ++i) {
            getUserName(group.getAttendees().get(i));
        }

    }

    private void getUserName(String uuid) {

        button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FriendsTrackerActivity.class);
            intent.putExtra("GROUP_ID", group.getUuid().toString());
            startActivity(intent);
        });

        CompletableFuture <String> getName = dbs.getField(USERS_COLLECTION_KEY + uuid, FULL_NAME_KEY);
        getName.thenAccept(s -> {
            if(s != null && s.length() != 0) {
                adapter.addUser(s);
            }
            else {
                CompletableFuture <String> getEmail = dbs.getField(USERS_COLLECTION_KEY + uuid, EMAIL_KEY);
                getEmail.thenAccept(e -> adapter.addUser(e));
            }
        });

    }

}
