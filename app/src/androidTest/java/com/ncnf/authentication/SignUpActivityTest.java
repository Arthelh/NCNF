package com.ncnf.authentication;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ncnf.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.ncnf.Utils.*;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {
    @Rule
    public ActivityScenarioRule<SignUpActivity> testRule = new ActivityScenarioRule<>(SignUpActivity.class);


    @Test
    public void wrongInputsTest() {
        Intents.init();

        String invalidEmail = "test";
        String validEmail = "test@test.com";
        String invalidPassword = "test";
        String validPassword = "test12";

        onView(ViewMatchers.withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(EMPTY_FIELD_STRING))));

        onView(withId(R.id.signUpEmail)).perform(typeText(invalidEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(BADLY_FORMATTED_EMAIL_STRING))));

        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(PASSWORDS_DO_NOT_MATCH_STRING))));

        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(INVALID_PASSWORD_STRING))));

        Intents.release();
    }

}
