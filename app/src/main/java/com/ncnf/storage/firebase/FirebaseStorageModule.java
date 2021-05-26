package com.ncnf.storage.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

/**
 * Hilt module for Firebase Storage
 */
@Module
@InstallIn(ActivityComponent.class)
public class FirebaseStorageModule {

    /**
     * Inject a Firebase Storage
     * @return a Firebase Storage instance
     */
    @Provides
    public static FirebaseStorage getFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

}
