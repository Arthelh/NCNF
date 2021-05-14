package com.ncnf.socialObject;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.Date;
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
@RunWith(AndroidJUnit4.class)
public class SocialObjectActivityTest {

    private final SocialObject event = new Event("hg0aavb0Fce6EgX3HrTW05rKSww1", "TestGeo", new Date(2021, 05, 04), new GeoPoint(46.518689, 6.568067), "Ecublens", "TestGeo", Event.Type.Conference, 0, 0, "beltan@test.com");
    private final CompletableFuture<SocialObject> eventC = new CompletableFuture<>();

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(MainActivity.class));

    @Before
    public void setup(){
        onView(withId(R.id.navigation_feed)).perform(click());
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @Test
    public void test_name(){
        onView(withId(R.id.eventName)).check(matches(withText(containsString(event.getName()))));
    }

    @Test
    public void test_address(){
        onView(withId(R.id.eventLocation)).check(matches(withText(containsString(event.getAddress()))));
    }

    @Test
    public void test_desc(){
        onView(withId(R.id.eventDescription)).check(matches(withText(containsString(event.getDescription()))));
    }

    @Test
    public void test_owner(){
        Event e = (Event) event;
        onView(withId(R.id.eventOwner)).check(matches(withText(containsString(e.getEmail()))));
    }
}