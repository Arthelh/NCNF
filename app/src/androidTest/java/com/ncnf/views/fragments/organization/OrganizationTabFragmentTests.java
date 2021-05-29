package com.ncnf.views.fragments.organization;

import android.widget.Button;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.authentication.firebase.CurrentUserModule;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.helpers.RecyclerViewItemCountAssertion;
import com.ncnf.models.Organization;
import com.ncnf.models.User;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.views.activities.organization.OrganizationProfileActivity;
import com.ncnf.views.activities.user.UserTabActivity;

import org.junit.Before;
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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class OrganizationTabFragmentTests {

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(UserTabActivity.class);

    private final FirebaseDatabase firebaseDatabase = Mockito.mock(FirebaseDatabase.class);
    private final User u1 = new User(firebaseDatabase, "u1", "johnny", "johnny@bar.com", "John Smith", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, LocalDate.now(), null);
    private final Organization o1 = new Organization(UUID.randomUUID(), "EPFL", new GeoPoint(0, 0), "Ecublens", "ncnf@epfl.ch", "08008008080", Collections.singletonList("u1"), new ArrayList<>());
    private final List<Organization> organizations = Collections.singletonList(o1);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public User user = Mockito.mock(User.class);

    @BindValue
    public OrganizationRepository organizationRepository = Mockito.mock(OrganizationRepository.class);

    @Before
    public void setup() {
        when(user.loadUserFromDB()).thenReturn(CompletableFuture.completedFuture(u1));
        when(user.getUuid()).thenReturn(u1.getUuid());
    }

    @Test
    public void noOrganization() {
        when(organizationRepository.getUserOrganizations(anyString())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));

        onView(withId(R.id.user_view_pager)).perform(swipeLeft());

        onView(withId(R.id.empty_organization_view)).check(matches(isDisplayed()));
        onView(withId(R.id.organization_list_recyclerview)).check(matches(not(isDisplayed())));
    }

    @Test
    public void organizationIsDisplay() {
        when(organizationRepository.getUserOrganizations(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));

        onView(withId(R.id.user_view_pager)).perform(swipeLeft());
        onView(withId(R.id.empty_organization_view)).check(matches(not(isDisplayed())));
        onView(withId(R.id.organization_list_recyclerview)).check(matches(isDisplayed()));

    }

    @Test
    public void clickOnOrganization() throws InterruptedException {
        Intents.init();

        when(organizationRepository.getUserOrganizations(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));
        when(organizationRepository.getByUUID(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));

        onView(withId(R.id.user_view_pager)).perform(swipeLeft());
        Thread.sleep(2000); // necessary because of the swipe
        onView(withText("EPFL")).perform(click());
        Intents.intended(hasComponent(OrganizationProfileActivity.class.getName()));

        Intents.release();
    }

    @Test
    public void addValidOrganization() throws InterruptedException {
        when(organizationRepository.getUserOrganizations(anyString())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        when(organizationRepository.getOrganizationsWithToken(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));
        when(organizationRepository.addUserToOrganization(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(true));

        onView(withId(R.id.user_view_pager)).perform(swipeLeft());
        Thread.sleep(2000);
        onView(withId(R.id.add_organization_button)).perform(click());
        onView(withClassName(endsWith("EditText"))).perform(typeText("token"), closeSoftKeyboard());
        onView(withText("Enter")).perform(click());

        onView(withId(R.id.organization_list_recyclerview)).check(new RecyclerViewItemCountAssertion(1));
    }

    @Test
    public void typeEmptyString() throws InterruptedException {
        when(organizationRepository.getUserOrganizations(anyString())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));

        onView(withId(R.id.user_view_pager)).perform(swipeLeft());
        Thread.sleep(2000);
        onView(withId(R.id.add_organization_button)).perform(click());
        onView(withText("Enter")).perform(click());
        onView(withClassName(endsWith("EditText"))).check(matches(hasErrorText("Token cannot be empty")));
    }

    @Test
    public void addInvalidOrganization() throws InterruptedException {
        when(organizationRepository.getUserOrganizations(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));
        CompletableFuture future = new CompletableFuture();
        future.completeExceptionally(new Exception("No organization found"));
        when(organizationRepository.getOrganizationsWithToken(anyString())).thenReturn(future);

        onView(withId(R.id.user_view_pager)).perform(swipeLeft());
        Thread.sleep(2000);
        onView(withId(R.id.add_organization_button)).perform(click());

        onView(withClassName(endsWith("EditText"))).perform(typeText("wrong token"), closeSoftKeyboard());
        onView(withText("Enter")).perform(click());
        onView(withId(R.id.popup_invalid_organization_text)).check(matches(withText("No organization found")));
    }
}
