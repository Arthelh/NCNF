package com.ncnf.repositories;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Group;
import com.ncnf.models.Organization;
import com.ncnf.models.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class GroupRepositoryTests {
    private final FirebaseDatabase firebaseDatabase = Mockito.mock(FirebaseDatabase.class);
    private final GroupRepository groupRepository = new GroupRepository(firebaseDatabase);

    private List<User> users;
    private final Group group = new Group("userUUID", UUID.randomUUID(), "group name", LocalDateTime.now(), new GeoPoint(0., 0.), "address", "description", new ArrayList<>());

    @Before
    public void setup() {
        User u1 = new User(firebaseDatabase, "u1", "John", "john@bar.com","John", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, LocalDate.now(), null);
        User u2 = new User(firebaseDatabase, "u2", "Albert", "albert@bar.com","Albert", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, LocalDate.now(), null);
        users = Arrays.asList(u1, u2);
    }

    @Test
    public void loadGroupWorks(){
        when(firebaseDatabase.getDocument(anyString(), eq(Group.class))).thenReturn(CompletableFuture.completedFuture(group));

        CompletableFuture<Group> ret = groupRepository.loadGroup(group.getUuid().toString());

        try {
            assertThat(ret.get(), is(group));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void storeGroupWorks(){

        when(firebaseDatabase.setDocument(anyString(), any())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> boolFuture = groupRepository.storeGroup(group);
        try {
            assertEquals(boolFuture.get(), true);
        } catch (Exception e) {
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void loadMultipleGroupsWorks(){
        when(firebaseDatabase.whereIn(anyString(), anyString(), anyList(), eq(Group.class))).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(group)));

        CompletableFuture<List<Group>> ret = groupRepository.loadMultipleGroups(Collections.singletonList(group.getUuid().toString()));

        try {
            assertThat(ret.get().get(0), is(group));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

    @Test
    public void deleteGroupWorks(){

        when(firebaseDatabase.deleteDocument(anyString())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> boolFuture = groupRepository.deleteGroup(group);
        try {
            assertEquals(boolFuture.get(), true);
        } catch (Exception e) {
            Assert.fail("Something went wrong with the future");
        }
    }

}
