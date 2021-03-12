package com.example.bootcamp;

import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.firebase.auth.FirebaseAuth;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {
    @Rule
    public ActivityScenarioRule<SignUpActivity> testRule = new ActivityScenarioRule<>(SignUpActivity.class);


    @Test
    public void wrongInputsTest() throws InterruptedException {
        Intents.init();

        String invalidEmail = "test";
        String validEmail = "test@test.com";
        String invalidPassword = "test";
        String validPassword = "test12";

        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(SignUpActivity.EMPTY_FIELD_STRING))));

        onView(withId(R.id.signUpEmail)).perform(typeText(invalidEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(SignUpActivity.BADLY_FORMATTED_EMAIL_STRING))));

        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(SignUpActivity.PASSWORDS_DO_NOT_MATCH_STRING))));

        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(SignUpActivity.INVALID_PASSWORD_STRING))));

        Intents.release();
    }

}
