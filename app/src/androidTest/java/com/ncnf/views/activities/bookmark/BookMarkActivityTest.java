package com.ncnf.views.activities.bookmark;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.authentication.firebase.CurrentUserModule;
import com.ncnf.authentication.firebase.FirebaseUserModule;
import com.ncnf.models.Event;
import com.ncnf.models.User;
import com.ncnf.repositories.UserRepository;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(FirebaseUserModule.class)
public class BookMarkActivityTest {

    private static final List<Event> events = new ArrayList<>();
    private static final Event event = new Event("EPFL", "EPFL event", LocalDateTime.of(2021, 03, 11, 0, 0), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", Event.Type.Conference, 0, 0, "test@email.com");

    private static User user = Mockito.mock(User.class);

    @BindValue
    public static FirebaseUser firebaseUser = Mockito.mock(FirebaseUser.class);

    @BindValue
    public static UserRepository userRepository = Mockito.mock(UserRepository.class);

    @BindValue
    FirebaseCacheFileStore fileStore = Mockito.mock(FirebaseCacheFileStore.class);

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(BookMarkActivity.class));

    @BeforeClass
    public static void setup(){
        for(int i = 0; i < 8; ++i){
            events.add(event);
        }

        when(firebaseUser.getUid()).thenReturn("u1");
        when(userRepository.loadUser(anyString())).thenReturn(CompletableFuture.completedFuture(user));
        when(user.getSavedEvents()).thenReturn(CompletableFuture.completedFuture(events));
    }

    @Test
    public void eventFormValidatesEmptyInput(){
        onView(withId(R.id.fragmentBookmarkContainer)).perform(click());
        onView(withId(R.id.EventPage)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}
