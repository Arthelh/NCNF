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
import com.ncnf.R;
import com.ncnf.utilities.StringCodes;
import com.ncnf.user.User;
import com.ncnf.user.UserAdapter;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FriendsFragment extends Fragment {

    private RecyclerView recycler;
    private UserAdapter adapter;
    private CollectionReference usersRef;

    @Inject
    public FirebaseFirestore databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersRef = databaseReference.collection(StringCodes.USERS_COLLECTION_KEY);

        //Handle recyclerView
        recycler = getView().findViewById(R.id.friends_recycler_view);
        recycler.hasFixedSize();

        //The query responsible for the results
        Query firestoreSearchQuery = usersRef
                .orderBy("first_name"); //TODO change to "username" when available
                //.startAt(name)
                //.endAt(name + "\uf8ff");


        FirestoreRecyclerOptions<User> options
                = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(firestoreSearchQuery, new SnapshotParser<User>() {
                    //Create a new Profile to show from the retrieved information from the db
                    @NonNull
                    @Override
                    public User parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        return new User(snapshot);
                    }
                })
                .build();

        adapter = new UserAdapter(options, new UserAdapter.OnItemClickListener() {
            //Custom method to display profile when clicking on it
            @Override
            public void onItemClick(User user) {
                displayUser(user);
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        //TODO find better way to listen to optimize resources
        adapter.startListening();
        recycler.setAdapter(adapter);
    }

    private void displayUser(User user){
        Toast.makeText(getActivity(), "TEST_PROFILE_DISPLAY", Toast.LENGTH_LONG).show();
    }
}