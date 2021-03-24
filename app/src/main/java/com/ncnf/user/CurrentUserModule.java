package com.ncnf.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

import static com.ncnf.Utils.USERs_COLLECTION_KEY;

@Module
@InstallIn(ActivityComponent.class)
public class CurrentUserModule {

    private static PrivateUser currentUser;

    @Provides
    public static PrivateUser getCurrentUser() {
        if (currentUser == null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                currentUser = new PrivateUser(user, USERs_COLLECTION_KEY + user.getUid());
            } else {
                return null;
            }
        }
        return currentUser;
    }

}
