package com.ncnf.views.fragments.group;

import android.provider.ContactsContract;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.authentication.firebase.CurrentUserModule;
import com.ncnf.database.firebase.DatabaseService;
import com.ncnf.models.Group;
import com.ncnf.models.SocialObject;
import com.ncnf.models.User;
import com.ncnf.views.activities.group.FriendsTrackerActivity;
import com.ncnf.views.activities.group.GroupActivity;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.ncnf.utilities.StringCodes.EVENTS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.FULL_NAME_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@UninstallModules(CurrentUserModule.class)
@HiltAndroidTest
public class GroupFragmentTest {

    static private final User user1 = Mockito.mock(User.class);
    static private final DatabaseService database1 = Mockito.mock(DatabaseService.class);

    static private UUID gUuid = UUID.randomUUID();
    static private final Group g = new Group("u1",gUuid, "Group Test", LocalDateTime.now(), new GeoPoint(-0.03, -0.03), "random address", SocialObject.Type.OTHER, new ArrayList<>(), "description here", new ArrayList<>());

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @InjectMocks
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(GroupActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public User user = user1;

    @BindValue
    public DatabaseService dbs = database1;

    @BeforeClass
    static public void injects() {
        when(user1.loadUserFromDB()).thenReturn(CompletableFuture.completedFuture(user1));
        ArrayList<Group> l = new ArrayList<>();
        l.add(g);
        when(user1.getParticipatingGroups()).thenReturn(CompletableFuture.completedFuture(l));

        when(user1.getParticipatingGroup(gUuid.toString())).thenReturn(CompletableFuture.completedFuture(g));

        when(database1.getField(USERS_COLLECTION_KEY + "u1", FULL_NAME_KEY)).thenReturn(CompletableFuture.completedFuture("John"));

    }

    @Before
    public void setup() {

        Intents.init();
    }


    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void recyclerViewIsDisplayed(){
        onView(withId(R.id.group_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void correctTextIsDisplayed(){
        onView(withId(R.id.group_name)).check(matches(withText("Group Test")));
        onView(withId(R.id.group_num)).check(matches(withText("1 participant")));
    }

    @Test
    public void groupFragmentOpens(){
        onView(withId(R.id.group_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.group_display_name)).check(matches(withText("Group Test")));
        onView(withId(R.id.group_display_description)).check(matches(withText("description here")));
        onView(withId(R.id.group_attendees_view)).check(matches(isDisplayed()));
    }

    @Test
    public void groupFragmentCloses(){
        onView(withId(R.id.group_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.group_display_name)).check(matches(withText("Group Test")));
        onView(withId(R.id.group_attendees_view)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withId(R.id.group_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void mapActivityOpens(){
        onView(withId(R.id.group_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.group_display_name)).check(matches(withText("Group Test")));
        onView(withId(R.id.open_map_button)).perform(click());
        Intents.intended(hasComponent(FriendsTrackerActivity.class.getName()));
    }

}
