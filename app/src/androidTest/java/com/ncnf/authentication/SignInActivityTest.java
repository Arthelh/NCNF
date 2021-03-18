package com.ncnf.authentication;


import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.main.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.Utils.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
public final class SignInActivityTest {

    String invalidEmail = "test";
    String validEmail = "test@test.com";
    String invalidPassword = "test";
    String validPassword = "test12";
    String unsuccessfulRegister = "Unsuccessful register.";

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private ActivityScenarioRule activityTestRule = new ActivityScenarioRule<>(SignInActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(activityTestRule);

    @Before
    public void setup(){
        Intents.init();
    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void emptyInputTest(){
        onView(ViewMatchers.withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.exceptionSignIn)).check(matches(withText(containsString(EMPTY_FIELD_STRING))));
    }

    @Test
    public void badlyFormattedEmailTest(){
        onView(withId(R.id.signInEmail)).perform(typeText(invalidEmail), closeSoftKeyboard());
        onView(withId(R.id.signInPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.exceptionSignIn)).check(matches(withText(containsString(BADLY_FORMATTED_EMAIL_STRING))));
    }

    @Test
    public void unsuccessfulRegisterTest(){
        onView(withId(R.id.progressBar2)).check(matches(not(isDisplayed())));

//        when(mockedAuth.register(anyString(), anyString())).thenReturn(future);

        onView(withId(R.id.signInEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signInPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());

//        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(unsuccessfulRegister))));
    }
}