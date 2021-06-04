package com.ncnf.repositories;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Event;
import com.ncnf.models.User;

import org.junit.Before;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserRepositoryTests {


    private final FirebaseDatabase mockDatabase = Mockito.mock(FirebaseDatabase.class);
    private final UserRepository userRepository  = new UserRepository(mockDatabase);

    private List<Event> events;

    @Before
    public void setup() {

    }

    


}
