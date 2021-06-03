package com.ncnf.views.activities.group;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.snapshot.BooleanNode;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.models.Group;
import com.ncnf.models.User;
import com.ncnf.repositories.FriendsRepository;
import com.ncnf.repositories.GroupRepository;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;
import com.ncnf.views.activities.main.MainActivity;
import com.ncnf.views.fragments.group.FriendSelectionGroupFragment;
import com.ncnf.views.fragments.group.GroupEditingFragment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;
import static com.ncnf.utilities.StringCodes.DEBUG_TAG;
import static com.ncnf.utilities.StringCodes.GROUPS_COLLECTION_KEY;

@AndroidEntryPoint
public class GroupCreationActivity extends AppCompatActivity {

    @Inject
    public User user;

    @Inject
    public GroupRepository groupRepository;

    @Inject
    public FirebaseCacheFileStore fileStore;


    final FragmentManager fragmentManager = getSupportFragmentManager();
    private GroupEditingFragment groupEditionFragment;
    private Fragment friendSelectorFragment;

    private MaterialButton nextStepButton;
    private MaterialButton closeButton;
    private MaterialButton createButton;
    private boolean firstStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);


        if(savedInstanceState == null){
            this.groupEditionFragment = new GroupEditingFragment();
            this.friendSelectorFragment = new FriendSelectionGroupFragment();
        }

        fragmentManager.beginTransaction().add(R.id.group_creation_fragment_container, this.friendSelectorFragment).hide(this.friendSelectorFragment).commit();
        fragmentManager.beginTransaction().add(R.id.group_creation_fragment_container, this.groupEditionFragment).hide(this.groupEditionFragment).commit();

        fragmentManager.beginTransaction().show(this.groupEditionFragment).commit();

        nextStepButton = findViewById(R.id.next_step_group_creation_button);
        closeButton = findViewById(R.id.close_group_creation_button);
        createButton = findViewById(R.id.create_group_button);
        createButton.setVisibility(View.INVISIBLE);

        nextStepButton.setOnClickListener(this::switchViews);
        closeButton.setOnClickListener(this::closeAndGoHome);
        createButton.setOnClickListener(this::createGroup);

        firstStep = true;
    }

    private void createGroup(View view) {
        List<String> members = ((FriendSelectionGroupFragment)this.friendSelectorFragment).getMembersIds();
        if(members.size() == 1){
            Snackbar.make(findViewById(android.R.id.content), "You can't create a group with nobody in it.", LENGTH_SHORT).show();
        } else {
            LocalTime time = this.groupEditionFragment.getGroupTime();
            LocalDate date = this.groupEditionFragment.getGroupDate();
            GeoPoint meetingPoint = this.groupEditionFragment.getMeetingPointLocation();
            String name = this.groupEditionFragment.getGroupName();
            String address = this.groupEditionFragment.getMeetingPointAddress();
            String description = this.groupEditionFragment.getGroupDescription();
            Group group = new Group(this.user.getUuid(), name, LocalDateTime.of(date, time), meetingPoint, address, description);
            for(int i = 0; i < members.size(); ++i){
                group.addMember(members.get(i));
            }
            CompletableFuture<Boolean> groupStored = groupRepository.storeGroup(group);
            CompletableFuture<Boolean> imageStored;
            if(this.groupEditionFragment.getPictureBitMap() != null){
                fileStore.setContext(this);
                fileStore.setPath(GROUPS_COLLECTION_KEY, group.getUuid().toString());
                imageStored = fileStore.uploadImage(this.groupEditionFragment.getPictureBitMap());
            } else {
                imageStored = CompletableFuture.completedFuture(true);
            }
            CompletableFuture<Boolean> ownedStored = this.user.addOwnedGroup(group);
            CompletableFuture<Boolean> participantsStored = this

        }
    }

    private void gotToGroupActivity(){
        finish();
        Intent intent = new Intent(this, GroupActivity.class);
        startActivity(intent);
    }

    private void switchViews(View view) {

        if(firstStep){

            LocalTime time = this.groupEditionFragment.getGroupTime();
            LocalDate date = this.groupEditionFragment.getGroupDate();
            GeoPoint meetingPoint = this.groupEditionFragment.getMeetingPointLocation();
            String name = this.groupEditionFragment.getGroupName();
            boolean isOkay = time != null && date != null && meetingPoint != null && name != null;
            if(!isOkay){
                Snackbar.make(findViewById(android.R.id.content), "Please fill every field (bio is optional).", LENGTH_SHORT).show();
            } else {
                fragmentManager.beginTransaction().hide(this.groupEditionFragment).show(this.friendSelectorFragment).commit();
                nextStepButton.setIcon(getDrawable(R.drawable.ic_baseline_arrow_back_24));
                firstStep = false;
                createButton.setVisibility(View.VISIBLE);
            }
        } else {
            fragmentManager.beginTransaction().hide(this.friendSelectorFragment).show(this.groupEditionFragment).commit();
            nextStepButton.setIcon(getDrawable(R.drawable.ic_baseline_arrow_forward_24));
            firstStep = true;
            createButton.setVisibility(View.INVISIBLE);
        }

    }

    private void closeAndGoHome(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}