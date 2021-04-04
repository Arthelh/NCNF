package com.ncnf.event.create;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.event.EventType;
import com.ncnf.main.MainActivity;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@HiltAndroidTest
public class EventCreateActivityTest {

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(EventCreateActivity.class));

    @Before
    public void setup(){
        Intents.init();
    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void eventFormValidatesEmptyInput() {
        Matcher emailField = withId(R.id.event_email_contact);

        onView(withId(R.id.event_create_button)).perform(scrollTo(), click());

        onView(emailField).check(matches(hasErrorText("This field cannot be empty")));
    }

    @Test
    public void eventFormValidatesWrongInput() {
        Matcher emailField = withId(R.id.event_email_contact);

        onView(emailField).perform(scrollTo(), replaceText("foo"));
        onView(withId(R.id.event_create_button)).perform(scrollTo(), click());

        onView(emailField).check(matches(hasErrorText("Please enter a correct email address")));
    }

    @Test
    public void eventFormValidatesCorrectInput() {
        onView(withId(R.id.event_name)).perform(scrollTo(), replaceText("Conference"));
        onView(withId(R.id.event_description)).perform(scrollTo(), replaceText("Math are fun!"));
        onView(withId(R.id.event_address)).perform(scrollTo(), replaceText("INM201"));
        onView(withId(R.id.event_price)).perform(scrollTo(), replaceText("10"));
        onView(withId(R.id.event_website)).perform(scrollTo(), replaceText("bar.com"));
        onView(withId(R.id.event_phone_contact)).perform(scrollTo(), replaceText("0771112233"));
        onView(withId(R.id.event_email_contact)).perform(scrollTo(), replaceText("foo@bar.com"));
        // Spinner
        onView(withId(R.id.event_creation_spinner)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(EventType.class)), is(EventType.Movie))).perform(click());
        // Date
        onView(withId(R.id.event_create_display_date)).perform(scrollTo(), click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 3, 16));
        onView(withId(android.R.id.button1)).perform(click()); // click OK
        // Time
        onView(withId(R.id.event_create_display_time)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(20, 0));
        onView(withId(android.R.id.button1)).perform(click()); // click OK

        onView(withId(R.id.event_create_button)).perform(scrollTo(), click());

        Intents.intended(hasComponent(MainActivity.class.getName()));
    }

}
