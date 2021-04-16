package com.ncnf.friends;

import android.view.KeyEvent;
import android.widget.EditText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ncnf.R;
import com.ncnf.friends.ui.FriendsActivity;
import com.ncnf.utilities.FirestoreModule;
import com.ncnf.utilities.RecyclerViewItemCountAssertion;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules(FirestoreModule.class)
public class FriendsActivityTest {

    public static FirebaseFirestore mockDatabaseReference = Mockito.mock(FirebaseFirestore.class);
    public static CollectionReference mockDbRef = Mockito.mock(CollectionReference.class, Mockito.RETURNS_DEEP_STUBS);
    public static Query mockQuery = Mockito.mock(Query.class);

    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(FriendsActivity.class);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public FirebaseFirestore databaseReference = mockDatabaseReference;


    @BeforeClass
    public static void setup() {
        when(mockDatabaseReference.collection(anyString())).thenReturn(mockDbRef);
    }

    @Test
    public void friendsPageViewerTest(){
        onView(withId(R.id.friends_view_pager)).perform(swipeLeft());
        onView(withId(R.id.friends_view_pager)).perform(swipeRight());
    }

//    @Test
//    public void searchingUserCreatesRecyclerView() throws InterruptedException {
//        onView(withId(R.id.friends_view_pager)).perform(swipeLeft());
//
//        onView(withId(R.id.add_friend_search_bar)).perform(click());
//        onView(withClassName(containsString(EditText.class.getSimpleName()))).perform(typeText("Gab2"));
//        onView(withId(R.id.add_friend_search_bar)).perform(pressKey(KeyEvent.KEYCODE_ENTER));
//
//        Thread.sleep(5000);
//
//        onView(withId(R.id.add_friend_recycler_view)).check(new RecyclerViewItemCountAssertion(1));
//    }
//
//
//    @Test
//    public void searchingUserThatExistsWorks() {
//
//        when(mockDbRef.orderBy(anyString()).startAt(anyString()).endAt(anyString())).thenReturn(mockQuery);
//
//        onView(withId(R.id.user_search)).perform(typeText("Gab 2"));
//        onView(withId(R.id.user_search)).perform(pressKey(KeyEvent.KEYCODE_ENTER));
//        onView(withId(R.id.user_search)).perform(pressImeActionButton());
//        onView(withId(R.id.add_friend_recycler_view)).check(matches(isDisplayed()));
//    }

}



