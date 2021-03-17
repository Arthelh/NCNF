package com.ncnf.event.create;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.event.EventType;
import com.ncnf.main.MainActivity;
import com.ncnf.map.MapActivity;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class EventCreateActivityTest {

    @Rule
    public ActivityScenarioRule rule = new ActivityScenarioRule<>(EventCreateActivity.class);

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
        Intents.init();

        onView(withId(R.id.event_name)).perform(scrollTo(), replaceText("Conference"));
        onView(withId(R.id.event_description)).perform(scrollTo(), replaceText("Math are fun!"));
        onView(withId(R.id.event_address)).perform(scrollTo(), replaceText("INM201"));
        onView(withId(R.id.event_price)).perform(scrollTo(), replaceText("10"));
        onView(withId(R.id.event_website)).perform(scrollTo(), replaceText("bar.com"));
        onView(withId(R.id.event_phone_contact)).perform(scrollTo(), replaceText("0771112233"));
        onView(withId(R.id.event_email_contact)).perform(scrollTo(), replaceText("foo@bar.com"));
        onView(withId(R.id.event_creation_spinner)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(EventType.class)), is(EventType.Movie))).perform(click());

        onView(withId(R.id.event_create_button)).perform(scrollTo(), click());

        Intents.intended(hasComponent(MainActivity.class.getName()));

        Intents.release();
    }

}
