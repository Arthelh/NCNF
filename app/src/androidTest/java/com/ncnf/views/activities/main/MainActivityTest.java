package com.ncnf.views.activities.main;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.ncnf.views.activities.main.MainActivity;

import org.junit.Rule;
import org.junit.rules.RuleChain;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public final class MainActivityTest {

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule activityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(activityTestRule);

}
