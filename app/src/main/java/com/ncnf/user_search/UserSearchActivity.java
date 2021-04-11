package com.ncnf.user_search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ncnf.R;
import com.ncnf.user.Profile.Profile;
import com.ncnf.user.Profile.ProfileAdapter;
import com.ncnf.user.Profile.PublicProfile;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

//Useful Links: https://github.com/firebase/FirebaseUI-Android/tree/master/firestore
@AndroidEntryPoint
public class UserSearchActivity extends AppCompatActivity {

    private final List<Profile> profilesList = new LinkedList<>();
    private RecyclerView recycler;
    private ProfileAdapter adapter;
    @Inject
    public FirebaseFirestore databaseReference;
    @Inject
    public CollectionReference usersRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        //Init DB Reference
        databaseReference = FirebaseFirestore.getInstance();
        usersRef = databaseReference.collection("users");


        //Handle recyclerView
        recycler = (RecyclerView)findViewById(R.id.user_search_recycler_view);
        recycler.hasFixedSize();
        //Handle searches
        handleIntent(getIntent());
    }


    //Needed for whenever a new search is made
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    //Handles the search
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchWord = intent.getStringExtra(SearchManager.QUERY);
            searchUserWithName(searchWord);
        }
    }

    //Handle search bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.user_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    //Search the database for a user with the given name
    private void searchUserWithName(String name){

        //The query responsible for the results
        Query firestoreSearchQuery = usersRef
                .orderBy("first_name") //TODO change to "username" when available
                .startAt(name)
                .endAt(name+ "\uf8ff");

        FirestoreRecyclerOptions<Profile> options
                = new FirestoreRecyclerOptions.Builder<Profile>()
                .setQuery(firestoreSearchQuery, new SnapshotParser<Profile>() {
                    //Create a new Profile to show from the retrieved information from the db
                    @NonNull
                    @Override
                    public Profile parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        return new PublicProfile((String)snapshot.get("first_name"), (String)snapshot.get("email"));
                    }
                })
                .build();

        adapter = new ProfileAdapter(options, new ProfileAdapter.OnItemClickListener() {
            //Custom method to display profile when clicking on it
            @Override
            public void onItemClick(Profile profile) {
                displayProfile(profile);
            }
        });

        recycler.setLayoutManager(new LinearLayoutManager(this));
        //TODO find better way to listen to optimize resources
        adapter.startListening();
        recycler.setAdapter(adapter);
    }

    //TODO decide how and what to display
    private void displayProfile(Profile profile) {
        Toast.makeText(this, "TEST_PROFILE_DISPLAY", Toast.LENGTH_LONG).show();
    }

   /* @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }*/

}
