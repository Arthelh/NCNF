package com.ncnf.socialObject.create;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.main.MainActivity;
import com.ncnf.user.CurrentUserModule;
import com.ncnf.user.User;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class SocialObjectCreateActivityTest {

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private static final User mockUser = Mockito.mock(User.class);

    @BindValue
    public User user = mockUser;

    CompletableFuture<Boolean> response = CompletableFuture.completedFuture(true);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(GroupCreateActivity.class));

    @Before
    public void setup(){
        hiltRule.inject();
        Intents.init();
    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void eventFormValidatesEmptyInput() {
        Matcher emailField = withId(R.id.set_contact_email);

        onView(withId(R.id.validate_event)).perform(scrollTo(), click());

        onView(emailField).perform(click(), closeSoftKeyboard()).check(matches(hasErrorText("This field cannot be empty")));
    }

    @Test
    public void eventFormValidatesWrongInput() {
        Matcher emailField = withId(R.id.set_contact_email);

        onView(withId(R.id.set_event_name)).perform(scrollTo(), replaceText("Conference"));
        onView(withId(R.id.set_event_description)).perform(scrollTo(), replaceText("Math are fun!"));
        onView(withId(R.id.set_event_address)).perform(scrollTo(), replaceText("Empire State Building"));
        // Spinner
        onView(withId(R.id.select_event_type)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(String.class)), is("Movie"))).perform(click());

        onView(emailField).perform(scrollTo(), replaceText("foo"));
        onView(withId(R.id.validate_event)).perform(scrollTo());
        onView(withId(R.id.validate_event)).perform(click());

        onView(emailField).perform(scrollTo(), click(), closeSoftKeyboard()).check(matches(hasErrorText("Please enter a correct email address.")));
    }

    @Test
    public void eventFormValidatesCorrectInput() {
        when(user.getUuid()).thenReturn("ownerId");
        when(user.createGroup(anyObject())).thenReturn(response);

        onView(withId(R.id.set_event_name)).perform(scrollTo(), replaceText("Conference"));
        onView(withId(R.id.set_event_description)).perform(scrollTo(), replaceText("Math are fun!"));
        onView(withId(R.id.set_event_address)).perform(scrollTo(), replaceText("Empire State Building"));
        onView(withId(R.id.set_event_price)).perform(scrollTo(), replaceText("10"));
        onView(withId(R.id.set_min_age)).perform(scrollTo(), replaceText("18"));
        onView(withId(R.id.set_contact_email)).perform(scrollTo(), replaceText("foo@bar.com"));
        // Spinner
        onView(withId(R.id.select_event_type)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(String.class)), is("Movie"))).perform(click());
        // Date
        onView(withId(R.id.set_event_date_button)).perform(scrollTo(), click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 3, 16));
        onView(withId(android.R.id.button1)).perform(click()); // click OK
        // Time
        onView(withId(R.id.set_event_time_button)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(20, 0));
        onView(withId(android.R.id.button1)).perform(click()); // click OK


        onView(withId(R.id.validate_event)).perform(scrollTo());
        onView(withId(R.id.validate_event)).perform(click());

        verify(user).getUuid();
        verify(user).createGroup(anyObject());
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testEmailAutoComplete() {
        when(user.getEmail()).thenReturn("user@email");

        onView(withId(R.id.use_personal_email_checkbox)).perform(scrollTo(), click());
        onView(withId(R.id.set_contact_email)).check(matches(withText("user@email")));

    }

}
