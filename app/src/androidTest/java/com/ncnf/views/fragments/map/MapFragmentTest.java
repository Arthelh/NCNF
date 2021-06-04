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

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ncnf.R;
import com.ncnf.authentication.firebase.FirebaseUserModule;
import com.ncnf.models.Organization;
import com.ncnf.repositories.EventRepository;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.views.activities.main.MainActivity;
import com.ncnf.views.activities.settings.SettingsActivity;
import com.ncnf.models.Event;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(FirebaseUserModule.class)
public final class MapFragmentTest {

    static private final EventRepository mockEventRepository = Mockito.mock(EventRepository.class);
    static private final OrganizationRepository mockOrgRepo = Mockito.mock(OrganizationRepository.class);
    static private final FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);

    static private final Event e1 = new Event("u1", "TestGeo", LocalDateTime.now(), new GeoPoint(46.5338f, 6.5914f), "EPFL", "Math Conference", Event.Type.Conference, 0, 0, "email@test.com");
    static private final Event e2 = new Event("u2", "Another Fun event", LocalDateTime.now(), new GeoPoint(46.5338f, 6.5914f), "EPFL", "Math Conference", Event.Type.Conference, 0, 0, "email@test.com");
    static private final List<Event> events = Arrays.asList(e1, e2);

    static private final Organization o1 = new Organization("EPFL", new GeoPoint(46.5338f, 6.5914f), "EPFL Route Cantonale 15", "epfl@epfl.ch", "021 123 45 67", "EPFL");
    static private final Organization o2 = new Organization("UniL", new GeoPoint(46.5211f, 6.5802f), "Route de l'UniL 1", "unil@unil.ch", "021 765 43 21", "UniL");
    static private final List<Organization> organizations = Arrays.asList(o1, o2);

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(activityRule);

    @BindValue
    public EventRepository eventRepository = mockEventRepository;

    @BindValue
    public OrganizationRepository organizationRepository = mockOrgRepo;

    @BindValue
    public FirebaseUser user = mockUser;

    @BeforeClass
    static public void injectEvents() {
        CompletableFuture<List<Event>> future = CompletableFuture.completedFuture(events);
        when(mockEventRepository.getEventsNearBy()).thenReturn(future);

        CompletableFuture<List<Organization>> orgFuture = CompletableFuture.completedFuture(organizations);
        when(mockOrgRepo.getOrganizationsNearby()).thenReturn(orgFuture);

        when(mockUser.getUid()).thenReturn("u1");
        when(mockOrgRepo.getByUUID(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));
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
        assertTrue("Organization markers exist", marker.waitForExists(5000));

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
        assertFalse("Organization markers exist", marker.waitForExists(5000));
        marker = device.findObject(new UiSelector().descriptionContains("UniL"));
        assertTrue("Organization markers exist", marker.waitForExists(5000));

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
            Thread.sleep(10000);
        } catch (UiObjectNotFoundException | InterruptedException e) {
            Assert.fail("Marker not found.");
        }

        onView(withId(R.id.map_feed_container)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.set_event_name), withText("TestGeo"))).perform(click());

        Espresso.pressBack();

        onView(withId(R.id.map_switch_button)).check(matches(withText(containsString("Switch"))));

    }

    @Ignore
    @Test
    public final void testOrgInfoWindow(){
        onView(withId(R.id.map_switch_button)).perform(click());

        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiObject marker = device.findObject(new UiSelector().descriptionContains(o1.getName()));
        try {
            marker.click();
            Rect markerRect = marker.getBounds();
            int x = markerRect.centerX();
            int y = markerRect.centerY() - (3 * markerRect.height() / 4);
            device.click(x, y);
            Thread.sleep(10000);
        } catch (UiObjectNotFoundException | InterruptedException e) {
            Assert.fail("Marker not found.");
        }

        onView(withId(R.id.organization_display_name)).check(matches(withText(containsString(o1.getName()))));

        Espresso.pressBack();

        onView(withId(R.id.map_switch_button)).check(matches(withText(containsString("Switch"))));
    }
}
