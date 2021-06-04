package com.ncnf.views.activities.bookmark;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.authentication.firebase.CurrentUserModule;
import com.ncnf.authentication.firebase.FirebaseUserModule;
import com.ncnf.models.Event;
import com.ncnf.models.Group;
import com.ncnf.models.Organization;
import com.ncnf.models.User;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;

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
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules({CurrentUserModule.class, FirebaseUserModule.class})
public class BookMarkActivityTest {

    private final Organization o1 = new Organization(UUID.randomUUID(), "EPFL", new GeoPoint(0, 0), "Ecublens", "ncnf@epfl.ch", "08008008080", Collections.singletonList("u1"), new ArrayList<>());
    private final List<Organization> organizations = Collections.singletonList(o1);
    List<Event> events = new ArrayList<>();
    private final Event event = new Event("EPFL", "EPFL event", LocalDateTime.of(2021, 03, 11, 0, 0), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", Event.Type.Conference, 0, 0, "test@email.com");
    List<Group> groups = new ArrayList<>();
    private final Group group = new Group("EPFL", "EPFL event", LocalDateTime.of(2021, 03, 11, 0, 0), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here");

    @BindValue
    public User user = Mockito.mock(User.class);

    @BindValue
    public FirebaseUser firebaseUser = Mockito.mock(FirebaseUser.class);

    @BindValue
    public OrganizationRepository organizationRepository = Mockito.mock(OrganizationRepository.class);

    @BindValue
    FirebaseCacheFileStore fileStore = Mockito.mock(FirebaseCacheFileStore.class);

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(BookMarkActivity.class));

    @Before
    public void setup(){
        for(int i = 0; i < 8; ++i){
            events.add(event);
            groups.add(group);
        }

        when(firebaseUser.getUid()).thenReturn("u1");
        when(organizationRepository.getByUUID(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));
        when(user.getSavedEvents()).thenReturn(CompletableFuture.completedFuture(events));
        when(user.getParticipatingGroups()).thenReturn(CompletableFuture.completedFuture(groups));
    }

    @Test
    public void eventFormValidatesEmptyInput() throws InterruptedException {
        onView(withId(R.id.bookmark_view_pager)).perform(swipeLeft());
        onView(withId(R.id.bookmark_view_pager)).perform(swipeRight());
        Thread.sleep(3000);
        onView(withId(R.id.bookmark_view_pager)).perform(click());
        onView(withId(R.id.EventPage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}
