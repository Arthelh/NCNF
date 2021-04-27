package com.ncnf.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Nullable;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@Module
@InstallIn(ActivityComponent.class)
public class CurrentUserModule {

    private static User currentUser;

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

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        currentUser = null;
    }

    public static FirebaseUser currentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
