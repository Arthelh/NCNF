package com.ncnf.friends;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ncnf.R;
import com.ncnf.database.DatabaseService;
import com.ncnf.friends.ui.FriendsActivity;
import com.ncnf.repositories.UsersRepository;
import com.ncnf.user.CurrentUserModule;
import com.ncnf.user.User;
import com.ncnf.utilities.FirestoreModule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.testing.BindValue;
import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.UninstallModules;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@HiltAndroidTest
@UninstallModules({FirestoreModule.class, CurrentUserModule.class})
public class FriendsActivityTest {

    public static FirebaseFirestore mockDatabaseReference = Mockito.mock(FirebaseFirestore.class);
    public static CollectionReference mockDbRef = Mockito.mock(CollectionReference.class, Mockito.RETURNS_DEEP_STUBS);
    public static Query mockQuery = Mockito.mock(Query.class);
    private static final User mockUser = Mockito.mock(User.class);
    private static final UsersRepository mockUserRepo = Mockito.mock(UsersRepository.class);
    private final HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private final ActivityScenarioRule scenario = new ActivityScenarioRule<>(FriendsActivity.class);
    private static final DatabaseService db = Mockito.mock(DatabaseService.class);

    private User u1 = new User(db,"1234567890", "", "foo@bar.com","",  "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, null);

    @Rule
    public RuleChain testRule = RuleChain.outerRule(hiltRule).around(scenario);

    @BindValue
    public FirebaseFirestore databaseReference = mockDatabaseReference;

    @BindValue
    public User user = mockUser;

    @BindValue
    public UsersRepository usersRepository = mockUserRepo;

    @BeforeClass
    public static void setup() {
        when(mockDatabaseReference.collection(anyString())).thenReturn(mockDbRef);
    }

    @Test
    public void friendsPageViewerTest(){
        CompletableFuture<User> futureUser = new CompletableFuture<>();
        futureUser.complete(mockUser);
        CompletableFuture<List<User>> futureFriends = new CompletableFuture<>();
        futureFriends.complete(Collections.singletonList(u1));

        when(mockUser.loadUserFromDB()).thenReturn(futureUser);
        when(usersRepository.loadUser(anyString())).thenReturn(futureUser);
        when(mockUser.getFriends()).thenReturn(futureFriends);
        when(mockUser.getAllUsersLike(anyString())).thenReturn(futureFriends);
        when(mockUser.getUuid()).thenReturn("");

        onView(withId(R.id.friends_switch_button)).perform(click());
//        onView(withId(R.id.friends_switch_button)).perform(click());

//        onView(withId(R.id.friends_view_pager)).perform(swipeLeft());
//
//        onView(withId(R.id.friends_view_pager)).perform(swipeRight());
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



