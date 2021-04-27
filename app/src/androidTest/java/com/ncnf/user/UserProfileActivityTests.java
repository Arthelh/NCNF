package com.ncnf.user;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.builder.UserBuilder;
import com.ncnf.main.MainActivity;
import com.ncnf.notification.Registration;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.Utils.BIRTH_DATE_KEY;
import static com.ncnf.Utils.EMAIL_KEY;
import static com.ncnf.Utils.FIRST_NAME_KEY;
import static com.ncnf.Utils.LAST_NAME_KEY;
import static com.ncnf.Utils.NOTIFICATIONS_KEY;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class UserProfileActivityTests {

    private static final User mockUser = Mockito.mock(User.class);
    private Exception exception = new Exception("There was an error.");

    // BeforeClass is required because the mocking must be done before the activity is launched
    @BeforeClass
    public static void setupClass() {
        when(mockUser.loadUserFromDB()).thenReturn(CompletableFuture.completedFuture(mockUser));
        when(mockUser.getEmail()).thenReturn("john@doe.ch");
        when(mockUser.getFirstName()).thenReturn("John");
        when(mockUser.getLastName()).thenReturn("Doe");
        when(mockUser.getBirthDate()).thenReturn(new Date());
    }

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(UserProfileActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public User user = mockUser;

    @BindValue
    Registration registration = Mockito.mock(Registration.class);

    @Before
    public void setup(){
        Intents.init();
    }

    @After
    public void cleanup(){
        Intents.release();
    }

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
    public void logOutReturnsToHome() {
        onView(withId(R.id.logOutButton)).perform(scrollTo(), click());
        Intents.intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void attributesAreDisplayed() {
        onView(withId(R.id.userProfileFirstName)).check(matches(withText(mockUser.getFirstName())));
        onView(withId(R.id.userProfileLastName)).check(matches(withText(mockUser.getLastName())));
    }


    @Test
    public void saveIsDisabledAfterSave() {
        CompletableFuture<Boolean> future = CompletableFuture.completedFuture(true);

        when(mockUser.saveUserToDB()).thenReturn(future);
        onView(withId(R.id.userProfileFirstName)).perform(typeText("Jean"), closeSoftKeyboard());
        onView(withId(R.id.userProfileSaveButton)).perform(click());
        onView(withId(R.id.userProfileSaveButton)).check(matches(not(isEnabled())));

        onView(withId(R.id.userProfileLastName)).perform(typeText("Dupont"), closeSoftKeyboard());
        onView(withId(R.id.userProfileSaveButton)).perform(click());
        onView(withId(R.id.userProfileSaveButton)).check(matches(not(isEnabled())));

        onView(withId(R.id.userProfileDateOfBirth)).perform(typeText("2013"), closeSoftKeyboard());
        onView(withId(R.id.userProfileSaveButton)).perform(click());
        onView(withId(R.id.userProfileSaveButton)).check(matches(not(isEnabled())));
    }

    @Test
    public void saveIsEnabledIfRequestFails() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.completeExceptionally(exception);

        when(mockUser.saveUserToDB()).thenReturn(future);

        onView(withId(R.id.userProfileFirstName)).perform(typeText("Jean"), closeSoftKeyboard());
        onView(withId(R.id.userProfileSaveButton)).perform(click());
        onView(withId(R.id.userProfileSaveButton)).check(matches(isEnabled()));

        onView(withId(R.id.userProfileLastName)).perform(typeText("Dupont"), closeSoftKeyboard());
        onView(withId(R.id.userProfileSaveButton)).perform(click());
        onView(withId(R.id.userProfileSaveButton)).check(matches(isEnabled()));

        onView(withId(R.id.userProfileDateOfBirth)).perform(typeText("2013"), closeSoftKeyboard());
        onView(withId(R.id.userProfileSaveButton)).perform(click());
        onView(withId(R.id.userProfileSaveButton)).check(matches(isEnabled()));
    }

    @Test
    public void enableNotificationsIsSuccessful() {
        CompletableFuture<Boolean> future = CompletableFuture.completedFuture(true);
        when(registration.register()).thenReturn(future);
        when(registration.unregister()).thenReturn(future);

        onView(withId(R.id.profile_notification_switch)).perform(scrollTo(), click());

        verify(registration).register();

        onView(withId(R.id.profile_notification_switch)).perform(scrollTo(), click());

        verify(registration).unregister();
    }

    @Test
    public void enableNotificationsFails() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.completeExceptionally(exception);

        when(registration.register()).thenReturn(future);
        when(registration.unregister()).thenReturn(future);

        onView(withId(R.id.profile_notification_switch)).perform(scrollTo(), click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("An error happened! Try again later")));

        onView(withId(R.id.profile_notification_switch)).perform(scrollTo(), click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("An error happened! Try again later")));
    }
}
