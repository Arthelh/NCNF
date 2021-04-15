package com.ncnf.friends.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.feed.ui.EventAdapter;
import com.ncnf.user.PrivateUser;
import com.ncnf.user.Profile.Profile;
import com.ncnf.user.Profile.ProfileAdapter;
import com.ncnf.user.UserAdapter;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

public class FriendsFragment extends Fragment {

    private final List<PrivateUser> profilesList = new LinkedList<>();
    private RecyclerView recycler;
    private ProfileAdapter adapter;
    private CollectionReference usersRef;

    @Inject
    public FirebaseFirestore databaseReference;

    @Inject
    public PrivateUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public UserViewHolder(View v) {
            super(v);
        }

        public void bind(final Event e, final EventAdapter.OnEventListener listener) {
            itemView.setOnClickListener(v -> listener.onEventClick(e));
        }
    }
}