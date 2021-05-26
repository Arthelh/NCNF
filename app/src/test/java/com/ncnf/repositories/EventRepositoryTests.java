package com.ncnf.repositories;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Event;
import com.ncnf.models.SocialObject;
import com.ncnf.models.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


import static com.ncnf.utilities.StringCodes.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventRepositoryTests {

    private final static String uuid = "my_uuid";
    private final static String other_uuid = "other_uuid";

    private final FirebaseDatabase mockDatabase = Mockito.mock(FirebaseDatabase.class);
    private final EventRepository eventRepository = new EventRepository(mockDatabase);

    private List<Event> events;

    @Before
    public void setup() {
        Event event1 = new Event("ownerId1", "name1", LocalDateTime.now(),  new GeoPoint(0., 0.), "address", "description", SocialObject.Type.OTHER, 0, 0, "test@email.com");
        Event event2 = new Event("ownerId2", "name2", LocalDateTime.now(),  new GeoPoint(0., 0.), "address", "description", SocialObject.Type.OTHER, 0, 0, "test@email.com");
        events = Arrays.asList(event1, event2);
    }

    @Test
    public void loadEventWorks(){
        when(mockDatabase.getDocument(anyString(), eq(Event.class))).thenReturn(CompletableFuture.completedFuture(events.get(0)));

        CompletableFuture<Event> event = eventRepository.loadEvent("eventUUID");

        try {
            assertThat(event.get(), is(events.get(0)));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }


    @Test
    public void getEventsNearbyWorks(){
        when(mockDatabase.geoQuery(any(), anyDouble(), anyString(), eq(Event.class))).thenReturn(CompletableFuture.completedFuture(events));

        CompletableFuture<List<Event>> eventList = eventRepository.getEventsNearBy();

        try {
            assertThat(eventList.get(), is(events));
        } catch (ExecutionException | InterruptedException e) {
            Assert.fail("The future did not complete correctly !");
        }
    }

}
