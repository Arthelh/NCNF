package com.ncnf.main;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.rules.RuleChain;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public final class MainActivityTest {

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule activityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(activityTestRule);

//    @Before
//    public void setup(){
//        Intents.init();
//    }
//
//    @After
//    public void cleanup(){
//        Intents.release();
//    }

//    @Test
//    public void testMapButton(){
//        onView(ViewMatchers.withId(R.id.to_map_link)).perform(click());
//        Intents.intended(hasComponent(MapActivity.class.getName()));
//    }
//
//    @Test
//    public void feedActivityButtonWorks(){
//        onView(withId(R.id.feedViewButton)).perform(click());
//        Intents.intended(hasComponent(FeedActivity.class.getName()));
//    }
//
//    @Test
//    public void profileTest(){
//        onView(withId(R.id.mainProfileButton)).perform(click());
//        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
//            Intents.intended(hasComponent(UserProfileActivity.class.getName()));
//        } else {
//            Intents.intended(hasComponent(LoginActivity.class.getName()));
//        }
//    }

}
