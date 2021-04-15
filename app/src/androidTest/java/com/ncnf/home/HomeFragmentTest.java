package com.ncnf.home;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.authentication.ui.LoginActivity;
import com.ncnf.event.create.EventCreateActivity;
import com.ncnf.event.update.EventNewsActivity;
import com.ncnf.main.MainActivity;
import com.ncnf.user.UserProfileActivity;
import com.ncnf.user_search.UserSearchActivity;

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
import static com.ncnf.Utils.NEXT_ACTIVITY_EXTRA_KEY;

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
        Intents.intended(hasExtra(NEXT_ACTIVITY_EXTRA_KEY, UserProfileActivity.class));
    }

    @Test
    public void createEventActivityOpensTest(){
        onView(withId(R.id.homeCreateEventButton)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());
        onView(withId(R.id.homeCreateEventButton)).perform(click());
        onView(withId(android.R.id.button1)).check(matches(isClickable()));
        onView(withId(android.R.id.button1)).perform(click());
        Intents.intended(hasComponent(LoginActivity.class.getName()));
        Intents.intended(hasExtra(NEXT_ACTIVITY_EXTRA_KEY, EventCreateActivity.class));
    }

    @Test
    public void searchUserOpensTest(){
        onView(withId(R.id.homeSearchUserButton)).perform(click());
        Intents.intended(hasComponent(UserSearchActivity.class.getName()));
    }

    @Test
    public void eventNewsActivityOpensTest(){
        onView(withId(R.id.homeEventNewsButton)).perform(click());
        Intents.intended(hasComponent(EventNewsActivity.class.getName()));
    }

    @Test
    public void searchUserActivityOpensTest(){
        onView(withId(R.id.homeSearchUserButton)).perform(click());
        Intents.intended(hasComponent(UserSearchActivity.class.getName()));
    }
}
