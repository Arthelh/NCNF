package com.ncnf.utilities;

import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

@Module
@InstallIn(ActivityComponent.class)
public class FirestoreModule {

    @Provides
    public static FirebaseFirestore getFirestoreRef(){
        return FirebaseFirestore.getInstance();
    }
}