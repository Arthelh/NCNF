package com.ncnf.socialObject;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.database.DatabaseService;
import com.ncnf.main.MainActivity;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@HiltAndroidTest
public class SocialObjectActivityTest {

    static private final DatabaseService db = Mockito.mock(DatabaseService.class);
    static private final Event e1 = new Event("u1", "TestGeo", LocalDateTime.now(), new GeoPoint(46.5338f, 6.5914f), "EPFL", "Math Conference", SocialObject.Type.Conference, 0, 0, "email@test.com");
    static private final List<Event> events = Arrays.asList(e1);

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(MainActivity.class));

    @BindValue
    public DatabaseService databaseService = db;

    @BeforeClass
    static public void injectEvents() {
        CompletableFuture<List<Event>> future = CompletableFuture.completedFuture(events);
        when(db.geoQuery(any(LatLng.class), anyInt(), eq(EVENTS_COLLECTION_KEY), eq(Event.class))).thenReturn(future);
    }

    @Before
    public void setup(){
        onView(withId(R.id.navigation_feed)).perform(click());
        onView(withId(R.id.feed_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void test_name(){
        onView(withId(R.id.eventName)).check(matches(withText(containsString(e1.getName()))));
    }

    @Test
    public void test_address(){
        onView(withId(R.id.eventLocation)).check(matches(withText(containsString(e1.getAddress()))));
    }

    @Test
    public void test_desc(){
        onView(withId(R.id.eventDescription)).check(matches(withText(containsString(e1.getDescription()))));
    }

    @Test
    public void test_owner(){
        onView(withId(R.id.eventOwner)).check(matches(withText(containsString(e1.getEmail()))));
    }
}