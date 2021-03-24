package com.ncnf.user;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.main.MainActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.Utils.BIRTH_YEAR_KEY;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.LAST_NAME_KEY;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class UserProfileActivityTests {

    private static PrivateUser mockUser = Mockito.mock(PrivateUser.class);
    private static CompletableFuture<DatabaseResponse> future = new CompletableFuture();

    // BeforeClass is required because the mocking must be done before the activity is launched
    @BeforeClass
    public static void setup() {
        when(mockUser.loadUserFromBD()).thenReturn(future);
    }

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(UserProfileActivity.class));

    @BindValue
    public PrivateUser user = mockUser;

    @Test
    public void titleIsVisible() {
        onView(withId(R.id.welcomeToProfileText)).check(matches(withText("Welcome to your Profile")));
    }

    @Test
    public void saveIsDisabledByDefault() {
        onView(withId(R.id.userProfileSaveButton)).check(matches(not(isEnabled())));
    }

    @Test
    public void saveIsEnabledAfterChange() {
        onView(withId(R.id.userProfileFirstName)).perform(typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.userProfileSaveButton)).check(matches(isEnabled()));
    }

    @Test
    public void saveIsDisabledAfterSave() {
        CompletableFuture<DatabaseResponse> future = new CompletableFuture<>();
        when(user.updateFirstName(anyString())).thenReturn(future);
        onView(withId(R.id.userProfileFirstName)).perform(typeText("John"), closeSoftKeyboard());
        onView(withId(R.id.userProfileSaveButton)).perform(click());
        onView(withId(R.id.userProfileSaveButton)).check(matches(not(isEnabled())));
    }

    @Test
    public void logOutReturnsToHome() {
        Intents.init();
        onView(withId(R.id.logOutButton)).perform(click());
        Intents.intended(hasComponent(MainActivity.class.getName()));
        Intents.release();
    }

    @Test
    public void attributesAreDisplayed() throws InterruptedException {
        HashMap<String, String> values = new HashMap<>();
        values.put(FIRST_NAME_KEY, "John");
        values.put(LAST_NAME_KEY, "Doe");
        values.put(BIRTH_YEAR_KEY, "2012-08-02");
        future.complete(new DatabaseResponse(true, values, null));
        onView(withId(R.id.userProfileFirstName)).check(matches(withText("John")));
    }


}
