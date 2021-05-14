package com.ncnf.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ncnf.R;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.storage.CacheFileStore;
import com.ncnf.storage.FileStore;
import com.ncnf.user.User;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.graphics.BitmapFactory.decodeFile;
import static android.graphics.BitmapFactory.decodeResource;
import static com.ncnf.utilities.StringCodes.USER_IMAGE_PATH;

@AndroidEntryPoint
public class PublicProfileFragment extends Fragment {

    @Inject
    public CacheFileStore fileStore;

    private User user;

    public PublicProfileFragment(User user){
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_profile, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Change with event UUID
        ImageView imageView = getView().findViewById(R.id.profile_picture);
        fileStore.setContext(this.getContext());
        fileStore.setPath(USER_IMAGE_PATH, user.getUuid() + ".jpg");
        fileStore.downloadImage(imageView, decodeResource(this.getContext().getResources(), R.drawable.default_profile_picture));

        TextView name = getView().findViewById(R.id.public_profile_name);
        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));

        TextView username = getView().findViewById(R.id.public_profile_username);
        username.setText(user.getUsername());

        TextView email = getView().findViewById(R.id.profile_email_placeholder);
        email.setText(user.getEmail());

        TextView birthDate = getView().findViewById(R.id.profile_birth_date_placeholder);
        birthDate.setText(user.getBirthDate().toString());
    }
}