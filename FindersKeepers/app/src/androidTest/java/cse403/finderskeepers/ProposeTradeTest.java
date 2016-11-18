package cse403.finderskeepers;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ProposeTradeTest {

    @Rule
    public ActivityTestRule<SignInActivity> zActivityTestRule = new ActivityTestRule<>(SignInActivity.class);




    @Test
    public void browseUsersTest() {
        ViewInteraction rc = onView(
                allOf(withText("Sign in"),
                        withParent(allOf(withId(R.id.sign_in_button),
                                withParent(withId(R.id.content_sign_in)))),
                        isDisplayed()));
        rc.perform(click());
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.propose_trade), withText("Propose Trade"),
                        withParent(withId(R.id.content_other_user_page)),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.send_trade), withText("Send Trade Request"),
                        withParent(withId(R.id.content_propose_trade)),
                        isDisplayed()));
        appCompatButton2.perform(click());
    }

}