package com.ncnf.friends.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ncnf.R;
import com.ncnf.Utils;
import com.ncnf.user.PrivateUser;
import com.ncnf.user.UserAdapter;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddFriendFragment extends Fragment {

    private final List<PrivateUser> users = new LinkedList<>();
    private RecyclerView recycler;
    private UserAdapter adapter;
    private CollectionReference usersRef;
    private MaterialSearchBar materialSearchBar;

    @Inject
    public FirebaseFirestore databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersRef = databaseReference.collection(Utils.USERS_COLLECTION_KEY);

        //Handle recyclerView
        recycler = getView().findViewById(R.id.add_friend_recycler_view);
        recycler.hasFixedSize();

        materialSearchBar = getView().findViewById(R.id.add_friend_search_bar);

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) { }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                searchUserWithName(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //Define behaviour for navigation button
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.closeSearch();
                }
            }
        });
    }

    //Search the database for a user with the given name
    private void searchUserWithName(String name){

        //The query responsible for the results
        Query firestoreSearchQuery = usersRef
                .orderBy("first_name") //TODO change to "username" when available
                .startAt(name)
                .endAt(name + "\uf8ff");


        FirestoreRecyclerOptions<PrivateUser> options
                = new FirestoreRecyclerOptions.Builder<PrivateUser>()
                .setQuery(firestoreSearchQuery, new SnapshotParser<PrivateUser>() {
                    //Create a new Profile to show from the retrieved information from the db
                    @NonNull
                    @Override
                    public PrivateUser parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        return new PrivateUser((String)snapshot.get(Utils.UUID_KEY), (String)snapshot.get(Utils.EMAIL_KEY));
                    }
                })
                .build();

        adapter = new UserAdapter(options, new UserAdapter.OnItemClickListener() {
            //Custom method to display profile when clicking on it
            @Override
            public void onItemClick(PrivateUser user) {
                displayUser(user);
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        //TODO find better way to listen to optimize resources
        adapter.startListening();
        recycler.setAdapter(adapter);
    }


    //TODO decide how and what to display
    private void displayUser(PrivateUser user){
        Toast.makeText(getActivity(), "TEST_PROFILE_DISPLAY", Toast.LENGTH_LONG).show();
    }

}