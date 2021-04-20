package com.ncnf.bookmark;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.PublicEvent;
import com.ncnf.event.EventActivity;
import com.ncnf.user.CurrentUserModule;
import com.ncnf.user.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(CurrentUserModule.class)
public class BookMarkActivityTest {

    private static final User mockUser = Mockito.mock(User.class);
    List<Event> list = new ArrayList<>();
    private final Event event = new PublicEvent("EPFL", "EPFL event", new Date(2021, 03, 11), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "Event description goes here", Event.Type.Conference, 0, 0, "test@email.com");
    private CompletableFuture<CompletableFuture<List<Event>>> events;

    @BindValue
    public User user = mockUser;

    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(BookMarkActivity.class));

    @Before
    public void setup(){
        for(int i = 0; i < 8; ++i){
            list.add(event);
        }
        events =  CompletableFuture.completedFuture(CompletableFuture.completedFuture(list));
        when(user.getAllEvents(anyString())).thenReturn(events);
        Intents.init();
    }

    @After
    public void cleanup(){
        Intents.release();
    }

    @Test
    public void eventFormValidatesEmptyInput() throws InterruptedException {
        onView(withId(R.id.bookmark_view_pager)).perform(swipeLeft());
        onView(withId(R.id.bookmark_view_pager)).perform(swipeRight());

        //Wait to be sure that events have been loaded
        Thread.sleep(5000);

        onView(withId(R.id.bookmark_view_pager)).perform(click());
        Intents.intended(hasComponent(EventActivity.class.getName()));
    }
}
