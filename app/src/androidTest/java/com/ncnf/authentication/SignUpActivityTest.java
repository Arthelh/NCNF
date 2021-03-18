package com.ncnf.authentication;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.Utils.BADLY_FORMATTED_EMAIL_STRING;
import static com.ncnf.Utils.EMPTY_FIELD_STRING;
import static com.ncnf.Utils.EMPTY_STRING;
import static com.ncnf.Utils.INVALID_PASSWORD_STRING;
import static com.ncnf.Utils.PASSWORDS_DO_NOT_MATCH_STRING;
import static org.hamcrest.Matchers.containsString;

@HiltAndroidTest
public class SignUpActivityTest {


    String invalidEmail = "test";
    String validEmail = "test@test.com";
    String invalidPassword = "test";
    String validPassword = "test12";

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(SignUpActivity.class));

    @Before
    public void setup(){
        Intents.init();
//        hiltRule.inject();
    }

    @After
    public void cleanup(){
        Intents.release();
    }
    
    @Test
    public void emptyInputTest(){
        onView(ViewMatchers.withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(EMPTY_FIELD_STRING))));
    }

    @Test
    public void badlyFormattedEmailTest(){
        onView(withId(R.id.signUpEmail)).perform(typeText(invalidEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(BADLY_FORMATTED_EMAIL_STRING))));
    }

    @Test
    public void passwordsDoNotMatchTest(){
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(PASSWORDS_DO_NOT_MATCH_STRING))));
    }

    @Test
    public void invalidPasswordTest() {
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(INVALID_PASSWORD_STRING))));
    }

    @Test
    public void setFieldsEmptyTest(){
        onView(withId(R.id.signUpEmail)).check(matches(withText(EMPTY_STRING)));
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        onView(withId(R.id.signUpEmail)).check(matches(withText(EMPTY_STRING)));
    }
}
