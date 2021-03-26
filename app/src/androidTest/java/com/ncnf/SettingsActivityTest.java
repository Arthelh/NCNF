package com.ncnf;

import android.view.View;
import android.widget.SeekBar;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.settings.Settings;
import com.ncnf.settings.SettingsActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@HiltAndroidTest
public class SettingsActivityTest {

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private ActivityScenarioRule activityTestRule = new ActivityScenarioRule<>(SettingsActivity.class);

    private static ViewAction setProgress(final int progress) { return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SeekBar.class);
            }

            @Override
            public String getDescription() {
                return "Change progress on SideBar";
            }

            @Override
            public void perform(UiController uiController, View view) {
                SeekBar seekBar = (SeekBar) view;
                seekBar.setProgress(progress);
            }
        };
    }

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(activityTestRule);

    @Test
    public void test_slider(){
        int progress = 49;
        int adjusted_progress = progress + 1;
        onView(withId(R.id.distanceSeekBar)).perform(setProgress(progress));
        onView(withId(R.id.distanceTextView)).check(matches(withText(containsString(Integer.toString(adjusted_progress)))));
    }

    @Test
    public void test_discard(){
        int initial = Settings.getCurrent_max_distance();
        int progress = 49;
        onView(withId(R.id.distanceSeekBar)).perform(setProgress(progress));
        onView(withId(R.id.discardButton)).perform(click());

        assertThat(Settings.getCurrent_max_distance(), is(initial));
    }

    @Test
    public void test_accept(){
        int progress = 49;
        int adjusted_progress = progress + 1;
        onView(withId(R.id.distanceSeekBar)).perform(setProgress(progress));
        onView(withId(R.id.validateButton)).perform(click());

        assertThat(Settings.getCurrent_max_distance(), is(adjusted_progress));
    }


}
