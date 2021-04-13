package com.ncnf.event;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.organizer.PublicOrganizer;

import org.junit.Test;

import java.util.Date;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

public class EventActivityTest {

    private final Event event1 = new PublicEvent("EPFL", "EPFL event", new Date(2021, 03, 11), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "Event description goes here", Event.Type.Conference, 0, 0);
    @Test
    public void test_name(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra("EVENT_NUM", 0);
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(ViewMatchers.withId(R.id.eventName)).check(matches(withText(containsString(event1.getName()))));
        }
    }

    @Test
    public void test_address(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra("EVENT_NUM", 0);
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventLocation)).check(matches(withText(containsString(event1.getAddress()))));
        }
    }

    @Test
    public void test_desc(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra("EVENT_NUM", 0);
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventDescription)).check(matches(withText(containsString(event1.getDescription()))));
        }
    }

    @Test
    public void test_date(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra("EVENT_NUM", 0);
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventDate)).check(matches(withText(containsString(event1.getDate().toString()))));
        }
    }

    @Test
    public void test_owner(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra("EVENT_NUM", 0);
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventOwner)).check(matches(withText(containsString(event1.getOwnerId()))));
        }
    }
}