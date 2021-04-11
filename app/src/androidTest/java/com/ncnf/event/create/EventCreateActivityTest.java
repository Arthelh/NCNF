package com.ncnf.event.create;

import android.content.Intent;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.R;
import com.ncnf.database.DatabaseResponse;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;
import com.ncnf.main.MainActivity;
import com.ncnf.user.CurrentUserModule;
import com.ncnf.user.PrivateUser;

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
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class EventCreateActivityTest {

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private static final PrivateUser mockUser = Mockito.mock(PrivateUser.class);


    @BindValue
    public PrivateUser user = mockUser;

    CompletableFuture response = CompletableFuture.completedFuture(CompletableFuture.completedFuture(new DatabaseResponse(true, false, null)));



    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(EventCreateActivity.class));

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
        Matcher emailField = withId(R.id.event_email_contact);

        onView(withId(R.id.event_create_button)).perform(scrollTo(), click());

        onView(emailField).perform(click(), closeSoftKeyboard()).check(matches(hasErrorText("This field cannot be empty")));
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
        when(user.getID()).thenReturn("ownerId");
        when(user.createEvent(anyObject())).thenReturn(response);

        onView(withId(R.id.event_name)).perform(scrollTo(), replaceText("Conference"));
        onView(withId(R.id.event_description)).perform(scrollTo(), replaceText("Math are fun!"));
        onView(withId(R.id.event_address)).perform(scrollTo(), replaceText("INM201"));
        onView(withId(R.id.event_price)).perform(scrollTo(), replaceText("10"));
        onView(withId(R.id.event_website)).perform(scrollTo(), replaceText("bar.com"));
        onView(withId(R.id.event_phone_contact)).perform(scrollTo(), replaceText("0771112233"));
        onView(withId(R.id.event_email_contact)).perform(scrollTo(), replaceText("foo@bar.com"));
        // Spinner
        onView(withId(R.id.event_creation_spinner)).perform(scrollTo(), click());
        onData(allOf(is(instanceOf(Event.Type.class)), is(Event.Type.Movie))).perform(click());
        // Date
        onView(withId(R.id.event_create_display_date)).perform(scrollTo(), click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 3, 16));
        onView(withId(android.R.id.button1)).perform(click()); // click OK
        // Time
        onView(withId(R.id.event_create_display_time)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(20, 0));
        onView(withId(android.R.id.button1)).perform(click()); // click OK

        onView(withId(R.id.event_create_button)).perform(scrollTo());
        onView(withId(R.id.event_create_button)).perform(click());

        verify(user).getID();
        verify(user).createEvent(anyObject());
        Intents.intended(hasComponent(MainActivity.class.getName()));

    }

}
