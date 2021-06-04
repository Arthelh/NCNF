package com.ncnf.views.fragments.feed;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.authentication.firebase.FirebaseUserModule;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;
import com.ncnf.repositories.EventRepository;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.views.activities.main.MainActivity;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@HiltAndroidTest
@UninstallModules(FirebaseUserModule.class)
public class FeedFragmentTest {

    static private final Organization o1 = new Organization(UUID.randomUUID(), "EPFL", new GeoPoint(0, 0), "Ecublens", "ncnf@epfl.ch", "08008008080", Collections.singletonList("u1"), new ArrayList<>());
    static private final List<Organization> organizations = Collections.singletonList(o1);
    static private final Event e1 = new Event("u1", "TestGeo", LocalDateTime.now(), new GeoPoint(46.5338f, 6.5914f), "EPFL", "Math Conference", Event.Type.Conference, 0, 0, "email@test.com");
    static private final Event e2 = new Event("u2", "Another Fun event", LocalDateTime.now(), new GeoPoint(46.5338f, 6.5914f), "EPFL", "Math Conference", Event.Type.Conference, 0, 0, "email@test.com");
    static private final List<Event> events = Arrays.asList(e1, e2);

    static private final EventRepository eventRepository = Mockito.mock(EventRepository.class);
    static private final OrganizationRepository organizationRepository = Mockito.mock(OrganizationRepository.class);
    static private final FirebaseUser user = Mockito.mock(FirebaseUser.class);

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(MainActivity.class));

    @BindValue
    public EventRepository mockEventRepo = eventRepository;

    @BindValue
    public OrganizationRepository mockOrganizationRepo = organizationRepository;

    @BindValue
    public FirebaseUser mockUser = user;

    @BeforeClass
    static public void injectEvents() {
        when(eventRepository.getEventsNearBy()).thenReturn(CompletableFuture.completedFuture(events));
        when(user.getUid()).thenReturn("u1");
        when(organizationRepository.getByUUID(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));
    }

    @Before
    public void setup() {
        onView(withId(R.id.navigation_feed)).perform(click());
    }

    @Test
    public void recyclerViewIsDisplayed(){
        onView(withId(R.id.feed_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void eventActivityOpens(){
        onView(withId(R.id.feed_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.eventName)).check(matches(withText(e1.getName())));
    }

    @Test
    public void canSwitchSortingOrder() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Sort by relevance")).perform(click());
        onView(withId(R.id.feed_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.eventName)).check(matches(withText(e1.getName())));
    }

    @Test
    public void eventFragmentCloses(){
        onView(withId(R.id.feed_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.eventName)).check(matches(withText(e1.getName())));
        Espresso.pressBack();
        onView(withId(R.id.feed_recycler_view)).check(matches(isDisplayed()));
    }
}
