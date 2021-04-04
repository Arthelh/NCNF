package com.ncnf;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ncnf.event.Event;
import com.ncnf.event.EventActivity;
import com.ncnf.event.EventDB;
import com.ncnf.event.EventType;
import com.ncnf.feed.ui.FeedFragment;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.Location;
import com.ncnf.event.PublicEvent;
import com.ncnf.organizer.PublicOrganizer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class EventActivityTest {

    private EventDB db = new EventDB();
    private Event event1 = db.getEvent(EventDB.uuid1.toString());

    @Test
    public void test_name(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra("event_uid", EventDB.uuid1.toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventName)).check(matches(withText(containsString(event1.getName()))));
        }
    }

    @Test
    public void test_address(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra("event_uid", EventDB.uuid1.toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventLocation)).check(matches(withText(containsString(event1.getLocation().getAddress()))));
        }
    }

    @Test
    public void test_desc(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra("event_uid", EventDB.uuid1.toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventDescription)).check(matches(withText(containsString(event1.getDescription()))));
        }
    }

    @Test
    public void test_date(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra("event_uid", EventDB.uuid1.toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            DateAdapter adapter = new DateAdapter(event1.getDate());
            onView(withId(R.id.eventDate)).check(matches(withText(containsString(adapter.toString()))));
        }
    }

    @Test
    public void test_owner(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra("event_uid", EventDB.uuid1.toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventOwner)).check(matches(withText(containsString(event1.getOrganizer().getName()))));
        }
    }
}