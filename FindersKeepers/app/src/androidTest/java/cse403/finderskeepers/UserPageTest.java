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

@LargeTest
@RunWith(AndroidJUnit4.class)
public class UserPageTest {

    @Rule
    public ActivityTestRule<SignInActivity> mActivityTestRule = new ActivityTestRule<>(SignInActivity.class);

    @Test
    public void userPageTest() {
        ViewInteraction rc = onView(
                allOf(withText("Sign in"),
                        withParent(allOf(withId(R.id.sign_in_button),
                                withParent(withId(R.id.content_sign_in)))),
                        isDisplayed()));
        rc.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.add_item),
                        withParent(allOf(withId(R.id.item_list),
                                withParent(withId(R.id.user_items_view))))));
        appCompatImageButton.perform(scrollTo(), click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.add_item_img),
                        withParent(allOf(withId(R.id.content_inventory_page),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTags),
                        withParent(allOf(withId(R.id.content_inventory_page),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("Brian"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.upload_button), withText("Add Item"),
                        withParent(allOf(withId(R.id.content_inventory_page),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edit_tags),
                        withParent(withId(R.id.content_home_page)),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Tag"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.update_tags), withText("Update Tags"),
                        withParent(withId(R.id.content_home_page)),
                        isDisplayed()));
        appCompatButton2.perform(click());

    }

}
