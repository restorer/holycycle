package com.eightsines.holycycle.util;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.text.TextUtils;
import junit.framework.Assert;

public final class TestUtils {
    private TestUtils() {
    }

    public static <T> void assertEqualsAny(T[] expectedList, T actual) {
        if (expectedList.length == 0) {
            throw new RuntimeException("expectedList must be non-empty");
        }

        for (T expected : expectedList) {
            if ((expected == null && actual == null)
                    || (expected != null && actual != null && expected.equals(actual))) {

                return;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Expecting [");
        sb.append(TextUtils.join("] or [", expectedList));
        sb.append("] but received ");

        if (actual == null) {
            sb.append("<NULL>");
        } else {
            sb.append('[');
            sb.append(actual);
            sb.append(']');
        }

        Assert.fail(sb.toString());
    }

    public static void performClick(@IdRes int id) {
        Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click());
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    public static void pressBack() {
        Espresso.pressBack();
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    public static void pressHome() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressHome();
        waitForIdleUiSync();
    }

    public static <T extends Activity> T launchActivity(ActivityTestRule<T> activityRule) {
        return launchActivity(activityRule, null);
    }

    public static <T extends Activity> T launchActivity(ActivityTestRule<T> activityRule,
            @Nullable Intent startIntent) {

        T result = activityRule.launchActivity(startIntent);
        TestUtils.waitForIdleUiSync(); // Additional wait for the great justice.
        return result;
    }

    public static <T extends Activity> void finishActivity(ActivityTestRule<T> activityRule) {
        activityRule.finishActivity();
        waitForIdleUiSync();
    }

    public static <T extends Activity> void changeActivityOrientation(ActivityTestRule<T> activityRule) {
        changeActivityOrientation(activityRule.getActivity());
    }

    @SuppressWarnings("WeakerAccess")
    public static void changeActivityOrientation(Activity activity) {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        int orientation = instrumentation.getTargetContext().getResources().getConfiguration().orientation;

        activity.setRequestedOrientation((orientation == Configuration.ORIENTATION_PORTRAIT)
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TestUtils.waitForIdleUiSync();
    }

    @SuppressWarnings("WeakerAccess")
    public static void waitForIdleUiSync() {
        // TODO: find a better way to ensure that we are on the right stage.
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        try {
            //noinspection MagicNumber
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            // ignored
        }
    }
}
