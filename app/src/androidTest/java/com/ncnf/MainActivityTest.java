package com.example.bootcamp;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void first_test(){
        Intents.init();

        String test_string = "This is a test";
        onView(withId(R.id.name_field)).perform(typeText(test_string), closeSoftKeyboard());
        onView(withId(R.id.name_button)).perform(click());

        Intents.intended(hasComponent(DisplayMessageActivity.class.getName()));


        Intents.release();
    }

    @Test
    public void test_event1(){
        Intents.init();

        onView(withId(R.id.eventButton)).perform(click());

        Intents.intended(hasComponent(EventActivity.class.getName()));


        Intents.release();
    }

    @Test
    public void test_event2(){
        Intents.init();

        onView(withId(R.id.event2Button)).perform(click());

        Intents.intended(hasComponent(EventActivity.class.getName()));


        Intents.release();
    }


}
