package com.ncnf.event;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.database.DatabaseService;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.Utils.EVENTS_COLLECTION_KEY;
import static com.ncnf.Utils.UUID_KEY;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
@RunWith(AndroidJUnit4.class)
public class EventActivityTest {

    @BindValue
    public DatabaseService db = Mockito.mock(DatabaseService.class);

    private final Event event = new PublicEvent("EPFL", "EPFL event", new Date(2021, 03, 11), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "Event description goes here", Event.Type.Conference, 0, 0, "test@email.com");
    private final CompletableFuture<Event> eventC = new CompletableFuture<>();

    @Before
    public void setup(){
        eventC.complete(event);
        when(db.getDocument(anyString(), eq(Event.class))).thenReturn(eventC);
        //doReturn(eventC).when(db).getDocument(anyString(), eq(Event.class));
        Intents.init();
    }

    @Test
    public void test_name(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventName)).check(matches(withText(containsString(event.getName()))));
        }
    }

    @Test
    public void test_address(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventLocation)).check(matches(withText(containsString(event.getAddress()))));
        }
    }

    @Test
    public void test_desc(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventDescription)).check(matches(withText(containsString(event.getDescription()))));
        }
    }

    @Test
    public void test_date(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            DateAdapter adapter = new DateAdapter(event.getDate());
            onView(withId(R.id.eventDate)).check(matches(withText(containsString(adapter.toString()))));
        }
    }

    @Test
    public void test_owner(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventOwner)).check(matches(withText(containsString(event.getOwnerId()))));
        }
    }

    @After
    public void cleanup(){
        Intents.release();
    }
}