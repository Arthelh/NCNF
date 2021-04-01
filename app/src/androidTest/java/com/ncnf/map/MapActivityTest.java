package com.ncnf.map;

import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ncnf.R;
import com.ncnf.settings.SettingsActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
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
public final class MapActivityTest {

    List<Event> TEST_EVENTS = Collections.singletonList(new Event("Math Conference, EPFL, 1pm March 3rd", 46.5191f, 6.5668f));
    List<Venue> TEST_VENUES = Arrays.asList(new Venue("EPFL", 46.5191f, 6.5668f),
            new Venue("UniL", 46.5211f, 6.5802f));

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(MapActivity.class));

    @BindValue
    public EventProvider eventProvider = Mockito.mock(EventProvider.class);
    @BindValue
    public VenueProvider venueProvider = Mockito.mock(VenueProvider.class);

    @Before
    public void setup() {
        Mockito.when(eventProvider.getAll()).thenReturn(TEST_EVENTS);
        Mockito.when(venueProvider.getAll()).thenReturn(TEST_VENUES);
    }

    @Test
    public void test_settings(){
        Intents.init();

        onView(withId(R.id.map_settings_button)).perform(click());
        Intents.intended(hasComponent(SettingsActivity.class.getName()));

        Intents.release();
    }

    @Test
    public final void switchMarkersUpdatesMap() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        assertNotNull("Map with events is loaded",
                device.wait(Until.hasObject(By.desc("MAP_WITH_EVENTS")), 10000)
        );
        // Events are shown
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Math"));
        assertTrue("Events markers exist", marker.waitForExists(10000));

        onView(withId(R.id.map_switch_button)).perform(click());

        assertNotNull("Map with venues is loaded",
                device.wait(Until.hasObject(By.desc("MAP_WITH_VENUES")), 10000)
        );
        // Venues are shown
        marker = device.findObject(new UiSelector().descriptionContains("EPFL"));
        assertTrue("Venue markers exist", marker.waitForExists(10000));

        Mockito.verify(eventProvider).getAll();
        Mockito.verify(venueProvider).getAll();
    }

    @Test
    public final void testUpdateMarkers(){
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        assertNotNull("Map with events is loaded",
                device.wait(Until.hasObject(By.desc("MAP_WITH_EVENTS")), 10000)
        );
        // Events are shown
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Math"));
        assertTrue("Events markers exist", marker.waitForExists(10000));

        onView(withId(R.id.map_settings_button)).perform(click());
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
        marker = device.findObject(new UiSelector().descriptionContains("Math"));
        assertFalse("Events markers exist", marker.waitForExists(10000));

        onView(withId(R.id.map_switch_button)).perform(click());

        marker = device.findObject(new UiSelector().descriptionContains("EPFL"));
        assertFalse("Venue markers exist", marker.waitForExists(10000));
        marker = device.findObject(new UiSelector().descriptionContains("UniL"));
        assertTrue("Venue markers exist", marker.waitForExists(10000));

        onView(withId(R.id.map_settings_button)).perform(click());
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
        onView(withId(R.id.searchBarMap)).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                assertTrue(((MaterialSearchBar) view).isSuggestionsVisible());
            }
        });
    }
}
