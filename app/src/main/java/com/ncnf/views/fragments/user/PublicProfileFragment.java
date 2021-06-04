package com.ncnf.views.fragments.user;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ncnf.R;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;
import com.ncnf.models.User;

import java.time.LocalDate;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.graphics.BitmapFactory.decodeResource;
import static com.ncnf.utilities.StringCodes.USER_IMAGE_PATH;

@AndroidEntryPoint
public class PublicProfileFragment extends Fragment {

    @Inject
    public FirebaseCacheFileStore fileStore;

    private User user;

    public PublicProfileFragment(){}

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

        if (user != null) {
            // Change with event UUID
            ImageView profilePicture = requireView().findViewById(R.id.personal_profile_picture);
            fileStore.setContext(this.getContext());
            fileStore.setPath(USER_IMAGE_PATH, user.getUuid() + ".jpg");
            fileStore.downloadImage(profilePicture, decodeResource(this.getResources(), R.drawable.default_profile_picture));

            TextView email = getView().findViewById(R.id.publicProfileEmail);
            email.setText(user.getEmail());

            TextView fullName = getView().findViewById(R.id.publicProfileFullName);
            fullName.setText(user.getFullName());
            String fullNameStr = user.getFullName();
            if(!fullNameStr.isEmpty()){
                fullName.setText(fullNameStr);
            }

            TextView username = getView().findViewById(R.id.publicProfileUsername);
            username.setText(user.getUsername());
            String usernameStr = user.getUsername();
            if(!usernameStr.isEmpty()){
                SpannableString ss = new SpannableString("@" + usernameStr);
                ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                username.setText(ss);
            }

            TextView birthDate = getView().findViewById(R.id.publicProfileBirthDay);
            LocalDate birthDateObj = user.getBirthDate();
            if(!(birthDateObj == null)){
                birthDate.setText(birthDateObj.toString());
            }

            email.setEnabled(false);
        }
    }
}