package com.ncnf.event;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ncnf.utilities.DateAdapter;
import com.ncnf.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.Utils.UUID_KEY;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventActivityTest {

    private EventDB db = new EventDB();
    private Event event1 = db.getEvent(EventDB.uuid1.toString());

    @Before
    public void setup(){
        Intents.init();
    }

    @Test
    public void test_name(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, EventDB.uuid1.toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventName)).check(matches(withText(containsString(event1.getName()))));
        }
    }

    @Test
    public void test_address(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, EventDB.uuid1.toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventLocation)).check(matches(withText(containsString(event1.getAddress()))));
        }
    }

    @Test
    public void test_desc(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, EventDB.uuid1.toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventDescription)).check(matches(withText(containsString(event1.getDescription()))));
        }
    }

    @Test
    public void test_date(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, EventDB.uuid1.toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            DateAdapter adapter = new DateAdapter(event1.getDate());
            onView(withId(R.id.eventDate)).check(matches(withText(containsString(adapter.toString()))));
        }
    }

    @Test
    public void test_owner(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, EventDB.uuid1.toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventOwner)).check(matches(withText(containsString(event1.getOwnerId()))));
        }
    }

    @After
    public void cleanup(){
        Intents.release();
    }
}