package com.ncnf.views.fragments.group;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.authentication.firebase.CurrentUserModule;
import com.ncnf.models.Group;
import com.ncnf.models.User;
import com.ncnf.repositories.GroupRepository;
import com.ncnf.repositories.UserRepository;
import com.ncnf.views.activities.group.FriendsTrackerActivity;
import com.ncnf.views.activities.group.GroupActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@UninstallModules(CurrentUserModule.class)
@HiltAndroidTest
public class GroupFragmentOwnerTest {

    static private final User user1 = Mockito.mock(User.class);
    static private final GroupRepository repository1 = Mockito.mock(GroupRepository.class);
    static private final UserRepository repository2 = Mockito.mock(UserRepository.class);

    static private UUID gUuid = UUID.randomUUID();
    static private final Group g = new Group("u1",gUuid, "Group Test", LocalDateTime.now(), new GeoPoint(-0.03, -0.03), "random address", "description here", new ArrayList<>());

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(GroupActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public User user = user1;

    @BindValue
    public GroupRepository repository = repository1;

    @BindValue
    public UserRepository userRepository = repository2;

    @BeforeClass
    static public void injects() {
        when(repository2.loadUser(anyString())).thenReturn(CompletableFuture.completedFuture(user1));
        ArrayList<String> l = new ArrayList<>();
        l.add(gUuid.toString());

        when(user1.loadUserFromDB()).thenReturn(CompletableFuture.completedFuture(user1));

        ArrayList<Group> l2 = new ArrayList<>();
        l2.add(g);

        when(user1.getUuid()).thenReturn("u1");

        when(repository2.loadUser(eq("u1"))).thenReturn(CompletableFuture.completedFuture(user1));

        when(user1.getParticipatingGroupsIds()).thenReturn(l);

        when(repository1.loadMultipleGroups(l)).thenReturn(CompletableFuture.completedFuture(l2));

        when(repository1.loadGroup(gUuid.toString())).thenReturn(CompletableFuture.completedFuture(g));

        when(repository2.getUserFullName(eq("u1"))).thenReturn(CompletableFuture.completedFuture("John"));

    }

    @Test
    public void showsUserEmail() {
        when(user1.getFullName()).thenReturn("");
        when(user1.getUsername()).thenReturn("");
        when(user1.getEmail()).thenReturn("test@test.com");
        onView(withId(R.id.group_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.group_owner_editable)).check(matches(withText("test@test.com")));
    }

    @Test
    public void showsUserFullName() {

        when(user1.getFullName()).thenReturn("John");
        when(user1.getUsername()).thenReturn("");
        when(user1.getEmail()).thenReturn("test@test.com");

        onView(withId(R.id.group_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.group_owner_editable)).check(matches(withText("John")));
    }

    @Test
    public void showsUsername() {

        when(user1.getFullName()).thenReturn("");
        when(user1.getUsername()).thenReturn("johnnn");
        when(user1.getEmail()).thenReturn("test@test.com");

        onView(withId(R.id.group_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.group_owner_editable)).check(matches(withText("@johnnn")));
    }

    @Test
    public void showsNameAndUserName() {
        when(user1.getFullName()).thenReturn("John");
        when(user1.getUsername()).thenReturn("johnnn");
        when(user1.getEmail()).thenReturn("test@test.com");

        onView(withId(R.id.group_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.group_owner_editable)).check(matches(withText("John (@johnnn)")));
    }

    @Test
    public void recyclerViewIsDisplayed(){
        onView(withId(R.id.group_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void correctTextIsDisplayed(){
        onView(withId(R.id.group_name)).check(matches(withText("Group Test")));
        onView(withId(R.id.group_num)).check(matches(withText(containsString("participant"))));
    }

    @Test
    public void groupFragmentOpens(){
        onView(withId(R.id.group_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.group_display_name_editable)).check(matches(withText("Group Test")));
        onView(withId(R.id.group_display_description_editable)).check(matches(withText("description here")));
        onView(withId(R.id.group_address_editable)).check(matches(withText("random address")));
        onView(withId(R.id.group_attendees_view_editable)).check(matches(isDisplayed()));
    }

    @Test
    public void groupFragmentCloses(){
        onView(withId(R.id.group_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.group_display_name_editable)).check(matches(withText("Group Test")));
        onView(withId(R.id.group_attendees_view_editable)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onView(withId(R.id.group_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void mapActivityOpens(){
        Intents.init();

        onView(withId(R.id.group_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.group_display_name_editable)).check(matches(withText("Group Test")));
        onView(withId(R.id.open_map_button_editable)).perform(click());
        Intents.intended(hasComponent(FriendsTrackerActivity.class.getName()));

        Intents.release();
    }

}
