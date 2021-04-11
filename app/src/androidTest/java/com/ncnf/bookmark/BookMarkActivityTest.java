package com.ncnf.bookmark;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;
import androidx.viewpager2.widget.ViewPager2;

import com.ncnf.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertTrue;

@HiltAndroidTest
public class BookMarkActivityTest {

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Rule
    public ActivityTestRule<BookMarkActivity> activityRule = new ActivityTestRule<>(BookMarkActivity.class);
    private BookMarkActivity bookMarkActivity;

    @Before
    public void setup(){
        bookMarkActivity = activityRule.getActivity();
        assertThat(bookMarkActivity, notNullValue());
    }

    @Test
    public void eventFormValidatesEmptyInput() {

        ViewPager2 viewPager = activityRule.getActivity().findViewById(R.id.view_pager);
        assertTrue(viewPager.getCurrentItem() == 0);
        onView(withId(R.id.view_pager)).perform(swipeLeft());
        assertTrue(viewPager.getCurrentItem() == 1);
        onView(withId(R.id.view_pager)).perform(swipeRight());
        assertTrue(viewPager.getCurrentItem() == 0);
    }
}
