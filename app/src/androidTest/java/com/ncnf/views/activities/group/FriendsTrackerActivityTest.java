package com.ncnf.views.activities.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;

import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ServiceTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.authentication.firebase.CurrentUserModule;
import com.ncnf.models.Group;
import com.ncnf.models.User;
import com.ncnf.repositories.GroupRepository;
import com.ncnf.repositories.UserRepository;
import com.ncnf.utilities.user.LocationService;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class FriendsTrackerActivityTest {

    static private final User user1 = Mockito.mock(User.class);
    static private final User user2 = Mockito.mock(User.class);
    static private final GroupRepository groupRepository1 = Mockito.mock(GroupRepository.class);
    static private final UserRepository userRepository1 = Mockito.mock(UserRepository.class);

    private static final List<String> users = Collections.singletonList("1");
    static private UUID gUuid = UUID.randomUUID();
    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), FriendsTrackerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", gUuid.toString());
        intent.putExtras(bundle);
    }


    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private static GeoPoint p1 = new GeoPoint(0.03, 0.03);


    static private final Group g = Mockito.mock(Group.class);
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();


    @InjectMocks
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(intent);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public User user = user1;

    @BindValue
    public GroupRepository groupRepository = groupRepository1;

    @BindValue
    public UserRepository userRepository = userRepository1;


    @BeforeClass
    static public void injects() {
        when(user1.loadUserFromDB()).thenReturn(CompletableFuture.completedFuture(user1));
        when(user1.getLocation()).thenReturn(new GeoPoint(0, 0));

        when(user1.getUuid()).thenReturn("0");
        when(user2.getUuid()).thenReturn("1");
        ArrayList<Group> l = new ArrayList<>();
        l.add(g);

        ArrayList<String> s = new ArrayList<>();
        s.add("1");
        s.add("0");
        when(g.getMembers()).thenReturn(s);

        when(groupRepository1.loadGroup(gUuid.toString())).thenReturn(CompletableFuture.completedFuture(g));
       // when(user1.getParticipatingGroupsIds().contains("0")).thenReturn(true);

        when(userRepository1.getUserFullName(eq("0"))).thenReturn(CompletableFuture.completedFuture("John"));
        when(userRepository1.getUserPosition(eq("0"))).thenReturn(CompletableFuture.completedFuture(new GeoPoint(0, 0)));

        when(userRepository1.getUserFullName(eq("1"))).thenReturn(CompletableFuture.completedFuture("Taylor"));
        when(userRepository1.getUserPosition(eq("1"))).thenReturn(CompletableFuture.completedFuture(p1));
    }

    @Before
    public void setup(){

        Intents.init();

        when(user.getEmail()).thenReturn("john@doe.ch");
        when(user.getFullName()).thenReturn("John");

        when(user.getLocation()).thenReturn(new GeoPoint(0, 0));

    }

    @After
    public void cleanup(){
        Intents.release();
    }

    //@Ignore
    @Test
    public void findsCurrentUser() {

        onView(withId(R.id.find_user_button)).perform(click());
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("This is me"));

        assertTrue("User marker exists", marker.waitForExists(5000));
    }

    //@Ignore
    @Test
    public void findsOtherUser() {

        onView(withId(R.id.find_user_button)).perform(click());
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Taylor"));

        assertTrue("User marker exists", marker.waitForExists(10000));
    }

    @Test
    public void testWithBoundService() throws TimeoutException {
        // Create the service Intent.
        Intent serviceIntent =
                new Intent(InstrumentationRegistry.getTargetContext(), LocationService.class);

        Looper.prepare();



        // Bind the service and grab a reference to the binder.
        mServiceRule.startService(serviceIntent);
        IBinder binder = mServiceRule.bindService(serviceIntent);

        // Get the reference to the service, or you can call public methods on the binder directly.
        LocationService service = ((LocationService.LocationServiceBinder) binder).getService();

        // Verify that the service is working correctly.
        assertThat(service.onStartCommand(null, 0, 0), is(2));
    }

}
