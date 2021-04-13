package com.ncnf.event;

import com.ncnf.organizer.Organizer;
import com.ncnf.organizer.PublicOrganizer;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.Location;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.user.PrivateUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import static com.ncnf.Utils.*;


public class PrivateEvent extends Event {

    private final List<String> invited;

    public PrivateEvent(String ownerId, String name, Date date, GeoPoint location, String address, String description, Type type) {
        super(ownerId, name, date, location, address, type, Event.Visibility.PRIVATE, description);
        invited = new ArrayList<>();
    }

    public PrivateEvent(String ownerId, UUID id, String name, Date date, GeoPoint location, String address, Type type, List<String> attendees, String description, List<String> invited) {
        super(ownerId, id, name, date, location, address, type, Event.Visibility.PRIVATE,attendees, description);
        this.invited = invited;
    }

    public void invite(String user) {
        invited.add(user);
    }

    public List<String> getInvited() {
        return invited;
    }

    @Override
    public String toString() {
        String separator = "@";

        StringBuilder builder = new StringBuilder();
        builder.append(getVisibility() + separator + getUuid().toString() + separator + getName() + separator);

        DateAdapter adapter = new DateAdapter(getDate());
        builder.append(adapter.toString() + separator);

        builder.append(getLocation().getLongitude() + " " + getLocation().getLatitude() + " " + getAddress() + separator);
        builder.append(getDescription() + separator);
        builder.append(getType() + separator + getOwnerId());

        return builder.toString();
    }

//    public static PrivateEvent toEvent(String[] arr) {
//
//        String name = arr[2];
//        Date date = DateAdapter.toDate(arr[3]);
//
//        String[] loc = arr[4].split(" ");
//        Location newLoc = new Location(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]), loc[2]);
//        String descr = arr[5];
//        EventType e = EventType.valueOf(arr[6]);
//        PublicOrganizer org = new PublicOrganizer(arr[7]);
//        String str = arr[8];
//
//        PrivateEvent event = new PrivateEvent(name, date, newLoc, descr, e, org, str);
//        event.setUuid(UUID.fromString(arr[1]));
//        return event;
//    }

    @Override
    public boolean equals(Object o) {
        PrivateEvent p = (PrivateEvent) o;
        return p.getUuid().equals(getUuid());
    }

    @Override
    public int compareTo(Object o) {
        PrivateEvent otherEvent = (PrivateEvent) o;
        return getDate().compareTo(otherEvent.getDate());
    }
    public CompletableFuture<DatabaseResponse> store(DatabaseService db){
        String[] fields = {INVITED_KEY};
        Object[] objects = {this.invited};
        return super.store(db, fields, objects);
    }

}
