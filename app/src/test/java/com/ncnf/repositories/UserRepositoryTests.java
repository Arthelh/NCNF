package com.ncnf.repositories;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Event;
import com.ncnf.models.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class UserRepositoryTests {


    private final FirebaseDatabase mockDatabase = Mockito.mock(FirebaseDatabase.class);
    private final UserRepository userRepository  = new UserRepository(mockDatabase);
    private final User u1 = new User(this.mockDatabase, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);


    private List<Event> events;

    @Before
    public void setup() {

    }

    @Test
    public void loadUserWorks(){
        when(mockDatabase.getDocument(anyString(), any())).thenReturn(CompletableFuture.completedFuture(u1));

        CompletableFuture<User> userFuture = userRepository.loadUser("uuid");
        try {
            assertEquals(userFuture.get(), u1);
        } catch (Exception e) {
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void storeUserWorks(){
        when(mockDatabase.setDocument(anyString(), any())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> boolFuture = userRepository.storeUser(u1);
        try {
            assertEquals(boolFuture.get(), true);
        } catch (Exception e) {
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void loadMultipleUsersWorks(){
        when(mockDatabase.whereIn(anyString(), anyString(), anyList(), any())).thenReturn(CompletableFuture.completedFuture(Arrays.asList(u1)));

        CompletableFuture<List<User>> listFuture = userRepository.loadMultipleUsers(Arrays.asList(u1.getUuid()));
        try {
            assertEquals(listFuture.get().get(0), u1);
        } catch (Exception e) {
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void updateNotificationsWorks(){
        when(mockDatabase.updateField(anyString(), anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(false));

        CompletableFuture<Boolean> boolFuture = userRepository.updateNotifications("string1", true);
        try{
            assertEquals(boolFuture.get(), false);
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void updateNotificationsTokenWorks(){
        when(mockDatabase.updateField(anyString(), anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> boolFuture = userRepository.updateNotificationsToken("string1", "token");
        try{
            assertEquals(boolFuture.get(), true);
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void updateUserPosition(){
        when(mockDatabase.updateField(anyString(), anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(true));

        CompletableFuture<Boolean> boolFuture = userRepository.updateUserPosition("string1", new GeoPoint(1,1));
        try{
            assertEquals(boolFuture.get(), true);
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getUserPositionWorks(){
        GeoPoint geo = new GeoPoint(1,1);
        when(mockDatabase.getField(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(geo));

        CompletableFuture<GeoPoint> geoFuture = userRepository.getUserPosition("uuid");
        try{
            assertEquals(geoFuture.get(), geo);
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getUserFullNameWorks(){
        when(mockDatabase.getField(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture("fullName"));
        CompletableFuture<String> nameFuture = userRepository.getUserFullName("uuid");
        try{
            assertEquals(nameFuture.get(), "fullName");
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void getUsernameWorks(){
        when(mockDatabase.getField(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture("username"));
        CompletableFuture<String> nameFuture = userRepository.getUserUsername("uuid");
        try{
            assertEquals(nameFuture.get(), "username");
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }

    @Test
    public void addBookmarkEventWorks(){
        when(mockDatabase.updateArrayField(anyString(), anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));
        CompletableFuture<Boolean> eventFuture = userRepository.addBookmarkEventToUser("uuid", "uuid");
        try{
            assertEquals(eventFuture.get(), true);
        } catch (Exception e){
            Assert.fail("Something went wrong with the future");
        }
    }
}
