package com.ncnf.views.activities.login;

import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.FirebaseException;
import com.ncnf.R;
import com.ncnf.authentication.firebase.FirebaseAuthentication;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.utilities.StringCodes.BADLY_FORMATTED_EMAIL_STRING;
import static com.ncnf.utilities.StringCodes.EMPTY_FIELD_STRING;
import static com.ncnf.utilities.StringCodes.INVALID_PASSWORD_STRING;
import static com.ncnf.utilities.StringCodes.PASSWORDS_DO_NOT_MATCH_STRING;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
public class LoginActivityTest {
    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule activityTestRule = new ActivityScenarioRule(LoginActivity.class);

    @BindValue
    FirebaseAuthentication mockedAuth = mock(FirebaseAuthentication.class);

    String invalidEmail = "test";
    String validEmail = "test@test.com";
    String invalidPassword = "test";
    String validPassword = "test12";

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(activityTestRule);

    @Before
    public void setup(){
        hiltRule.inject();
    }

    @After
    public void cleanup(){ }

    @Test
    public void signInFragmentEmptyInputTest() throws InterruptedException {
        onView(withId(R.id.loginViewPager)).perform(swipeLeft());
        Thread.sleep(500);
        onView(withId(R.id.loginViewPager)).perform(swipeRight());
        Thread.sleep(500);
        onView(withId(R.id.signInLoginButton)).perform(click());
        onView(withId(R.id.signInEmail)).check(matches(hasErrorText(EMPTY_FIELD_STRING)));
        onView(withId(R.id.signInEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signInLoginButton)).perform(click());
        onView(withId(R.id.signInPassword)).check(matches(hasErrorText(EMPTY_FIELD_STRING)));
    }

    @Test
    public void signInFragmentBadlyFormattedEmailTest() {
        onView(withId(R.id.signInEmail)).perform(typeText(invalidEmail), closeSoftKeyboard());
        onView(withId(R.id.signInPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signInLoginButton)).perform(click());
        onView(withId(R.id.signInEmail)).check(matches(hasErrorText(BADLY_FORMATTED_EMAIL_STRING)));
    }

    @Test
    public void signInFragmentUnsuccessfulLoginTest() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.completeExceptionally(new FirebaseException("There was an error."));

        when(mockedAuth.logIn(anyString(), anyString())).thenReturn(future);

        onView(withId(R.id.signInEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signInPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signInLoginButton)).perform(click());

        verify(mockedAuth).logIn(validEmail, validPassword);

        onView(withId(android.R.id.button1)).check(matches(isClickable()));
    }
    
    @Test
    public void signUpFragmentEmptyInputTest() throws InterruptedException {
        onView(withId(R.id.loginViewPager)).perform(swipeLeft());
        Thread.sleep(500);
        onView(ViewMatchers.withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.signUpEmail)).check(matches(hasErrorText(EMPTY_FIELD_STRING)));

        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.signUpPassword)).check(matches(hasErrorText(EMPTY_FIELD_STRING)));

        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.signUpConfirmPassword)).check(matches(hasErrorText(EMPTY_FIELD_STRING)));
    }

    @Test
    public void signUpFragmentBadlyFormattedEmailTest() throws InterruptedException {
        onView(withId(R.id.loginViewPager)).perform(swipeLeft());
        Thread.sleep(500);
        onView(withId(R.id.signUpEmail)).perform(typeText(invalidEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.signUpEmail)).check(matches(hasErrorText(BADLY_FORMATTED_EMAIL_STRING)));
    }

    @Test
    public void signUpFragmentPasswordsDoNotMatchTest() throws InterruptedException {
        onView(withId(R.id.loginViewPager)).perform(swipeLeft());
        Thread.sleep(500);
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.signUpConfirmPassword)).check(matches(hasErrorText(PASSWORDS_DO_NOT_MATCH_STRING)));
    }

    @Test
    public void signUpFragmentInvalidPasswordTest() throws InterruptedException {
        onView(withId(R.id.loginViewPager)).perform(swipeLeft());
        Thread.sleep(500);
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.signUpPassword)).check(matches(hasErrorText(INVALID_PASSWORD_STRING)));
    }

    @Test
    public void signUpFragmentUnsuccessfulRegisterTest() throws InterruptedException {
        CompletableFuture<Boolean> future = new CompletableFuture();
        future.completeExceptionally(new Exception());

        when(mockedAuth.register(anyString(), anyString())).thenReturn(future);

        onView(withId(R.id.loginViewPager)).perform(swipeLeft());
        Thread.sleep(500);
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpRegisterButton)).perform(click());

        verify(mockedAuth).register(anyString(), anyString());

        onView(withId(android.R.id.button1)).check(matches(isClickable()));
    }

    @Test
    public void signUpFragmentSuccessfulRegisterTest() throws InterruptedException {
        CompletableFuture<Boolean> future = CompletableFuture.completedFuture(true);

        when(mockedAuth.register(anyString(), anyString())).thenReturn(future);

        onView(withId(R.id.loginViewPager)).perform(swipeLeft());
        Thread.sleep(500);
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpRegisterButton)).perform(click());

        verify(mockedAuth).register(anyString(), anyString());
    }

}
