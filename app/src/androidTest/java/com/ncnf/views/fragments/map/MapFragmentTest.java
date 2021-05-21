package com.ncnf.views.fragments.map;

import android.graphics.Rect;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ncnf.R;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.repositories.EventRepository;
import com.ncnf.views.activities.main.MainActivity;
import com.ncnf.views.activities.settings.SettingsActivity;
import com.ncnf.models.Event;
import com.ncnf.models.SocialObject;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@HiltAndroidTest
public final class MapFragmentTest {

    static private final EventRepository mockEventRepository = Mockito.mock(EventRepository.class);

    static private final Event e1 = new Event("u1", "TestGeo", LocalDateTime.now(), new GeoPoint(46.5338f, 6.5914f), "EPFL", "Math Conference", SocialObject.Type.Conference, 0, 0, "email@test.com");
    static private final Event e2 = new Event("u2", "Another Fun event", LocalDateTime.now(), new GeoPoint(46.5338f, 6.5914f), "EPFL", "Math Conference", SocialObject.Type.Conference, 0, 0, "email@test.com");
    static private final List<Event> events = Arrays.asList(e1, e2);

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(activityRule);

    @BindValue
    public EventRepository eventRepository = mockEventRepository;

    @BeforeClass
    static public void injectEvents() {
        CompletableFuture<List<Event>> future = CompletableFuture.completedFuture(events);
        when(mockEventRepository.getEventsNearBy()).thenReturn(future);
    }

    @Before
    public void setup() {
        Intents.init();

        onView(withId(R.id.navigation_map)).perform(click());
    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void test_settings() {
        onView(withId(R.id.menu_settings)).perform(click());
        Intents.intended(hasComponent(SettingsActivity.class.getName()));
    }

    @Test
    public final void switchMarkersUpdatesMap() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        assertNotNull("Map with events is loaded",
                device.wait(Until.hasObject(By.desc("MAP_WITH_EVENTS")), 5000)
        );
        // Events are shown
        UiObject marker = device.findObject(new UiSelector().descriptionContains("TestGeo"));
        assertTrue("Events markers exist", marker.waitForExists(5000));

        onView(withId(R.id.map_switch_button)).perform(click());

        assertNotNull("Map with venues is loaded",
                device.wait(Until.hasObject(By.desc("MAP_WITH_VENUES")), 5000)
        );
        // Venues are shown
        marker = device.findObject(new UiSelector().descriptionContains("UniL"));
        assertTrue("Venue markers exist", marker.waitForExists(5000));

        onView(withId(R.id.map_switch_button)).perform(click());

        assertNotNull("Map with venues is loaded",
                device.wait(Until.hasObject(By.desc("MAP_WITH_VENUES")), 5000)
        );
    }

    @Test
    public final void testUpdateMarkers(){
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        assertNotNull("Map with events is loaded",
                device.wait(Until.hasObject(By.desc("MAP_WITH_EVENTS")), 1000)
        );
        // Events are shown
        UiObject marker = device.findObject(new UiSelector().descriptionContains("TestGeo"));
        assertTrue("Events markers exist", marker.waitForExists(5000));

        onView(withId(R.id.menu_settings)).perform(click());
        onView(withId(R.id.distanceSeekBar)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Changing progress of seekbar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(0);
            }
        });
        onView(withId(R.id.validateButton)).perform(click());

        marker = device.findObject(new UiSelector().descriptionContains("TestGeo"));
        assertFalse("Events markers exist", marker.waitForExists(5000));

        onView(withId(R.id.map_switch_button)).perform(click());

        marker = device.findObject(new UiSelector().descriptionContains("EPFL"));
        assertFalse("Venue markers exist", marker.waitForExists(5000));
        marker = device.findObject(new UiSelector().descriptionContains("UniL"));
        assertTrue("Venue markers exist", marker.waitForExists(5000));

        onView(withId(R.id.menu_settings)).perform(click());
        onView(withId(R.id.distanceSeekBar)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Changing progress of seekbar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(24);
            }
        });
        onView(withId(R.id.validateButton)).perform(click());

    }

    @Test
    public final void testSearchBar(){
        onView(withId(R.id.searchBarMap)).perform(click());
        onView(withClassName(containsString(EditText.class.getSimpleName()))).perform(typeText("Boulevard"));
        onView(withId(R.id.searchBarMap)).check((view, noViewFoundException) -> {
            assertTrue(((MaterialSearchBar) view).isSuggestionsEnabled());
            assertTrue(((MaterialSearchBar) view).isSearchOpened());
        });
    }

    @Test
    public final void testInfoWindow(){
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject marker = device.findObject(new UiSelector().descriptionContains("TestGeo"));
        try {
            marker.click();
            Rect markerRect = marker.getBounds();
            int x = markerRect.centerX();
            int y = markerRect.centerY() - markerRect.height();
            device.click(x, y);
            Thread.sleep(2000);
        } catch (UiObjectNotFoundException | InterruptedException e) {
            Assert.fail("Marker not found.");
        }

        onView(allOf(withId(R.id.set_event_name), withText("TestGeo"))).perform(click());

        Espresso.pressBack();

        onView(withId(R.id.map_switch_button)).check(matches(withText(containsString("Switch"))));

    }
}
