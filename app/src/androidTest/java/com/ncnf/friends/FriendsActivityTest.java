package com.ncnf.friends;

import android.view.KeyEvent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseUser;
import com.ncnf.R;
import com.ncnf.database.DatabaseService;
import com.ncnf.friends.ui.FriendsActivity;
import com.ncnf.firebase.modules.FirebaseUserModule;
import com.ncnf.user.User;
import com.ncnf.user.friend.FriendsRepository;
import com.ncnf.utilities.CustomRecyclerViewAction;
import com.ncnf.utilities.RecyclerViewItemCountAssertion;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;

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
    private static final User u1 = new User(databaseService, "1", "johnny", "john@bar.com","John",  "Smith", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, new Date(), null);
    private static final List<User> users = Collections.singletonList(u1);

    @BeforeClass
    public static void setup() {
        when(friendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        when(friendsRepository.searchFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        when(friendsRepository.awaitingRequests(anyString())).thenReturn(CompletableFuture.completedFuture(users));
    }

    @Test
    public void friendsPageViewerTest() {
        onView(withId(R.id.friends_view_pager)).perform(swipeLeft());
        onView(withId(R.id.friends_view_pager)).perform(swipeRight());
    }

    @Test
    public void friendsFragmentDisplaysOneFriend(){
        when(friendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        onView(withId(R.id.friends_recycler_view)).check(new RecyclerViewItemCountAssertion(1));
    }

    @Test
    public void friendsFragmentDisplaysNoFriendMessage(){
        when(friendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        onView(withId(R.id.friends_recycler_view)).check(new RecyclerViewItemCountAssertion(0));
        onView(withId(R.id.friends_text)).check(matches(isDisplayed()));
    }

    @Test
    public void friendsFragmentButtonSwitchesBetweenViews(){
        when(friendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        when(friendsRepository.searchFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        onView(withId(R.id.friends_switch_button)).perform(click());
        onView(withId(R.id.friends_search_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.friends_switch_button)).perform(click());
        onView(withId(R.id.friends_search_bar)).check(matches(not(isDisplayed())));
    }

    @Test
    public void friendsFragmentSearchUserDisplaysNoUserMessage(){
        when(friendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        when(friendsRepository.searchFriends(anyString())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        onView(withId(R.id.friends_switch_button)).perform(click());
        onView(withId(R.id.friends_search_bar)).perform(click());
        onView(withHint("Username...")).perform(typeText("test")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withId(R.id.friends_recycler_view)).check(new RecyclerViewItemCountAssertion(0));
        onView(withId(R.id.friends_text)).check(matches(isDisplayed()));
    }

    @Test
    public void friendsFragmentSearchUserDisplaysOneUser(){
        when(friendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        when(friendsRepository.searchFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        onView(withId(R.id.friends_switch_button)).perform(click());
        onView(withId(R.id.friends_search_bar)).perform(click());
        onView(withHint("Username...")).perform(typeText("test")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withId(R.id.friends_recycler_view)).check(new RecyclerViewItemCountAssertion(1));
    }

    @Test
    public void friendsFragmentClickOnFriendLeadsToPublicProfile(){
        when(friendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        onView(withId(R.id.friends_recycler_view)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.friends_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.public_profile_header)).check(matches(isDisplayed()));
    }

    @Test
    public void friendsFragmentClickOnRemoveFriendWorks(){
        when(friendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        when(friendsRepository.removeFriend(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));
        onView(withId(R.id.friends_recycler_view)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.friends_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, longClick()));
        onView(withId(R.id.friends_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, CustomRecyclerViewAction.clickOnButtonInRecyclerViewItem(R.id.friend_card_button)));
        verify(friendsRepository).removeFriend(anyString(), anyString());
        onView(withId(R.id.friends_recycler_view)).check(new RecyclerViewItemCountAssertion(0));
    }

    @Test
    public void friendsFragmentClickOnAddFriendWorks(){
        when(friendsRepository.getFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        when(friendsRepository.request(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));
        when(friendsRepository.searchFriends(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        onView(withId(R.id.friends_switch_button)).perform(click());
        onView(withId(R.id.friends_search_bar)).perform(click());
        onView(withHint("Username...")).perform(typeText("test")).perform(pressKey(KeyEvent.KEYCODE_ENTER));
        onView(withId(R.id.friends_recycler_view)).check(new RecyclerViewItemCountAssertion(1));
        onView(withId(R.id.friends_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, CustomRecyclerViewAction.clickOnButtonInRecyclerViewItem(R.id.friend_card_button)));
        verify(friendsRepository).request(anyString(), anyString());;
    }

    @Test
    public void friendsRequestsFragmentShowsOneRequest(){
        when(friendsRepository.awaitingRequests(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        onView(withId(R.id.friends_view_pager)).perform(swipeLeft());
        onView(withId(R.id.friends_requests_recycler_view)).check(new RecyclerViewItemCountAssertion(1));
    }

    @Test
    public void friendsRequestsFragmentShowsNoRequest(){
        when(friendsRepository.awaitingRequests(anyString())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        onView(withId(R.id.friends_view_pager)).perform(swipeLeft());
        onView(withId(R.id.friends_requests_recycler_view)).check(new RecyclerViewItemCountAssertion(0));
    }

    @Test
    public void friendsRequestsFragmentShowsMessageOnAccept() throws InterruptedException {
        when(friendsRepository.awaitingRequests(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        onView(withId(R.id.friends_view_pager)).perform(swipeLeft());
        Thread.sleep(2000);
        onView(withId(R.id.accept_friends_request)).perform(click());
        onView(withId(R.id.friends_request_message)).check(matches(isDisplayed()));
    }

    @Test
    public void friendsRequestsFragmentShowsMessageOnDecline() throws InterruptedException {
        when(friendsRepository.awaitingRequests(anyString())).thenReturn(CompletableFuture.completedFuture(users));
        onView(withId(R.id.friends_view_pager)).perform(swipeLeft());
        Thread.sleep(2000);
        onView(withId(R.id.decline_friends_request)).perform(click());
        onView(withId(R.id.friends_request_message)).check(matches(isDisplayed()));
    }

}

