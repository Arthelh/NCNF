package com.ncnf.repositories;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Group;
import com.ncnf.models.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GroupRepositoryTests {
    private final FirebaseDatabase mockDatabase = Mockito.mock(FirebaseDatabase.class);
    private final GroupRepository groupRepository = new GroupRepository(mockDatabase);

    private List<User> users;
    private final Group group = new Group("userUUID", UUID.randomUUID(), "group name", LocalDateTime.now(), new GeoPoint(0., 0.), "address", "description", new ArrayList<>());

    @Before
    public void setup() {
        User u1 = new User(mockDatabase, "u1", "John", "john@bar.com","John", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, LocalDate.now(), null);
        User u2 = new User(mockDatabase, "u2", "Albert", "albert@bar.com","Albert", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, LocalDate.now(), null);
        users = Arrays.asList(u1, u2);
    }

}
