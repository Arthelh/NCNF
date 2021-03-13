package com.example.bootcamp.map;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.example.bootcamp.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@HiltAndroidTest
public final class MapActivityTest {

    List<Event> TEST_EVENTS = Collections.singletonList(new Event("Math Conference, EPFL, 1pm March 3rd", 46.5191f, 6.5668f));
    List<Venue> TEST_VENUES = Collections.singletonList(new Venue("EPFL", 46.5191f, 6.5668f));

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

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

        hiltRule.inject();
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
}
