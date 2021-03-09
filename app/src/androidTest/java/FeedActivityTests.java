import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ncnf.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ncnf.feed.FeedActivity;
import ncnf.main.MainActivity;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class FeedActivityTests {

    @Rule
    public ActivityScenarioRule<MainActivity> testRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public ActivityScenarioRule<FeedActivity> testActivityRule =
            new ActivityScenarioRule<>(FeedActivity.class);

    @Test
    public void feedActivityButtonWorks(){
        Espresso.onView(ViewMatchers.withId(R.id.feed_view_button)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.feed_activity)).check(matches(isDisplayed()));
    }
}
