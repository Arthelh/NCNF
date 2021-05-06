package com.ncnf.friends;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.database.DatabaseService;
import com.ncnf.friends.ui.FriendsActivity;
import com.ncnf.user.FirebaseUserModule;
import com.ncnf.user.FriendsRepository;
import com.ncnf.user.User;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules({FirebaseUserModule.class})
public class FriendsActivityTest {

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(FriendsActivity.class);

    private static final FriendsRepository friendsRepository = Mockito.mock(FriendsRepository.class);
    private static final DatabaseService databaseService = Mockito.mock(DatabaseService.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public FirebaseUser firebaseUser = Mockito.mock(FirebaseUser.class);

    @BindValue
    public FriendsRepository mockFriendsRepository = friendsRepository;

    private static final User u1 = new User(databaseService,"1", "johnny", "john@bar.com","John",  "Smith", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, false);
    private static final List<User> users = Collections.singletonList(u1);

    @BeforeClass
    public static void setup() {
        when(friendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        when(friendsRepository.awaitingRequests(anyString())).thenReturn(CompletableFuture.completedFuture(users));
    }

    @Test
    public void friendsAreDisplayedAndClickable() {
        onView(withId(R.id.friends_recycler_view)).perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText("John Smith")), click()
        ));
        // TODO: check that it go user profile when it is implemented
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("DISPLAY_USER_PROFILE")));
    }

//    @Test
//    public void searchingUserCreatesRecyclerView() throws InterruptedException {
//        onView(withId(R.id.friends_view_pager)).perform(swipeLeft());
//
//        onView(withId(R.id.add_friend_search_bar)).perform(click());
//        onView(withClassName(containsString(EditText.class.getSimpleName()))).perform(typeText("Gab2"));
//        onView(withId(R.id.add_friend_search_bar)).perform(pressKey(KeyEvent.KEYCODE_ENTER));
//
//        Thread.sleep(5000);
//
//        onView(withId(R.id.add_friend_recycler_view)).check(new RecyclerViewItemCountAssertion(1));
//    }
//
//
//    @Test
//    public void searchingUserThatExistsWorks() {
//
//        when(mockDbRef.orderBy(anyString()).startAt(anyString()).endAt(anyString())).thenReturn(mockQuery);
//
//        onView(withId(R.id.user_search)).perform(typeText("Gab 2"));
//        onView(withId(R.id.user_search)).perform(pressKey(KeyEvent.KEYCODE_ENTER));
//        onView(withId(R.id.user_search)).perform(pressImeActionButton());
//        onView(withId(R.id.add_friend_recycler_view)).check(matches(isDisplayed()));
//    }

}

