package com.ncnf.views.activities.group;

import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.User;
import com.ncnf.notifications.firebase.FirebaseNotifications;
import com.ncnf.repositories.FriendsRepository;
import com.ncnf.repositories.GroupRepository;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;
import com.ncnf.views.activities.bookmark.BookMarkActivity;
import com.ncnf.authentication.firebase.FirebaseUserModule;
import com.ncnf.views.activities.friends.FriendsActivity;
import com.ncnf.views.activities.main.MainActivity;
import com.ncnf.authentication.firebase.CurrentUserModule;
import com.ncnf.views.activities.user.UserTabActivity;

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

import javax.inject.Inject;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.when;

import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class GroupCreationActivityTest {

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(GroupCreationActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    private static final GroupRepository mockedGroupRepository = Mockito.mock(GroupRepository.class);
    @BindValue
    public GroupRepository groupRepository = mockedGroupRepository;

    private static final User mockedUser = Mockito.mock(User.class);
    @BindValue
    public User user = mockedUser;

    private static final FirebaseCacheFileStore mockedFirebaseCacheFileStore = Mockito.mock(FirebaseCacheFileStore.class);
    @BindValue
    public FirebaseCacheFileStore fileStore = mockedFirebaseCacheFileStore;

    private static final FriendsRepository mockedFriendsRepository = Mockito.mock(FriendsRepository.class);
    @BindValue
    public FriendsRepository friendsRepository = mockedFriendsRepository;

    private static final FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);
    @BindValue
    public FirebaseDatabase databaseService = mockedFirebaseDatabase;

    @BeforeClass
    public static void setupClass(){
        User u1 = new User(mockedFirebaseDatabase, "1234567890", "", "foo@bar.com","", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null, null);
        List<User> users = new ArrayList<>();
        users.add(u1);
        when(mockedFriendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        //mockedFriendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        when(mockedUser.loadUserFromDB()).thenReturn(CompletableFuture.completedFuture(mockedUser));
    }

    @Before
    public void setup(){
        Intents.init();
    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void fieldEmptyAlertWorks(){
         onView(withId(R.id.next_step_group_creation_button)).perform(click());

        onView(Matchers.allOf(withId(com.google.android.material.R.id.snackbar_text), withText(containsString("Please fill every field"))))
                .check(matches(isDisplayed()));
    }

    @Ignore
    @Test
    public void writingInAllField() throws InterruptedException {
        onView(withId(R.id.create_group_button)).check(matches(not(isEnabled())));

        onView(withId(R.id.group_name)).perform(replaceText("GroupName"), closeSoftKeyboard());

        Thread.sleep(100);
        onView(withId(R.id.date_text_group_creation)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 3, 16));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.date_text_group_creation)).check(matches(withText(containsString("2020-02-16"))));

        Thread.sleep(100);
        onView(withId(R.id.time_text_group_creation)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(18, 30));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.time_text_group_creation)).check(matches(withText(containsString("18:30"))));

        Thread.sleep(100);
        onView(withId(R.id.next_step_group_creation_button)).perform(click());
        onView(withId(R.id.group_name)).check(matches(not(isDisplayed())));
        onView(withId(R.id.friends_selector_group_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.create_group_button)).check(matches(isEnabled()));
    }

    @Ignore
    @Test
    public void creatingGroupWithNobodyIsImpossible() throws InterruptedException {

        onView(withId(R.id.group_name)).perform(replaceText("GroupName"), closeSoftKeyboard());
        Thread.sleep(100);
        onView(withId(R.id.date_text_group_creation)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 3, 16));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.date_text_group_creation)).check(matches(withText(containsString("2020-02-16"))));

        Thread.sleep(100);
        onView(withId(R.id.time_text_group_creation)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(18, 30));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.time_text_group_creation)).check(matches(withText(containsString("18:30"))));

        Thread.sleep(100);
        onView(withId(R.id.next_step_group_creation_button)).perform(click());
        onView(withId(R.id.group_name)).check(matches(not(isDisplayed())));
        onView(withId(R.id.friends_selector_group_recycler_view)).check(matches(isDisplayed()));

        onView(withId(R.id.create_group_button)).check(matches(isEnabled()));
        onView(withId(R.id.create_group_button)).perform(click());

        onView(Matchers.allOf(withId(com.google.android.material.R.id.snackbar_text), withText(containsString("You can't"))))
                .check(matches(isDisplayed()));
    }

    @Ignore
    @Test
    public void canGoBackToEditing() throws InterruptedException {
        onView(withId(R.id.group_name)).perform(replaceText("GroupName"));

        Thread.sleep(100);
        onView(withId(R.id.date_text_group_creation)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 3, 16));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.date_text_group_creation)).check(matches(withText(containsString("2020-02-16"))));

        Thread.sleep(100);
        onView(withId(R.id.time_text_group_creation)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(18, 30));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.time_text_group_creation)).check(matches(withText(containsString("18:30"))));

        Thread.sleep(100);
        onView(withId(R.id.next_step_group_creation_button)).perform(click());
        onView(withId(R.id.group_name)).check(matches(not(isDisplayed())));
        onView(withId(R.id.friends_selector_group_recycler_view)).check(matches(isDisplayed()));

        Thread.sleep(100);
        onView(withId(R.id.next_step_group_creation_button)).perform(click());
        onView(withId(R.id.group_name)).check(matches(isDisplayed()));
        onView(withId(R.id.friends_selector_group_recycler_view)).check(matches(not(isDisplayed())));
    }

    @Ignore
    @Test
    public void createsCorrectlyAGroup(){
        when(groupRepository.storeGroup(any())).thenReturn(CompletableFuture.completedFuture(true));
        when(mockedUser.addOwnedGroup(any())).thenReturn(CompletableFuture.completedFuture(true));
        when(mockedUser.addParticipatingGroup(any())).thenReturn(CompletableFuture.completedFuture(true));
        when(mockedFirebaseDatabase.updateArrayField(anyString(), anyString(), anyObject())).thenReturn(CompletableFuture.completedFuture(true));


        onView(withId(R.id.group_name)).perform(replaceText("GroupName"));

        onView(withId(R.id.date_text_group_creation)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 3, 16));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.date_text_group_creation)).check(matches(withText(containsString("2020-02-16"))));

        onView(withId(R.id.time_text_group_creation)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(18, 30));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.time_text_group_creation)).check(matches(withText(containsString("18:30"))));

        onView(withId(R.id.next_step_group_creation_button)).perform(click());
        onView(withId(R.id.group_name)).check(matches(not(isDisplayed())));
        onView(withId(R.id.friends_selector_group_recycler_view)).check(matches(isDisplayed()));


        onView(withId(R.id.create_group_button)).check(matches(isEnabled()));
        onView(withId(R.id.check_friend_button)).perform(click());
        onView(withId(R.id.create_group_button)).perform(click());
        Intents.intended(hasComponent(GroupActivity.class.getName()));

    }

    /**
     * To perform the following test, you must uncomment line 114 and comment line 115
     */
    /*
    @Test
    public void hasNoFriendsToAdd(){
        when(mockedFriendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));

        onView(withId(R.id.group_name)).perform(replaceText("GroupName"));

        onView(withId(R.id.date_text_group_creation)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 3, 16));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.date_text_group_creation)).check(matches(withText(containsString("2020-02-16"))));

        onView(withId(R.id.time_text_group_creation)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(18, 30));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.time_text_group_creation)).check(matches(withText(containsString("18:30"))));

        onView(withId(R.id.next_step_group_creation_button)).perform(click());
        onView(withId(R.id.group_name)).check(matches(not(isDisplayed())));
        onView(withId(R.id.friends_selector_group_recycler_view)).check(matches(isDisplayed()));

        onView(withId(R.id.friends_group_selector_text)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    */

    @Test
    public void importingPhotosWorks(){
        onView(withId(R.id.group_picture)).perform(click());
        Intents.intended(hasAction(Intent.ACTION_PICK));
    }
}
