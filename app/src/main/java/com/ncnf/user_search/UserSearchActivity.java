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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ncnf.R;
import com.ncnf.user.Profile.Profile;
import com.ncnf.user.Profile.ProfileAdapter;
import com.ncnf.user.Profile.PublicProfile;

import java.util.LinkedList;
import java.util.List;

//Useful Links

public class UserSearchActivity extends AppCompatActivity {

    private final List<Profile> profilesList = new LinkedList<>();
    private RecyclerView recycler;
    private FirebaseFirestore databaseReference;
    private ProfileAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        //Init DB Reference
        databaseReference = FirebaseFirestore.getInstance();


        //Handle recyclerView
        recycler = (RecyclerView)findViewById(R.id.user_search_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        //Handle searches
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String name = intent.getStringExtra(SearchManager.QUERY);
            searchUserWithName(name);
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

    //TODO
    private void searchUserWithName(String name){
        Toast.makeText(UserSearchActivity.this, "HELLO",Toast.LENGTH_LONG).show();

        Query firestoreSearchQuery = databaseReference
                .collection("chats");
               //.startAt(name).endAt(name+ "\uf8ff");

        FirestoreRecyclerOptions<Profile> options
                = new FirestoreRecyclerOptions.Builder<Profile>()
                .setQuery(firestoreSearchQuery, new SnapshotParser<Profile>() {
                    @NonNull
                    @Override
                    public Profile parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        return new PublicProfile("TestName", "testEmail@email");
                    }
                })
                .build();

        adapter = new ProfileAdapter(options, new ProfileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Profile profile) {
                displayProfile(profile);
            }

        });
        //adapter.startListening();
        recycler.setAdapter(adapter);

    }

    private void displayProfile(Profile profile) {
        Toast.makeText(this, "TEST_PROFILE_DISPLAY", Toast.LENGTH_LONG).show();
    }

    @Override protected void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

}
