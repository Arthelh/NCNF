package com.ncnf.feed;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ncnf.R;
import com.ncnf.feed.ui.EventActivity;
import com.ncnf.feed.ui.FeedActivity;
import com.ncnf.main.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@HiltAndroidTest
public class FeedFragmentTest {

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(MainActivity.class));

    @Before
    public void setup(){
        Intents.init();
        onView(withId(R.id.navigation_feed)).perform(click());
    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void eventActivityOpens(){
        onView(withId(R.id.event)).perform(click());
        Intents.intended(hasComponent(EventActivity.class.getName()));
    }
}
