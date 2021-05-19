package com.ncnf.views.fragments.home;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.views.activities.login.LoginActivity;
import com.ncnf.views.activities.friends.FriendsActivity;
import com.ncnf.views.activities.main.MainActivity;
import com.ncnf.views.activities.group.GroupCreateActivity;
import com.ncnf.views.activities.event.EventNewsActivity;
import com.ncnf.views.activities.group.FriendsTrackerActivity;
import com.ncnf.views.activities.user.UserTabActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.ncnf.utilities.StringCodes.NEXT_ACTIVITY_EXTRA_KEY;

@HiltAndroidTest
public final class HomeFragmentTest {

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(MainActivity.class));

    @Before
    public void setup(){
        Intents.init();
        onView(withId(R.id.navigation_home)).perform(click());
    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void loginActivityOpensTest(){
        onView(withId(R.id.homeProfileButton)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
        onView(withId(R.id.homeProfileButton)).perform(click());
        onView(withId(android.R.id.button1)).check(matches(isClickable()));
        onView(withId(android.R.id.button1)).perform(click());
        Intents.intended(hasComponent(LoginActivity.class.getName()));
        Intents.intended(hasExtra(NEXT_ACTIVITY_EXTRA_KEY, UserTabActivity.class));
    }

    @Test
    public void friendsActivityOpensTest() {
        onView(withId(R.id.homeFriendsButton)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
        onView(withId(R.id.homeFriendsButton)).perform(click());
        onView(withId(android.R.id.button1)).check(matches(isClickable())).perform(click());
        Intents.intended(hasComponent(LoginActivity.class.getName()));
        Intents.intended(hasExtra(NEXT_ACTIVITY_EXTRA_KEY, FriendsActivity.class));
    }


    @Test
    public void friendsTrackerActivityOpensTest() {
        onView(withId(R.id.track_friends_button)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
        onView(withId(R.id.track_friends_button)).perform(click());
        onView(withId(android.R.id.button1)).check(matches(isClickable())).perform(click());
        Intents.intended(hasComponent(LoginActivity.class.getName()));
        Intents.intended(hasExtra(NEXT_ACTIVITY_EXTRA_KEY, FriendsTrackerActivity.class));
    }


    @Test
    public void createEventActivityOpensTest(){
        onView(withId(R.id.homeCreateEventButton)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
        onView(withId(R.id.homeCreateEventButton)).perform(click());
        onView(withId(android.R.id.button1)).check(matches(isClickable()));
        onView(withId(android.R.id.button1)).perform(click());
        Intents.intended(hasComponent(LoginActivity.class.getName()));
        Intents.intended(hasExtra(NEXT_ACTIVITY_EXTRA_KEY, GroupCreateActivity.class));
    }

    @Test
    public void eventNewsActivityOpensTest(){
        onView(withId(R.id.homeEventNewsButton)).perform(click());
        Intents.intended(hasComponent(EventNewsActivity.class.getName()));
    }
}
