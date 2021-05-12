package com.ncnf.user;

import android.provider.ContactsContract;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.database.DatabaseService;
import com.ncnf.main.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.ncnf.utilities.StringCodes.FIRST_NAME_KEY;
import static com.ncnf.utilities.StringCodes.USERS_COLLECTION_KEY;
import static com.ncnf.utilities.StringCodes.USER_LOCATION_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class FriendsTrackerActivityTest {


    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private GeoPoint p1;

    @InjectMocks
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(FriendsTrackerActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public User user = mock(User.class);

    @BindValue
    public DatabaseService dbs = mock(DatabaseService.class);

    @Before
    public void setup(){
        Intents.init();

        when(user.loadUserFromDB()).thenReturn(CompletableFuture.completedFuture(user));
        when(user.getEmail()).thenReturn("john@doe.ch");
        when(user.getFirstName()).thenReturn("John");
        when(user.getUuid()).thenReturn("0");

        ArrayList<String> s = new ArrayList<>();
        s.add("0");
        when(user.getFriendsIds()).thenReturn(s);

        p1 = new GeoPoint(0.03, 0.03);


        when(dbs.getField(USERS_COLLECTION_KEY + "0", USER_LOCATION_KEY)).thenReturn(CompletableFuture.completedFuture(p1));
        when(dbs.getField(USERS_COLLECTION_KEY + "0", FIRST_NAME_KEY)).thenReturn(CompletableFuture.completedFuture("Taylor"));

    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void findsCurrentUser() {
        when(user.getLoc()).thenReturn(new GeoPoint(0, 0));

        onView(withId(R.id.find_user_button)).perform(click());
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("John"));

        assertTrue("User marker exists", marker.waitForExists(2000));
    }

    @Test
    public void findsOtherUser() {
        when(user.getLoc()).thenReturn(new GeoPoint(0, 0));

        onView(withId(R.id.find_user_button)).perform(click());
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Taylor"));

        assertTrue("User marker exists", marker.waitForExists(10000));
    }

}
