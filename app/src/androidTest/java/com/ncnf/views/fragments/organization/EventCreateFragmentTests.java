package com.ncnf.views.fragments.organization;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.authentication.firebase.FirebaseUserModule;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;
import com.ncnf.repositories.EventRepository;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.views.activities.main.MainActivity;
import com.ncnf.storage.firebase.FirebaseFileStore;
import com.ncnf.models.User;
import com.ncnf.authentication.firebase.CurrentUserModule;
import com.ncnf.views.activities.organization.OrganizationProfileActivity;
import com.ncnf.views.activities.user.UserTabActivity;
import com.ncnf.views.fragments.organization.EventCreateFragment;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.google.android.material.internal.ContextUtils.getActivity;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules({FirebaseUserModule.class})
public class EventCreateFragmentTests {

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private ActivityScenarioRule<OrganizationProfileActivity> scenarioRule = new ActivityScenarioRule<>(OrganizationProfileActivity.class);

    private final Organization o1 = new Organization(UUID.randomUUID(), "EPFL", new GeoPoint(0, 0), "Ecublens", "ncnf@epfl.ch", "08008008080", Collections.singletonList("u1"), new ArrayList<>());
    private final List<Organization> organizations = Collections.singletonList(o1);

    @BindValue
    public FirebaseUser user = Mockito.mock(FirebaseUser.class);

    @BindValue
    public OrganizationRepository organizationRepository = Mockito.mock(OrganizationRepository.class);

    @BindValue
    public EventRepository eventRepository = Mockito.mock(EventRepository.class);

    @BindValue
    public FirebaseFileStore firebaseFileStore = Mockito.mock(FirebaseFileStore.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenarioRule);

    @Before
    public void setup() {
        when(organizationRepository.getByUUID(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));
        when(user.getUid()).thenReturn("u1");
        when(user.getEmail()).thenReturn("johnny@bar.com");

        onView(withId(R.id.organization_view_pager)).perform(swipeLeft());
        onView(withId(R.id.add_event_organization_button)).perform(click());
    }

    @Test
    public void eventFormValidatesEmptyInput() {
        onView(withId(R.id.validate_event)).perform(scrollTo(), click());

        onView(withId(R.id.set_contact_email)).perform(click(), closeSoftKeyboard()).check(matches(hasErrorText("This field cannot be empty")));
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
        when(firebaseFileStore.uploadImage(any(Bitmap.class))).thenReturn(CompletableFuture.completedFuture(true));
        when(eventRepository.storeEvent(any(Event.class))).thenReturn(CompletableFuture.completedFuture(true));
        when(organizationRepository.addEventToOrganization(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

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

        verify(eventRepository).storeEvent(any(Event.class));
    }

    @Test
    public void testEmailAutoComplete() {
        onView(withId(R.id.use_personal_email_checkbox)).perform(scrollTo(), click());
        onView(withId(R.id.set_contact_email)).check(matches(withText("johnny@bar.com")));
    }

}
