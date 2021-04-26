
package com.ncnf.event;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import static com.ncnf.Utils.*;

public class EventBuilder {

    private DatabaseService db;

    public EventBuilder(){
        this.db = new DatabaseService();
    }

    public EventBuilder(DatabaseService db){
        if(db == null){
            throw new IllegalArgumentException("Database is null");
        }
        this.db = db;
    }

    public CompletableFuture<Event> build(String eventId){

        CompletableFuture<Event> event = db.getDocument(EVENTS_COLLECTION_KEY + eventId, Event.class);
        return event;
    }

    private Event build(Map<String, Object> data){
        try{
            String ownerId = data.get(OWNER_KEY).toString();
            String uuidStr = data.get(UUID_KEY).toString();
            UUID uuid = UUID.fromString(uuidStr);
            String name = (String) data.get(NAME_KEY);
            Date date = ((Timestamp) data.get(DATE_KEY)).toDate();
            GeoPoint location = (GeoPoint) data.get(LOCATION_KEY);
            String address = data.get(ADDRESS_KEY).toString();
            String visibilityStr = data.get(VISIBILITY_KEY).toString();
            Event.Visibility visibility = Event.Visibility.valueOf(visibilityStr);
            String typeStr = data.get(TYPE_KEY).toString();
            Event.Type type = Event.Type.valueOf(typeStr);
            List<String> attendees = (ArrayList) data.get(ATTENDEES_KEY);
            String description = (String) data.get(DESCRIPTION_KEY);
            String email = (String) data.get(EMAIL_KEY);

            if(visibility.equals(Event.Visibility.PUBLIC)){
                int minAge = (int) data.get(MIN_AGE_KEY);
                int price = (int) data.get(PRICE_KEY);
                List<Tag> tags = (ArrayList) data.get(TAGS_LIST_KEY);
                //TODO : should serialize / deserialize tags before adding them
                return new PublicEvent(ownerId, uuid, name, date, location, address, description, type, attendees, minAge, price, tags, email);

            } else {
                List<String> invited = (ArrayList) data.get(INVITED_KEY);
                return new PrivateEvent(ownerId, uuid, name, date, location, address, type, attendees, description, invited);
            }
        } catch (Exception e){
            return null;
        }
    }

    public Event eventFromSnapshot(DocumentSnapshot documentSnapshot){
        return this.build(documentSnapshot.getData());
    }
}