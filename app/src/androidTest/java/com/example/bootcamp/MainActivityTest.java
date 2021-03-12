package com.example.bootcamp;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void signInTest(){
        Intents.init();

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            onView(withId(R.id.mainSignInButton)).perform(click());
            Intents.intended(hasComponent(LoginActivity.class.getName()));
        }

        Intents.release();
    }

    @Test
    public void signUpTest(){
        Intents.init();

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            onView(withId(R.id.mainSignUpButton)).perform(click());
            Intents.intended(hasComponent(SignUpActivity.class.getName()));
        }

        Intents.release();
    }

    @Test
    public void profileTest(){
        Intents.init();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            onView(withId(R.id.mainProfileButton)).perform(click());
            Intents.intended(hasComponent(UserProfileActivity.class.getName()));
        }

        Intents.release();
    }

}
