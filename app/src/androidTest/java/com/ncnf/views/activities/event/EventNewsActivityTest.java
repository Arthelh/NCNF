package com.ncnf.views.activities.event;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.FirebaseException;
import com.ncnf.R;
import com.ncnf.database.firebase.FirebaseDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
public class EventNewsActivityTest {

    private final Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventNewsActivity.class);

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(intent);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    FirebaseDatabase db = Mockito.mock(FirebaseDatabase.class);

    @Before
    public void setup() {
        intent.putExtra("uuid", "fake");
    }

    @Test
    public void publishWithoutFillingText() {
        onView(withId(R.id.event_news_publish_button)).perform(click());

        onView(withId(R.id.event_news_field)).check(matches(hasErrorText("This field cannot be empty")));
    }

    @Test
    public void successfullyPublishNews() {
        CompletableFuture<Boolean> future = CompletableFuture.completedFuture(true);
        when(db.updateArrayField(anyString(), anyString(), anyObject())).thenReturn(future);

        onView(withId(R.id.event_news_field)).perform(typeText("The event starts soon !"), closeSoftKeyboard());
        onView(withId(R.id.event_news_publish_button)).perform(click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("News published !")));
    }

    @Test
    public void unsuccessfullyPublishNews() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.completeExceptionally(new FirebaseException("Error connecting to firebase"));
        when(db.updateArrayField(anyString(), anyString(), anyObject())).thenReturn(future);

        onView(withId(R.id.event_news_field)).perform(typeText("The event starts soon !"), closeSoftKeyboard());
        onView(withId(R.id.event_news_publish_button)).perform(click());

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Could not publish the news ! Try again later.")));
    }

}
