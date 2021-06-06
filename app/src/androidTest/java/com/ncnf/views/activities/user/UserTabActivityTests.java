package com.ncnf.views.activities.user;

import android.content.Intent;
import android.widget.DatePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.models.User;
import com.ncnf.notifications.firebase.FirebaseNotifications;
import com.ncnf.views.activities.bookmark.BookMarkActivity;
import com.ncnf.authentication.firebase.FirebaseUserModule;
import com.ncnf.views.activities.friends.FriendsActivity;
import com.ncnf.views.activities.main.MainActivity;
import com.ncnf.authentication.firebase.CurrentUserModule;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.views.activities.friends.FriendsActivityTest.friendsRepository;
import static com.ncnf.views.fragments.user.UserProfileTabFragment.email_popup_input_text;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules({FirebaseUserModule.class, CurrentUserModule.class})
public class UserTabActivityTests {

    private static final User mockUser = Mockito.mock(User.class);
    private static final FirebaseUser mockFirebaseUser = Mockito.mock(FirebaseUser.class);
    private Exception exception = new Exception("There was an error.");

    private static final List<User> users = Collections.singletonList(mockUser);

    // BeforeClass is required because the mocking must be done before the activity is launched
    @BeforeClass
    public static void setupClass() {
        when(mockUser.loadUserFromDB()).thenReturn(CompletableFuture.completedFuture(mockUser));
        when(mockUser.getEmail()).thenReturn("john@doe.ch");
        when(mockUser.getFullName()).thenReturn("John Doe");
        when(mockUser.getBirthDate()).thenReturn(LocalDate.now());
        when(mockUser.getSavedEvents()).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        when(mockUser.getParticipatingGroups()).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        when(mockUser.getNotifications()).thenReturn(false);
        when(mockFirebaseUser.getUid()).thenReturn("uuid");
    }

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(UserTabActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public User user = mockUser;

    @BindValue
    FirebaseNotifications firebaseNotifications = Mockito.mock(FirebaseNotifications.class);

    @BindValue
    public FirebaseUser firebaseUser = mockFirebaseUser;

    @Before
    public void setup(){
        Intents.init();
    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void fieldsAreDisabled() {
        onView(withId(R.id.user_profile_full_name)).check(matches(not(isEnabled())));
        onView(withId(R.id.user_profile_username)).check(matches(not(isEnabled())));
        onView(withId(R.id.user_profile_email)).check(matches(not(isEnabled())));
        onView(withId(R.id.user_profile_birthDay)).check(matches(not(isEnabled())));
    }

    @Test
    public void saveIsEnabledAfterChange() {
        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.user_profile_full_name)).check(matches(isEnabled()));
        onView(withId(R.id.user_profile_username)).check(matches(isEnabled()));
        onView(withId(R.id.user_profile_email)).check(matches(isEnabled()));
        onView(withId(R.id.user_profile_birthDay)).check(matches(isEnabled()));
    }

    @Test
    public void logOutReturnsToHome() {
        onView(withId(R.id.logout_button)).perform(click());
        Intents.intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void openGalleryTest(){
        onView(withId(R.id.edit_profile_picture_button)).perform(click());
        Intents.intended(hasAction(Intent.ACTION_PICK));
    }



    @Test
    public void changeFieldsWorks(){
        when(user.saveUserToDB()).thenReturn(CompletableFuture.completedFuture(true));


        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.user_profile_username)).perform(replaceText("new username"));
        onView(withId(R.id.user_profile_full_name)).perform(replaceText("new full name"));

        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.user_profile_full_name)).check(matches(not(isEnabled())));
        onView(withId(R.id.user_profile_username)).check(matches(not(isEnabled())));
        onView(withId(R.id.user_profile_email)).check(matches(not(isEnabled())));

        onView(Matchers.allOf(withId(com.google.android.material.R.id.snackbar_text), withText("Changes successfully saved")))
                .check(matches(isDisplayed()));
    }

    @Test
    public void changeFieldsFailsOnSavingUser(){
        CompletableFuture future = new CompletableFuture();
        future.completeExceptionally(new Exception());

        when(user.saveUserToDB()).thenReturn(future);

        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.user_profile_username)).perform(replaceText("new username"));
        onView(withId(R.id.user_profile_full_name)).perform(replaceText("new full name"));

        onView(withId(R.id.user_profile_birthDay)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 3, 16));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.edit_profile_button)).perform(click());


        onView(withId(android.R.id.message)).check(matches(withText(containsString("Fail to save your profile changes.\n Please disconnect, reconnect and try again"))));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.user_profile_full_name)).check(matches(isEnabled()));
        onView(withId(R.id.user_profile_username)).check(matches(isEnabled()));
        onView(withId(R.id.user_profile_email)).check(matches(isEnabled()));
        onView(withId(R.id.user_profile_birthDay)).check(matches(isEnabled()));
    }

    @Test
    public void changeFieldsWorksWithEmail(){
        when(user.getEmail()).thenReturn("email@email.com");
        when(user.changeEmail(any(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        onView(withId(R.id.edit_profile_button)).perform(click());

        onView(withId(R.id.user_profile_email)).perform(click());
        onView(withId(android.R.id.message)).check(matches(withText("Please enter your new email")));
        onView(withId(android.R.id.button2)).perform(click());

        onView(withId(R.id.user_profile_email)).perform(click());
        onView(withId(android.R.id.message)).check(matches(withText("Please enter your new email")));
        onView(withId(email_popup_input_text)).perform(typeText("e"), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(android.R.id.message)).check(matches(withText("The email you entered is incorrect : please enter a correct email address")));


        onView(withId(email_popup_input_text)).perform(typeText("email@email.com"), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(android.R.id.message)).check(matches(withText("Please enter an email address different than your current email address")));

        onView(withId(email_popup_input_text)).perform(typeText("newEmail@email.com"), closeSoftKeyboard());
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Email successfully changed")));

        onView(withId(R.id.user_profile_email)).check(matches(withText("newEmail@email.com")));
    }

    @Test
    public void enableNotificationsIsSuccessful() {
        CompletableFuture<Boolean> future = CompletableFuture.completedFuture(true);
        when(firebaseNotifications.registerToNotifications()).thenReturn(future);
        when(firebaseNotifications.unregisterFromNotifications()).thenReturn(future);

        onView(withId(R.id.profile_notification_switch)).perform(scrollTo(), click());

        //verify(registration).register();
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Notifications successfully changed")));


        onView(withId(R.id.profile_notification_switch)).perform(scrollTo(), click());

        //verify(registration).unregister();
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Notifications successfully changed")));

    }

    @Test
    public void enableNotificationsFails() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.completeExceptionally(exception);

        when(firebaseNotifications.registerToNotifications()).thenReturn(future);
        when(firebaseNotifications.unregisterFromNotifications()).thenReturn(future);

        onView(withId(R.id.profile_notification_switch)).perform(scrollTo(), click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("An error happened! Try again later")));

        onView(withId(R.id.profile_notification_switch)).perform(scrollTo(), click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("An error happened! Try again later")));
    }
}
