package com.ncnf.database.builders;

import com.google.firebase.Timestamp;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.User;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import static com.ncnf.utilities.StringCodes.BIRTH_DATE_KEY;
import static com.ncnf.utilities.StringCodes.EMAIL_KEY;
import static com.ncnf.utilities.StringCodes.FULL_NAME_KEY;
import static com.ncnf.utilities.StringCodes.FRIENDS_KEY;
import static com.ncnf.utilities.StringCodes.NOTIFICATIONS_KEY;
import static com.ncnf.utilities.StringCodes.OWNED_GROUPS_KEY;
import static com.ncnf.utilities.StringCodes.PARTICIPATING_GROUPS_KEY;
import static com.ncnf.utilities.StringCodes.SAVED_EVENTS_KEY;
import static com.ncnf.utilities.StringCodes.USERNAME_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;

public class DatabaseUserBuilder extends DatabaseObjectBuilder<User>{

    @Inject
    FirebaseDatabase db;

    @Override
    public User toObject(String uuid, @NotNull Map<String, Object> data) {
        Objects.requireNonNull(data);

        String username = (String) data.getOrDefault(USERNAME_KEY, "");
        String email = (String) data.getOrDefault(EMAIL_KEY, "");
        String full_name = (String) data.getOrDefault(FULL_NAME_KEY, "");
        List<String> friends = (List<String>) data.getOrDefault(FRIENDS_KEY, new ArrayList<>());
        List<String> ownedGroups = (List<String>) data.getOrDefault(OWNED_GROUPS_KEY, new ArrayList<>());
        List<String> participatingGroups = (List<String>) data.getOrDefault(PARTICIPATING_GROUPS_KEY, new ArrayList<>());
        List<String> savedEvents = (List<String>) data.getOrDefault(SAVED_EVENTS_KEY, new ArrayList<>());
        Date firebaseDate = ((Timestamp) data.getOrDefault(BIRTH_DATE_KEY, Timestamp.now())).toDate();
        LocalDate birthDate = LocalDate.ofEpochDay(firebaseDate.getTime()/1000/60/60/24);
        boolean notifications = (boolean) data.getOrDefault(NOTIFICATIONS_KEY, false);
        return new User(db, uuid, username, email, full_name, friends, ownedGroups, participatingGroups, savedEvents, notifications, birthDate, null);
        } 
    

    @Override
    public Map<String, Object> toMap(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put(UUID_KEY, user.getUuid());
        data.put(USERNAME_KEY, user.getUsername());
        data.put(EMAIL_KEY, user.getEmail());
        data.put(FULL_NAME_KEY, user.getFullName());
        Timestamp toSave = Timestamp.now();
        if(user.getBirthDate() != null){
            toSave = new Timestamp(user.getBirthDate().toEpochDay()*24*60*60,0);
        }
        data.put(BIRTH_DATE_KEY, toSave);

        data.put(FRIENDS_KEY, user.getFriendsIds());
        data.put(OWNED_GROUPS_KEY, user.getOwnedGroupsIds());
        data.put(PARTICIPATING_GROUPS_KEY, user.getParticipatingGroupsIds());
        data.put(SAVED_EVENTS_KEY, user.getSavedEventsIds());
        data.put(NOTIFICATIONS_KEY, user.getNotifications());

        return data;
    }
}
