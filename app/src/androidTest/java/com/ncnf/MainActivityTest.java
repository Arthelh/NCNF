package com.ncnf;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;
import com.ncnf.authentication.SignInActivity;
import com.ncnf.authentication.SignUpActivity;
import com.ncnf.main.MainActivity;
import com.ncnf.map.MapActivity;
import com.ncnf.feed.FeedActivity;
import com.ncnf.user.UserProfileActivity;

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

    @Test
    public void testMapButton(){
        Intents.init();

        onView(withId(R.id.to_map_link)).perform(click());
        Intents.intended(hasComponent(MapActivity.class.getName()));
        
        Intents.release();
    }

//    @Test
//    public void feedActivityButtonWorks(){
//        Intents.init();
//
//        onView(withId(R.id.feed_view_button)).perform(click());
//        Intents.intended(hasComponent(FeedActivity.class.getName()));
//
//        Intents.release();
//    }

    @Test
    public void signInTest(){
        Intents.init();

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            onView(withId(R.id.mainSignInButton)).perform(click());
            Intents.intended(hasComponent(SignInActivity.class.getName()));
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
