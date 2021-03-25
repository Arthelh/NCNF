package com.ncnf.event;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.EventActivity;
import com.ncnf.event.EventType;
import com.ncnf.event.Location;
import com.ncnf.event.PublicEvent;
import com.ncnf.organizer.PublicOrganizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import java.util.Date;

public class EventActivityTest {

    private Event event1 = new PublicEvent("EPFL event", new Date(2021, 03, 11), new Location(46.518689, 6.568067, "Rolex Learning Center, 1015 Ecublens"), "Event description goes here", EventType.Conference, 0, 0, new PublicOrganizer("EPFL"));

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
            onView(withId(R.id.eventLocation)).check(matches(withText(containsString(event1.getLocation().getAddress()))));
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
            onView(withId(R.id.eventOwner)).check(matches(withText(containsString(event1.getOrganizer().getName()))));
        }
    }
}