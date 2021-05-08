package com.ncnf.map;

import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.google.firebase.firestore.GeoPoint;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ncnf.R;
import com.ncnf.main.MainActivity;
import com.ncnf.settings.ui.SettingsActivity;
import com.ncnf.socialObject.Event;
import com.ncnf.socialObject.EventDB;
import com.ncnf.socialObject.SocialObject;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@HiltAndroidTest
public final class MapFragmentTest {

    List<SocialObject> TEST_SocialObjects = Collections.singletonList(new Event("lol", "Carmen", new Date(), new GeoPoint(46.5338f, 6.5914f), "EPFL", "Math Conference", SocialObject.Type.Conference, 0, 0, "email@test.com"));
    List<Venue> TEST_VENUES = Arrays.asList(new Venue("EPFL", 46.5191f, 6.5668f),
            new Venue("UniL", 46.5211f, 6.5802f));

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(activityRule);

    @BindValue
    public EventDB eventDB = Mockito.mock(EventDB.class);
    @BindValue
    public VenueProvider venueProvider = Mockito.mock(VenueProvider.class);

    @Before
    public void setup() {
        Intents.init();
        Mockito.when(eventDB.toList()).thenReturn(TEST_SocialObjects);
        Mockito.when(venueProvider.getAll()).thenReturn(TEST_VENUES);
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
                device.wait(Until.hasObject(By.desc("MAP_WITH_EVENTS")), 1000)
        );
        // Events are shown
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Carmen"));
        assertTrue("Events markers exist", marker.waitForExists(1000));

        onView(withId(R.id.map_switch_button)).perform(click());

        assertNotNull("Map with venues is loaded",
                device.wait(Until.hasObject(By.desc("MAP_WITH_VENUES")), 1000)
        );
        // Venues are shown
        marker = device.findObject(new UiSelector().descriptionContains("UniL"));
        assertTrue("Venue markers exist", marker.waitForExists(10000));

        onView(withId(R.id.map_switch_button)).perform(click());

        assertNotNull("Map with venues is loaded",
                device.wait(Until.hasObject(By.desc("MAP_WITH_VENUES")), 10000)
        );

        //Mockito.verify(eventDB).toList();
        Mockito.verify(venueProvider).getAll();
    }

    @Test
    public final void testUpdateMarkers(){
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        assertNotNull("Map with events is loaded",
                device.wait(Until.hasObject(By.desc("MAP_WITH_EVENTS")), 1000)
        );
        // Events are shown
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Carmen"));
        assertTrue("Events markers exist", marker.waitForExists(1000));

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

        marker = device.findObject(new UiSelector().descriptionContains("Carmen"));
        assertFalse("Events markers exist", marker.waitForExists(1000));

        onView(withId(R.id.map_switch_button)).perform(click());

        marker = device.findObject(new UiSelector().descriptionContains("EPFL"));
        assertFalse("Venue markers exist", marker.waitForExists(1000));
        marker = device.findObject(new UiSelector().descriptionContains("UniL"));
        assertTrue("Venue markers exist", marker.waitForExists(1000));

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
}
