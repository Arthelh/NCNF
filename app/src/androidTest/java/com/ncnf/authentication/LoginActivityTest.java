package com.ncnf.authentication;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.authentication.ui.LoginActivity;

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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.ncnf.Utils.BADLY_FORMATTED_EMAIL_STRING;
import static com.ncnf.Utils.EMPTY_FIELD_STRING;
import static com.ncnf.Utils.INVALID_PASSWORD_STRING;
import static com.ncnf.Utils.PASSWORDS_DO_NOT_MATCH_STRING;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
public class LoginActivityTest {
    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private ActivityScenarioRule activityTestRule = new ActivityScenarioRule(LoginActivity.class);

    @BindValue
    AuthenticationService mockedAuth = mock(AuthenticationService.class);;

    String invalidEmail = "test";
    String validEmail = "test@test.com";
    String invalidPassword = "test";
    String validPassword = "test12";
    String unsuccessfulLogin = "Unsuccessful login.";
    String unsuccessfulRegister = "Unsuccessful register.";

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(activityTestRule);

    @Before
    public void setup(){
        hiltRule.inject();
    }

    @After
    public void cleanup(){ }

    @Test
    public void signInFragmentIsCreatedTest(){

    }

    @Test
    public void signInFragmentEmptyInputTest(){
        onView(ViewMatchers.withId(R.id.signInLoginButton)).perform(click());
        onView(withId(R.id.exceptionSignIn)).check(matches(withText(containsString(EMPTY_FIELD_STRING))));
    }

    @Test
    public void signInFragmentBadlyFormattedEmailTest(){
        onView(withId(R.id.signInEmail)).perform(typeText(invalidEmail), closeSoftKeyboard());
        onView(withId(R.id.signInPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signInLoginButton)).perform(click());
        onView(withId(R.id.exceptionSignIn)).check(matches(withText(containsString(BADLY_FORMATTED_EMAIL_STRING))));
    }

    @Test
    public void signInFragmentUnsuccessfulLoginTest(){
        Exception exception = new Exception(unsuccessfulLogin);
        CompletableFuture<AuthenticationResponse> future = new CompletableFuture<>();
        future.complete(new AuthenticationResponse(false, null, exception));

        when(mockedAuth.logIn(anyString(), anyString())).thenReturn(future);

        onView(withId(R.id.signInEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signInPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signInLoginButton)).perform(click());

        verify(mockedAuth).logIn(anyString(), anyString());

        onView(withId(R.id.exceptionSignIn)).check(matches(withText(containsString(unsuccessfulLogin))));
    }
    
    @Test
    public void signUpFragmentEmptyInputTest(){
        onView(withId(R.id.registerButton)).perform(click());
        onView(ViewMatchers.withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(EMPTY_FIELD_STRING))));

        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(EMPTY_FIELD_STRING))));

        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(EMPTY_FIELD_STRING))));
    }

    @Test
    public void signUpFragmentBadlyFormattedEmailTest(){
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.signUpEmail)).perform(typeText(invalidEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(BADLY_FORMATTED_EMAIL_STRING))));
    }

    @Test
    public void signUpFragmentPasswordsDoNotMatchTest(){
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(PASSWORDS_DO_NOT_MATCH_STRING))));
    }

    @Test
    public void signUpFragmentInvalidPasswordTest() {
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(invalidPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpRegisterButton)).perform(click());
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(INVALID_PASSWORD_STRING))));
    }

    @Test
    public void signUpOrganizerViewTest(){
        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.organizerButton)).perform(click());
        onView(withId(R.id.signUpEmail)).check(matches(withHint(containsString("Business Email.."))));
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString("Organizer Registration"))));

        onView(withId(R.id.organizerButton)).perform(click());
        onView(withId(R.id.signUpEmail)).check(matches(withHint(containsString("Email.."))));
        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString("Register"))));
    }

    @Test
    public void signUpFragmentUnsuccessfulRegisterTest(){
        Exception exception = new Exception(unsuccessfulRegister);
        CompletableFuture<AuthenticationResponse> future = new CompletableFuture<>();
        future.complete(new AuthenticationResponse(false, null, exception));

        when(mockedAuth.register(anyString(), anyString())).thenReturn(future);

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpRegisterButton)).perform(click());

        verify(mockedAuth).register(anyString(), anyString());

        onView(withId(R.id.exceptionSignUp)).check(matches(withText(containsString(unsuccessfulRegister))));
    }

    @Test
    public void signUpFragmentUnsuccessfulRegisterTest2(){
        CompletableFuture<AuthenticationResponse> future = new CompletableFuture<>();
        future.complete(new AuthenticationResponse(true, null, null));

        when(mockedAuth.register(anyString(), anyString())).thenReturn(future);

        onView(withId(R.id.registerButton)).perform(click());

        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.signUpEmail)).perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.signUpPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpConfirmPassword)).perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.signUpRegisterButton)).perform(click());

        verify(mockedAuth).register(anyString(), anyString());

    }

}
