package com.ncnf.home;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.authentication.ui.LoginActivity;
import com.ncnf.event.update.EventNewsActivity;
import com.ncnf.main.MainActivity;
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
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
        Intents.intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void createEventActivityOpensTest(){
        onView(withId(R.id.homeCreateEventButton)).perform(click());
        Intents.intended(hasComponent(LoginActivity.class.getName()));
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
