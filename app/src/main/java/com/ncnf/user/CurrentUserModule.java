package com.ncnf.user;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ncnf.database.DatabaseService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

import static com.ncnf.Utils.USERS_COLLECTION_KEY;

@Module
@InstallIn(ActivityComponent.class)
public class CurrentUserModule {

    private static PrivateUser currentUser;

    @Provides
    public static PrivateUser getCurrentUser() {
        if (currentUser == null) {
            FirebaseUser user = currentUser();
            if (user != null) {
                currentUser = new PrivateUser(new DatabaseService(), USERS_COLLECTION_KEY + user.getUid(), user.getUid(), user.getEmail());
            } else {
                return null;
            }
        }
        return currentUser;
    }

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
        currentUser().delete();
        currentUser = null;
    }

    public static FirebaseUser currentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

}
