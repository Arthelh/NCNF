//package com.ncnf.bookmark;
//
//import androidx.test.espresso.intent.Intents;
//import androidx.test.ext.junit.rules.ActivityScenarioRule;
//
//import com.google.firebase.firestore.GeoPoint;
//import com.ncnf.R;
//import com.ncnf.socialObject.Event;
//import com.ncnf.socialObject.Group;
//import com.ncnf.socialObject.SocialObject;
//import com.ncnf.user.CurrentUserModule;
//import com.ncnf.user.User;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.RuleChain;
//import org.mockito.Mockito;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//import dagger.hilt.android.testing.BindValue;
//import dagger.hilt.android.testing.HiltAndroidRule;
//import dagger.hilt.android.testing.HiltAndroidTest;
//import dagger.hilt.android.testing.UninstallModules;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.swipeLeft;
//import static androidx.test.espresso.action.ViewActions.swipeRight;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static org.mockito.Mockito.when;
//
//@HiltAndroidTest
//@UninstallModules(CurrentUserModule.class)
//public class BookMarkActivityTest {
//
//    private static final User mockUser = Mockito.mock(User.class);
//
//    List<Event> events = new ArrayList<>();
//    private final Event event = new Event("EPFL", "EPFL event", new Date(2021, 03, 11), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference, 0, 0, "test@email.com");
//    private CompletableFuture<List<Event>> eventsFuture;
//
//    List<Group> groups = new ArrayList<>();
//    private final Group group = new Group("EPFL", "EPFL event", new Date(2021, 03, 11), new GeoPoint(46.518689, 6.568067), "Rolex Learning Center, 1015 Ecublens", "SocialObject description goes here", SocialObject.Type.Conference);
//    private CompletableFuture<List<Group>> groupsFuture;
//
//    @BindValue
//    public User user = mockUser;
//
//    private HiltAndroidRule hiltRule = new HiltAndroidRule(this);
//    @Rule
//    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(new ActivityScenarioRule<>(BookMarkActivity.class));
//
//    @Before
//    public void setup(){
//        for(int i = 0; i < 8; ++i){
//            events.add(event);
//            groups.add(group);
//        }
//        eventsFuture = CompletableFuture.completedFuture(events);
//        when(mockUser.getSavedEvents()).thenReturn(eventsFuture);
//
//        groupsFuture = CompletableFuture.completedFuture(groups);
//        when(mockUser.getParticipatingGroups()).thenReturn(groupsFuture);
//
//        Intents.init();
//    }
//
//    @After
//    public void cleanup(){
//        Intents.release();
//    }
//
//    @Test
//    public void eventFormValidatesEmptyInput() throws InterruptedException {
//        onView(withId(R.id.bookmark_view_pager)).perform(swipeLeft());
//        onView(withId(R.id.bookmark_view_pager)).perform(swipeRight());
//
//        Thread.sleep(5000);
//
//        onView(withId(R.id.bookmark_view_pager)).perform(click());
//        onView(withId(R.id.EventPage)).check(matches(isDisplayed()));
//        //onView(withId(R.id.eventName)).check(matches(withText(contains(event.getName()))));
//    }
//}
