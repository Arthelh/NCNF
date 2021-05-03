package com.ncnf.user;

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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.LOCATION_KEY;
import static com.ncnf.Utils.USERS_COLLECTION_KEY;
import static com.ncnf.Utils.USER_LOCATION_KEY;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class FriendsTrackerActivityTest {


    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @InjectMocks
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(FriendsTrackerActivity.class);

    private static final String userId = "MSpKLkyyrrN3PC5KmxkoD05Vy1m2";

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public User user = Mockito.mock(User.class);

    @Spy
    private DatabaseService dbs;


    @Before
    public void setup(){
        Intents.init();

        dbs = Mockito.mock(DatabaseService.class);

        when(user.loadUserFromDB()).thenReturn(CompletableFuture.completedFuture(user));
        when(user.getEmail()).thenReturn("john@doe.ch");
        when(user.getFirstName()).thenReturn("John");
        when(user.getUuid()).thenReturn("0");
        when(user.getLoc()).thenReturn(new GeoPoint(0, 0));

        when(dbs.getField(USERS_COLLECTION_KEY + userId, USER_LOCATION_KEY)).thenReturn(CompletableFuture.completedFuture(new GeoPoint(0.05,0.05)));
        when(dbs.getField(USERS_COLLECTION_KEY + userId, FIRST_NAME_KEY)).thenReturn(CompletableFuture.completedFuture("Jane"));
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

}
