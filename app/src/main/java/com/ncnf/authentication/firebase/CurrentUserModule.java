package com.ncnf.authentication.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ncnf.models.User;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@Module
@InstallIn(ActivityComponent.class)
public class CurrentUserModule {

    private static User currentUser;

    /**
     * Get the current User
     */
    @Provides
    public static User getCurrentUser() {
        if (currentUser == null) {
            FirebaseUser user = currentUser();
            if (user != null) {
                currentUser = new User();
            } else {
                return null;
            }
        }
        return currentUser;
    }

    /**
     * Sign out the current user
     */
    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        currentUser = null;
    }

    /**
     * Get the current FirebaseUser
     */
    public static FirebaseUser currentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
