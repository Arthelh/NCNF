package com.ncnf.settings;

import android.view.View;
import android.widget.DatePicker;
import android.widget.SeekBar;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.settings.ui.SettingsActivity;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.time.LocalDate;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

@HiltAndroidTest
public class SettingsActivityTest {

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule activityTestRule = new ActivityScenarioRule<>(SettingsActivity.class);

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
    public void testSlider(){
        int progress = 49;
        int adjusted_progress = progress + 1;
        onView(ViewMatchers.withId(R.id.distanceSeekBar)).perform(setProgress(progress));
        onView(withId(R.id.distanceTextView)).check(matches(withText(containsString(Integer.toString(adjusted_progress)))));
    }

    @Test
    public void testToggle(){
        onView(withId(R.id.settingsToggleToday)).perform(click());
        String today = LocalDate.now().toString();
        onView(withId(R.id.settingsMinDateView)).check(matches(withText(containsString(today))));
        onView(withId(R.id.settingsMaxDateView)).check(matches(withText(containsString(today))));

        onView(withId(R.id.settingsToggleTomorrow)).perform(click());
        String tomorrow = LocalDate.now().plusDays(1).toString();
        onView(withId(R.id.settingsMinDateView)).check(matches(withText(containsString(tomorrow))));
        onView(withId(R.id.settingsMaxDateView)).check(matches(withText(containsString(tomorrow))));

        onView(withId(R.id.settingsToggleWeek)).perform(click());
        String nextWeek = LocalDate.now().plusDays(7).toString();
        onView(withId(R.id.settingsMinDateView)).check(matches(withText(containsString(today))));
        onView(withId(R.id.settingsMaxDateView)).check(matches(withText(containsString(nextWeek))));
    }

    @Test
    public void testDateSetters(){
        String date1 = LocalDate.of(2021, 6, 16).toString();
        String date2 = LocalDate.of(2021, 5, 16).toString();

        onView(withId(R.id.settingsMinDateButton)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2021, 6, 16));
        onView(withId(android.R.id.button1)).perform(click()); // click OK
        onView(withId(R.id.settingsMinDateView)).check(matches(withText(containsString(date1))));

        onView(withId(R.id.settingsMaxDateButton)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2021, 5, 16));
        onView(withId(android.R.id.button1)).perform(click()); // click OK
        onView(withId(R.id.settingsMaxDateView)).check(matches(withText(containsString(date2))));
        onView(withId(R.id.settingsMinDateView)).check(matches(withText(containsString(date2))));
    }

    @Test
    public void testDiscard(){
        int initial = Settings.getCurrentMaxDistance();
        int progress = 49;
        onView(withId(R.id.distanceSeekBar)).perform(setProgress(progress));
        onView(withId(R.id.discardButton)).perform(click());

        //ViewMatchers.assertThat(Settings.getCurrentMaxDistance(), is(initial));
        assertEquals(Settings.getCurrentMaxDistance(), initial);
    }

    @Test
    public void testAccept(){
        int progress = 49;
        int adjusted_progress = progress + 1;
        onView(withId(R.id.distanceSeekBar)).perform(setProgress(progress));
        onView(withId(R.id.validateButton)).perform(click());
        
        ViewMatchers.assertThat(Settings.getCurrentMaxDistance(), is(adjusted_progress));
    }


}
