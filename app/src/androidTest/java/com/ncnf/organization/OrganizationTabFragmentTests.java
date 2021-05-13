package com.ncnf.organization;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.database.DatabaseService;
import com.ncnf.user.User;
import com.ncnf.user.UserTabActivity;
import com.ncnf.user.helpers.CurrentUserModule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

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
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class OrganizationTabFragmentTests {

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(UserTabActivity.class);

    private final DatabaseService databaseService = Mockito.mock(DatabaseService.class);
    private final User u1 = new User(databaseService, "u1", "johnny", "johnny@bar.com", "John", "Smith", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null);
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

    //@Test
    public void clickOnOrganization() {
        when(organizationRepository.getUserOrganizations(anyString())).thenReturn(CompletableFuture.completedFuture(organizations));

        onView(withId(R.id.user_view_pager)).perform(swipeLeft());

        onView(withId(R.id.organization_list_recyclerview)).perform(RecyclerViewActions.actionOnItem(
                hasDescendant(withText("EPFL")), click()
        ));

        // TODO
        // onView(withId(R.id.organization_display_email)).check(matches(withText("johnny@bar.com")));
    }

    @Test
    public void addValidOrganization() {
        when(organizationRepository.getUserOrganizations(anyString())).thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));

        onView(withId(R.id.user_view_pager)).perform(swipeLeft());
        onView(withId(R.id.add_organization_button)).perform(click());

        // TODO
    }

    @Test
    public void addInvalidOrganization() {
        // TODO
    }

}
