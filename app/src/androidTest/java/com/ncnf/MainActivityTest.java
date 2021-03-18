package com.ncnf;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ncnf.authentication.SignInActivity;
import com.ncnf.authentication.SignUpActivity;
import com.ncnf.main.MainActivity;
import com.ncnf.map.MapActivity;
import com.ncnf.feed.FeedActivity;
import com.ncnf.user.UserProfileActivity;

import org.junit.After;
import org.junit.Before;
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

    @Before
    public void setup(){
        Intents.init();
    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void testMapButton(){
        onView(withId(R.id.to_map_link)).perform(click());
        Intents.intended(hasComponent(MapActivity.class.getName()));
    }

    @Test
    public void feedActivityButtonWorks(){
        onView(withId(R.id.feedViewButton)).perform(click());
        Intents.intended(hasComponent(FeedActivity.class.getName()));
    }

    @Test
    public void signInTest(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            onView(withId(R.id.mainSignInButton)).perform(click());
            Intents.intended(hasComponent(SignInActivity.class.getName()));
        }
    }

    @Test
    public void signUpTest(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            onView(withId(R.id.mainSignUpButton)).perform(click());
            Intents.intended(hasComponent(SignUpActivity.class.getName()));
        }
    }

    @Test
    public void profileTest(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            onView(withId(R.id.mainProfileButton)).perform(click());
            Intents.intended(hasComponent(UserProfileActivity.class.getName()));
        }
    }

}
