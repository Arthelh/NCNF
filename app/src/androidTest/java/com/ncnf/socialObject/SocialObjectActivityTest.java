package com.ncnf.socialObject;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

@HiltAndroidTest
public class SocialObjectActivityTest {

    private final Event event = new Event("hg0aavb0Fce6EgX3HrTW05rKSww1", "TestGeo", LocalDateTime.of(2021, 5, 4, 12, 0), new GeoPoint(46.518689, 6.568067), "Ecublens", "TestGeo", Event.Type.Conference, 0, 0, "beltan@test.com");
    private final CompletableFuture<SocialObject> eventC = new CompletableFuture<>();

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(MainActivity.class));

    @Before
    public void setup(){
        onView(withId(R.id.navigation_feed)).perform(click());
        onView(withId(R.id.feed_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void test_name(){
        onView(withId(R.id.eventName)).check(matches(withText(containsString("Concert"))));
    }

    @Test
    public void test_address(){
        onView(withId(R.id.eventLocation)).check(matches(withText(containsString("Ecublens"))));
    }

    @Test
    public void test_desc(){
        onView(withId(R.id.eventDescription)).check(matches(withText(containsString("Rock concert"))));
    }

    @Test
    public void test_owner(){
        onView(withId(R.id.eventOwner)).check(matches(withText(containsString(event.getEmail()))));
    }
}