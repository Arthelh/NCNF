package com.ncnf.repositories;

import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Event;
import com.ncnf.utilities.settings.Settings;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.UUID_KEY;

public class EventRepository {

    private final FirebaseDatabase db;

    public EventRepository() {
        this.db = new FirebaseDatabase();
    }

    @Inject
    public EventRepository(FirebaseDatabase db)  {
        this.db = db;
    }

    /**
     * Loads the Event from Database
     * @param uuid the unique identifier of the Event
     * @return A CompletableFuture wrapping the loaded Event
     */
    public CompletableFuture<Event> loadEvent(String uuid) {
        return db.getDocument(EVENTS_COLLECTION_KEY + uuid, Event.class);
    }

    /**
     * Stores the Event to Database
     * @param event the Event object to store
     * @return A CompletableFuture wrapping a boolean indicating that the request was successful or not
     */
    public CompletableFuture<Boolean> storeEvent(Event event){
        return this.db.setDocument(EVENTS_COLLECTION_KEY + event.getUuid(), event);
    }

    /**
     * Loads multiple Event objects from Database
     * @param uuidList the list of Event unique identifiers to load
     * @return A CompletableFuture wrapping a list containing the loaded Event objects
     */
    public CompletableFuture<List<Event>> loadMultipleEvents(List<String> uuidList){
        return this.db.whereIn(EVENTS_COLLECTION_KEY, UUID_KEY, uuidList, Event.class);
    }

    /**
     * Returns all events that are happening in a radius set in the settings around the current user position
     * @return A CompletableFuture wrapping a list containing the nearby Event objects
     */
    public CompletableFuture<List<Event>> getEventsNearBy(){
        return db.geoQuery(Settings.getUserPosition(), Settings.getCurrentMaxDistance() * 1000, EVENTS_COLLECTION_KEY, Event.class);
    }

}
