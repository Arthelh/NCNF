package com.ncnf.database.builder;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.database.DatabaseService;
import com.ncnf.user.User;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ncnf.Utils.BIRTH_DATE_KEY;
import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.EMAIL_KEY;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.FRIENDS_KEY;
import static com.ncnf.Utils.LAST_NAME_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_KEY;
import static com.ncnf.Utils.OWNED_EVENTS_KEY;
import static com.ncnf.Utils.SAVED_EVENTS_KEY;
import static com.ncnf.Utils.USERNAME_KEY;
import static com.ncnf.Utils.UUID_KEY;

public class UserBuilder extends DatabaseObjectBuilder<User> {

    @Override
    public User toObject(String uuid, Map<String, Object> data) {

        String username = (String) data.get(USERNAME_KEY);
        String email = (String) data.get(EMAIL_KEY);
        String firstName = (String) data.get(FIRST_NAME_KEY);
        String lastName = (String) data.get(LAST_NAME_KEY);
        List<String> friends = (List<String>) data.get(FRIENDS_KEY);
        List<String> ownedEvents = (List<String>) data.get(OWNED_EVENTS_KEY);
        List<String> savedEvents = (List<String>) data.get(SAVED_EVENTS_KEY);
        Log.d(DEBUG_TAG, data.get(BIRTH_DATE_KEY).toString());

        //Date birthDate = ((Timestamp) data.get(BIRTH_DATE_KEY)).toDate();
        Date birthDate = Date.from(Instant.now());
        boolean notifications = (boolean) data.get(NOTIFICATIONS_KEY);

        return new User(new DatabaseService(), uuid, username, email, firstName, lastName, friends, ownedEvents, savedEvents, birthDate, notifications, null);
    }

    @Override
    public Map<String, Object> toMap(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put(UUID_KEY, user.getUuid());
        data.put(USERNAME_KEY, user.getUsername());
        data.put(EMAIL_KEY, user.getEmail());
        data.put(LAST_NAME_KEY, user.getLastName());
        data.put(FIRST_NAME_KEY, user.getFirstName());
        Timestamp toSave = Timestamp.now();
        if(user.getBirthDate() != null){
            toSave = new Timestamp(user.getBirthDate());
        }
        data.put(BIRTH_DATE_KEY, toSave);

        data.put(FRIENDS_KEY, user.getFriendsIds());
        data.put(OWNED_EVENTS_KEY, user.getOwnedEventsIds());
        data.put(SAVED_EVENTS_KEY, user.getSavedEventsIds());
        data.put(NOTIFICATIONS_KEY, user.getNotifications());

        return data;
    }
}
