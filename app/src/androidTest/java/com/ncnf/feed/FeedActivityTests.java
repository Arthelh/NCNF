package com.ncnf.feed;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ncnf.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class FeedActivityTests {

    @Rule
    public ActivityScenarioRule<FeedActivity> testRule =
            new ActivityScenarioRule<>(FeedActivity.class);

    @Test
    public void eventActivityOpens(){
        Intents.init();

        onView(withId(R.id.event)).perform(click());
        Intents.intended(hasComponent(EventActivity.class.getName()));

        Intents.release();
    }
}
