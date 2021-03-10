import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ncnf.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ncnf.feed.EventActivity;
import ncnf.feed.FeedActivity;
import ncnf.main.DisplayMessageActivity;
import ncnf.main.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class FeedActivityTests {

    @Rule
    public ActivityScenarioRule<FeedActivity> testRule =
            new ActivityScenarioRule<>(FeedActivity.class);

    @Test
    public void eventActivityOpens(){
        Intents.init();

        String test_string = "This is a test";
        onView(withId(R.id.event)).perform(click());
        Intents.intended(hasComponent(EventActivity.class.getName()));


        Intents.release();
    }
}
