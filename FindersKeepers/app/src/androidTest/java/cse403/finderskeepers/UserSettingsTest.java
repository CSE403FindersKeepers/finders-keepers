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
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Tests the functionality of user settings page. Changes avatar. Enters zipcode. Tests all buttons.
 * Requires the user to enter their google account, allow image permission,
 * and an image for the avatar.
 * The tester must have an image on their phone.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class UserSettingsTest {

    @Rule
    public ActivityTestRule<SignInActivity> mActivityTestRule = new ActivityTestRule<>(SignInActivity.class);

    @Test
    public void userSettingsTest() {
        //Sign in button selected
        ViewInteraction rc = onView(
                allOf(withText("Sign in"),
                        withParent(allOf(withId(R.id.sign_in_button),
                                withParent(withId(R.id.content_sign_in)))),
                        isDisplayed()));
        rc.perform(click());

        //select the menu
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        //select the settings option
        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Settings"), isDisplayed()));
        appCompatTextView.perform(click());

        //Select change avatar button
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.action_change_avatar), withText("Change Avatar"),
                        withParent(withId(R.id.content_user_settings)),
                        isDisplayed()));
        appCompatButton.perform(click());

        //Select update avatar and zip button
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.action_change_location), withText("Update Avatar and ZIP"),
                        withParent(withId(R.id.content_user_settings)),
                        isDisplayed()));
        appCompatButton2.perform(click());

        //select the text field
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edit_zip_field), withText("98105"),
                        withParent(withId(R.id.content_user_settings)),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("98105"), closeSoftKeyboard());

        //change the contents of the text field to test text field functionality
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edit_zip_field), withText("98105"),
                        withParent(withId(R.id.content_user_settings)),
                        isDisplayed()));
        appCompatEditText2.perform(pressImeActionButton());

        //press update avatar and zip button
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.action_change_location), withText("Update Avatar and ZIP"),
                        withParent(withId(R.id.content_user_settings)),
                        isDisplayed()));
        appCompatButton3.perform(click());

    }

}
