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
import com.ncnf.database.firebase.DatabaseService;
import com.ncnf.models.Group;
import com.ncnf.models.SocialObject;
import com.ncnf.models.User;
import com.ncnf.utilities.user.LocationService;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import static com.ncnf.utilities.StringCodes.FULL_NAME_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.USER_LOCATION_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class FriendsTrackerActivityTest {

    static private final User user1 = Mockito.mock(User.class);
    static private final DatabaseService database1 = Mockito.mock(DatabaseService.class);

    private static final User u1 = new User(database1, "1", "taylor", "john@bar.com","John", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, LocalDate.now(), null);
    private static final List<String> users = Collections.singletonList("1");

    static Intent intent;
    static {
        intent = new Intent(ApplicationProvider.getApplicationContext(), FriendsTrackerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("GROUP_ID", "u1");
        intent.putExtras(bundle);
    }


    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private static GeoPoint p1 = new GeoPoint(0.03, 0.03);


    static private UUID gUuid = UUID.randomUUID();

    static private final Group g = new Group("u1",gUuid, "Group Test", LocalDateTime.now(), new GeoPoint(-0.03, -0.03), "random address", SocialObject.Type.OTHER, new ArrayList<>(), "description here", users);
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();


    @InjectMocks
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(intent);

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

        when(user1.getParticipatingGroup("u1")).thenReturn(CompletableFuture.completedFuture(g));

        when(database1.getField(USERS_COLLECTION_KEY + "u1", FULL_NAME_KEY)).thenReturn(CompletableFuture.completedFuture("John"));
        when(database1.getField(USERS_COLLECTION_KEY + "1", USER_LOCATION_KEY)).thenReturn(CompletableFuture.completedFuture(p1));
        when(database1.getField(USERS_COLLECTION_KEY + "1", FULL_NAME_KEY)).thenReturn(CompletableFuture.completedFuture("Taylor"));

    }

    @Before
    public void setup(){

        Intents.init();

        when(user.getEmail()).thenReturn("john@doe.ch");
        when(user.getFullName()).thenReturn("John");
        when(user.getUuid()).thenReturn("0");

        ArrayList<String> s = new ArrayList<>();
        s.add("0");
        when(user.getFriendsIds()).thenReturn(s);

        when(user.getLoc()).thenReturn(new GeoPoint(0, 0));


        when(dbs.getField(USERS_COLLECTION_KEY + "0", USER_LOCATION_KEY)).thenReturn(CompletableFuture.completedFuture(p1));
        when(dbs.getField(USERS_COLLECTION_KEY + "0", FULL_NAME_KEY)).thenReturn(CompletableFuture.completedFuture("Taylor"));

    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void findsCurrentUser() {

        onView(withId(R.id.find_user_button)).perform(click());
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("John"));

        assertTrue("User marker exists", marker.waitForExists(2000));
    }


    @Test
    public void findsOtherUser() {

        onView(withId(R.id.find_user_button)).perform(click());
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Taylor"));

        assertTrue("User marker exists", marker.waitForExists(30000));
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
