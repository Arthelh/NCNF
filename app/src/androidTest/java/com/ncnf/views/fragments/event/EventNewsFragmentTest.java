package com.ncnf.views.fragments.event;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.authentication.firebase.FirebaseUserModule;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;
import com.ncnf.repositories.EventRepository;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.views.activities.organization.OrganizationProfileActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules({FirebaseUserModule.class})
public class EventNewsFragmentTest {

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private ActivityScenarioRule<OrganizationProfileActivity> scenarioRule = new ActivityScenarioRule<>(OrganizationProfileActivity.class);

    private final Organization o1 = new Organization(UUID.randomUUID(), "EPFL", new GeoPoint(0, 0), "Ecublens", "ncnf@epfl.ch", "08008008080", Collections.singletonList("u1"), new ArrayList<>());
    private final List<Organization> organizations = Collections.singletonList(o1);
    private final Event e1 = new Event("u1", "Concert", LocalDateTime.now(), new GeoPoint(46.5338f, 6.5914f), "EPFL", "Rock concert", Event.Type.OTHER, 0, 0, "email@test.com");
    private final List<Event> events = Collections.singletonList(e1);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenarioRule);

    @BindValue
    public FirebaseUser user = Mockito.mock(FirebaseUser.class);

    @BindValue
    public OrganizationRepository organizationRepository = Mockito.mock(OrganizationRepository.class);

    @BindValue
    EventRepository eventRepository = Mockito.mock(EventRepository.class);

    @Before
    public void setup() throws InterruptedException {
        when(organizationRepository.getByUUID(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));
        when(organizationRepository.getOrganizationEvents(anyString())).thenReturn(CompletableFuture.completedFuture(events));
        when(organizationRepository.loadOrganization(anyString())).thenReturn(CompletableFuture.completedFuture(o1));
        when(user.getUid()).thenReturn("u1");
        when(user.getEmail()).thenReturn("johnny@bar.com");

        onView(withId(R.id.organization_view_pager)).perform(swipeLeft());
        Thread.sleep(2000); // necessary because of the swipe
        onView(withText("Concert")).perform(click());
        onView(withId(R.id.button_publish_event_news)).perform(scrollTo(), click());
    }
    /*

    @Test
    public void publishWithoutFillingText() {
        onView(withId(R.id.event_news_publish_button)).perform(click());

        onView(withId(R.id.event_news_field)).check(matches(hasErrorText("This field cannot be empty")));
    }

    @Test
    public void successfullyPublishNews() {
        when(eventRepository.addNews(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        onView(withId(R.id.event_news_field)).perform(typeText("The event starts soon !"), closeSoftKeyboard());
        onView(withId(R.id.event_news_publish_button)).perform(click());

        verify(eventRepository).addNews(e1.getUuid().toString(), "The event starts soon !");
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("News published !")));
    }

     */

}
