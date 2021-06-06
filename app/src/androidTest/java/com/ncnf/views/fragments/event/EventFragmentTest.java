package com.ncnf.views.fragments.event;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.authentication.firebase.FirebaseUserModule;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.views.activities.main.MainActivity;

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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(FirebaseUserModule.class)
public class EventFragmentTest {

    static private final FirebaseDatabase db = Mockito.mock(FirebaseDatabase.class);

    private final Organization o1 = new Organization(UUID.randomUUID(), "EPFL", new GeoPoint(0, 0), "Ecublens", "ncnf@epfl.ch", "08008008080", Collections.singletonList("u1"), new ArrayList<>());
    private final List<Organization> organizations = Collections.singletonList(o1);
    static private final Event e1 = new Event("u1", "TestGeo", LocalDateTime.now(), new GeoPoint(46.5338f, 6.5914f), "EPFL", "Math Conference", Event.Type.Conference, 0, 0, "email@test.com");
    static private final List<Event> events = Arrays.asList(e1);

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(MainActivity.class));

    @BindValue
    public FirebaseDatabase firebaseDatabase = db;

    @BindValue
    public FirebaseUser user = Mockito.mock(FirebaseUser.class);

    @BindValue
    public OrganizationRepository organizationRepository = Mockito.mock(OrganizationRepository.class);

    @BeforeClass
    static public void injectEvents() {
        CompletableFuture<List<Event>> future = CompletableFuture.completedFuture(events);
        when(db.geoQuery(any(LatLng.class), anyInt(), eq(EVENTS_COLLECTION_KEY), eq(Event.class))).thenReturn(future);
    }

    public void setup(){
        when(user.getUid()).thenReturn("u1");
        when(organizationRepository.getByUUID(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));
        when(organizationRepository.loadOrganization(anyString())).thenReturn(CompletableFuture.completedFuture(o1));


        onView(withId(R.id.navigation_feed)).perform(click());
        onView(withId(R.id.feed_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void test_name(){
        setup();
        onView(withId(R.id.eventName)).check(matches(withText(containsString(e1.getName()))));
    }

    @Test
    public void test_address(){
        setup();
        onView(withId(R.id.eventAddress)).check(matches(withText(containsString(e1.getAddress()))));
    }

    @Test
    public void test_desc(){
        setup();
        onView(withId(R.id.eventDescription)).check(matches(withText(containsString(e1.getDescription()))));
    }

    @Test
    public void test_owner(){
        setup();
        onView(withId(R.id.eventOrganization)).check(matches(withText(containsString(o1.getName()))));
    }
}