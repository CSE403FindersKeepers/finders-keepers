package cse403.finderskeepers;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Tests the ability to reach the view trades screen
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ViewTradesTest {

    @Rule
    public ActivityTestRule<SignInActivity> mActivityTestRule = new ActivityTestRule<>(SignInActivity.class);

    @Test
    public void viewTradesTest() {
        //Click the google sign in button
        ViewInteraction rc = onView(
                allOf(withText("Sign in"),
                        withParent(allOf(withId(R.id.sign_in_button),
                                withParent(withId(R.id.content_sign_in)))),
                        isDisplayed()));
        rc.perform(click());

        //select menu
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        //select view trades
        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("View Trades"), isDisplayed()));
        appCompatTextView.perform(click());

    }

}
