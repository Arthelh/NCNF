package com.ncnf.database;

import com.google.firebase.Timestamp;
import com.ncnf.database.builder.UserBuilder;
import com.ncnf.user.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ncnf.utilities.StringCodes.*;
import static org.junit.Assert.assertEquals;

public class UserBuilderTest {

    private UserBuilder builder = new UserBuilder();
    private DatabaseService db;

    String uuid = "UUID";
    String username = "username";
    String email = "test@test.com";
    String firstName = "Test";
    String lastName = "Test";
    List<String> friends = new ArrayList<>(Collections.singleton("testFriendUUID"));
    List<String> ownedGroups = new ArrayList<>(Collections.singleton("testOwnedUUID"));
    List<String> participatingGroups = new ArrayList<>(Collections.singleton("testGroupUUID"));
    List<String> savedEvents = new ArrayList<>(Collections.singleton("testSavedUUID"));
    Date birthDate = new Date();
    boolean notifications = true;

    @Before
    public void setup() {
        db = Mockito.mock(DatabaseService.class);
    }

    @Test
    public void toObjectWorks(){
        Map<String, Object> data = new HashMap<>();
        data.put(USERNAME_KEY, username);
        data.put(EMAIL_KEY, email);
        data.put(FIRST_NAME_KEY, firstName);
        data.put(LAST_NAME_KEY, lastName);
        data.put(FRIENDS_KEY, friends);
        data.put(OWNED_GROUPS_KEY, ownedGroups);
        data.put(PARTICIPATING_GROUPS_KEY, participatingGroups);
        data.put(SAVED_EVENTS_KEY, savedEvents);
        data.put(BIRTH_DATE_KEY, new Timestamp(birthDate));
        data.put(NOTIFICATIONS_KEY, notifications);

        User user = builder.toObject(uuid, data);

        assertEquals(user.getUuid(), uuid);
        assertEquals(user.getUsername(), username);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getFirstName(), firstName);
        assertEquals(user.getLastName(), lastName);
        assertEquals(user.getFriendsIds().get(0), friends.get(0));
        assertEquals(user.getOwnedGroupsIds().get(0), ownedGroups.get(0));
        assertEquals(user.getParticipatingGroupsIds().get(0), participatingGroups.get(0));
        assertEquals(user.getSavedEventsIds().get(0), savedEvents.get(0));
        assertEquals(user.getBirthDate(), birthDate);
        assertEquals(user.getNotifications(), notifications);
    }

    @Test
    public void toMapWorks(){
        User user = new User(db, uuid, username, email, firstName, lastName, friends, ownedGroups, participatingGroups, savedEvents, notifications, birthDate);

        Map<String, Object> data = builder.toMap(user);

        assertEquals(builder.toObject(uuid, data), user);
    }

}
