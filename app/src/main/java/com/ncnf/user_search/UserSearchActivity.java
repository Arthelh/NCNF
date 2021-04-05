package com.ncnf.user_search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.user.Profile.Profile;
import com.ncnf.user.Profile.ProfileAdapter;

import java.util.LinkedList;
import java.util.List;


public class UserSearchActivity extends AppCompatActivity {

    private final List<Profile> profilesList = new LinkedList<>();;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);


        //Handle recyclerView
        RecyclerView recycler = (RecyclerView)findViewById(R.id.user_search_recycler_view);

        ProfileAdapter adapter = new ProfileAdapter(profilesList, new ProfileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Profile item) {
                //TODO
            }
        });
        recycler.setAdapter(adapter);
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
            String query = intent.getStringExtra(SearchManager.QUERY);
           searchUserWithName(query);
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
    void searchUserWithName(String query){
        Toast.makeText(UserSearchActivity.this, "HELLO",Toast.LENGTH_LONG).show();
    }

}
