package cse403.finderskeepers;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Test that adds item to the user inventory. Requires that the user has a picture saved on their
 * phone before running the test. Requires the user to select google account and permissions
 * for image access.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddItemTest {

    @Rule
    public ActivityTestRule<SignInActivity> mActivityTestRule = new ActivityTestRule<>(SignInActivity.class);

    @Test
    public void addItemTest() {
        //Click on google sign in
        ViewInteraction rc = onView(
                allOf(withText("Sign in"),
                        withParent(allOf(withId(R.id.sign_in_button),
                                withParent(withId(R.id.content_sign_in)))),
                        isDisplayed()));
        rc.perform(click());

        //Select add item picture
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.add_item),
                        withParent(allOf(withId(R.id.item_list),
                                withParent(withId(R.id.user_items_view))))));
        appCompatImageButton.perform(scrollTo(), click());

        //Select picture to add to the item
        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.add_item_img),
                        withParent(allOf(withId(R.id.content_inventory_page),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        //Add tag to the image
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTags),
                        withParent(allOf(withId(R.id.content_inventory_page),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Tagthing"), closeSoftKeyboard());

        //Select the upload button
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.upload_button), withText("Add Item"),
                        withParent(allOf(withId(R.id.content_inventory_page),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatButton.perform(click());

    }

}
