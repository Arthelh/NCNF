package com.example.bootcamp;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.ncnf.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;
import org.junit.runner.RunWith;

import ncnf.main.DisplayMessageActivity;
import ncnf.main.MainActivity;

@RunWith(AndroidJUnit4.class)
public class DisplayMessageActivityTest {



    @Test
    public void test_text(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DisplayMessageActivity.class);
        intent.putExtra(MainActivity.EXTRA_MESSAGE, "this is a test");
        try (ActivityScenario<DisplayMessageActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.name_display)).check(matches(withText(containsString("this is a test"))));
        }
    }
}
