package com.ncnf.socialObject;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
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
public class SocialObjectActivityTest {

<<<<<<< HEAD:app/src/androidTest/java/com/ncnf/event/EventActivityTest.java
    @BindValue
    public DatabaseService db = Mockito.mock(DatabaseService.class);

    private final Event event = new PublicEvent("EPFL", "EPFL event", new Date(2021, 03, 11), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "Event description goes here", Event.Type.Conference, 0, 0, "test@email.com");
    private final CompletableFuture<Event> eventC = new CompletableFuture<>();
=======
    private EventDB db = new EventDB();
    private SocialObject socialObject1 = db.getSocialObj(EventDB.uuid1.toString());
>>>>>>> main:app/src/androidTest/java/com/ncnf/socialObject/SocialObjectActivityTest.java

    @Before
    public void setup(){
        eventC.complete(event);
        when(db.getDocument(anyString(), eq(Event.class))).thenReturn(eventC);
        //doReturn(eventC).when(db).getDocument(anyString(), eq(Event.class));
        Intents.init();
    }

    @Test
    public void test_name(){
<<<<<<< HEAD:app/src/androidTest/java/com/ncnf/event/EventActivityTest.java
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventName)).check(matches(withText(containsString(event.getName()))));
=======
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SocialObjActivity.class);
        intent.putExtra(UUID_KEY, EventDB.uuid1.toString());
        try (ActivityScenario<SocialObjActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventName)).check(matches(withText(containsString(socialObject1.getName()))));
>>>>>>> main:app/src/androidTest/java/com/ncnf/socialObject/SocialObjectActivityTest.java
        }
    }

    @Test
    public void test_address(){
<<<<<<< HEAD:app/src/androidTest/java/com/ncnf/event/EventActivityTest.java
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventLocation)).check(matches(withText(containsString(event.getAddress()))));
=======
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SocialObjActivity.class);
        intent.putExtra(UUID_KEY, EventDB.uuid1.toString());
        try (ActivityScenario<SocialObjActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventLocation)).check(matches(withText(containsString(socialObject1.getAddress()))));
>>>>>>> main:app/src/androidTest/java/com/ncnf/socialObject/SocialObjectActivityTest.java
        }
    }

    @Test
    public void test_desc(){
<<<<<<< HEAD:app/src/androidTest/java/com/ncnf/event/EventActivityTest.java
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventDescription)).check(matches(withText(containsString(event.getDescription()))));
=======
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SocialObjActivity.class);
        intent.putExtra(UUID_KEY, EventDB.uuid1.toString());
        try (ActivityScenario<SocialObjActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventDescription)).check(matches(withText(containsString(socialObject1.getDescription()))));
>>>>>>> main:app/src/androidTest/java/com/ncnf/socialObject/SocialObjectActivityTest.java
        }
    }

    @Test
    public void test_date(){
<<<<<<< HEAD:app/src/androidTest/java/com/ncnf/event/EventActivityTest.java
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            DateAdapter adapter = new DateAdapter(event.getDate());
=======
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SocialObjActivity.class);
        intent.putExtra(UUID_KEY, EventDB.uuid1.toString());
        try (ActivityScenario<SocialObjActivity> scenario = ActivityScenario.launch(intent)) {
            DateAdapter adapter = new DateAdapter(socialObject1.getDate());
>>>>>>> main:app/src/androidTest/java/com/ncnf/socialObject/SocialObjectActivityTest.java
            onView(withId(R.id.eventDate)).check(matches(withText(containsString(adapter.toString()))));
        }
    }

    @Test
    public void test_owner(){
<<<<<<< HEAD:app/src/androidTest/java/com/ncnf/event/EventActivityTest.java
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventActivity.class);
        intent.putExtra(UUID_KEY, event.getUuid().toString());
        try (ActivityScenario<EventActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventOwner)).check(matches(withText(containsString(event.getOwnerId()))));
=======
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SocialObjActivity.class);
        intent.putExtra(UUID_KEY, EventDB.uuid1.toString());
        try (ActivityScenario<SocialObjActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.eventOwner)).check(matches(withText(containsString(socialObject1.getOwnerId()))));
>>>>>>> main:app/src/androidTest/java/com/ncnf/socialObject/SocialObjectActivityTest.java
        }
    }

    @After
    public void cleanup(){
        Intents.release();
    }
}