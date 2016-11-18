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
import static android.support.test.espresso.Espresso.pressBack;
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
public class SignInActivityTest {

    @Rule
    public ActivityTestRule<SignInActivity> mActivityTestRule = new ActivityTestRule<>(SignInActivity.class);

    @Test
    public void signInActivityTest() {
        ViewInteraction rc = onView(
                allOf(withText("Sign in"),
                        withParent(allOf(withId(R.id.sign_in_button),
                                withParent(withId(R.id.content_sign_in)))),
                        isDisplayed()));
        rc.perform(click());

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("View Trades"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction addableItem = onView(
                allOf(withClassName(is("cse403.finderskeepers.AddableItem")),
                        withParent(allOf(withId(R.id.outgoing_trades_list),
                                withParent(withId(R.id.outgoing_trades_view))))));
        addableItem.perform(scrollTo(), click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.view_profile), withText("View Other User's Profile"),
                        withParent(withId(R.id.content_view_user_trade)),
                        isDisplayed()));
        appCompatButton.perform(click());

        pressBack();

        pressBack();

        ViewInteraction addableItem2 = onView(
                allOf(withClassName(is("cse403.finderskeepers.AddableItem")),
                        withParent(allOf(withId(R.id.completed_trades_list),
                                withParent(withId(R.id.completed_trades_view))))));
        addableItem2.perform(scrollTo(), click());

        pressBack();

        ViewInteraction addableItem3 = onView(
                allOf(withClassName(is("cse403.finderskeepers.AddableItem")),
                        withParent(allOf(withId(R.id.rejected_trades_list),
                                withParent(withId(R.id.rejected_trades_view))))));
        addableItem3.perform(scrollTo(), click());

        pressBack();

    }

}
