package com.ncnf;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.main.MainActivity;
import com.ncnf.map.MapActivity;
import com.ncnf.feed.FeedActivity;

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
public final class MainActivityTest {

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private ActivityScenarioRule activityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(activityTestRule);

    @Test
    public void testMapButton(){
        Intents.init();

        onView(withId(R.id.to_map_link)).perform(click());
        Intents.intended(hasComponent(MapActivity.class.getName()));
        
        Intents.release();
    }

    @Test
    public void feedActivityButtonWorks(){
        Intents.init();

        onView(withId(R.id.feed_view_button)).perform(click());
        Intents.intended(hasComponent(FeedActivity.class.getName()));

        Intents.release();
    }

}
