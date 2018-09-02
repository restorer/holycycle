package com.eightsines.holycycle;

import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import com.eightsines.holycycle.app.TestCoverActivity;
import com.eightsines.holycycle.app.TestLauncherActivity;
import com.eightsines.holycycle.app.ViewControllerActivityStub;
import com.eightsines.holycycle.util.TestUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ViewControllerActivityInstrumentedTest {
    @Rule
    public ActivityTestRule<TestLauncherActivity> launcherActivityRule = new ActivityTestRule<>(TestLauncherActivity.class);

    @Rule
    public ActivityTestRule<ViewControllerActivityStub> activityRule = new ActivityTestRule<>(ViewControllerActivityStub.class,
            false,
            false);

    private Instrumentation instrumentation;
    private Context context;
    private UiDevice uiDevice;
    private ViewControllerActivityStub activity;

    @Before
    public void setUp() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        context = instrumentation.getTargetContext();
        uiDevice = UiDevice.getInstance(instrumentation);

        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testLaunch() {
        launchActivity(false);

        Assert.assertEquals("create:N;getContentLayoutId;contentViewCreated;start;resume;focus",
                activity.getSignature());
    }

    @Test
    public void testBlur() {
        launchActivity();

        Espresso.onView(ViewMatchers.withId(R.id.show_progress)).perform(ViewActions.click());
        waitALittle();

        Assert.assertEquals("blur", activity.getSignature());
    }

    @Test
    public void testFocus() {
        launchActivity();

        Espresso.onView(ViewMatchers.withId(R.id.show_progress)).perform(ViewActions.click());
        waitALittle();

        activity.resetSignature();

        Espresso.pressBack();
        waitALittle();

        Assert.assertEquals("focus", activity.getSignature());
    }

    @Test
    public void testStartAnotherActivity() {
        launchActivity();

        Espresso.onView(ViewMatchers.withId(R.id.start_activity)).perform(ViewActions.click());
        waitALittle();

        TestUtils.assertEqualsAny(new String[] {
                "blur;pause;persistUserData",
                "blur;pause;persistUserData;stop;saveInstanceState" }, activity.getSignature());

        Intents.intended(IntentMatchers.hasComponent(TestCoverActivity.class.getName()));
        activity.resetSignature();

        Espresso.pressBack();
        waitALittle();

        TestUtils.assertEqualsAny(new String[] {
                "resume;focus",
                "stop;saveInstanceState;start;resume;focus" }, activity.getSignature());
    }

    @Test
    public void testPressHome() {
        launchActivity();

        uiDevice.pressHome();
        waitALittle();

        Assert.assertEquals("blur;pause;persistUserData;stop;saveInstanceState", activity.getSignature());
    }

    @Test
    public void testRotateDevice() {
        launchActivity();
        changeActivityOrientation();
        Assert.assertEquals("blur;pause;persistUserData;stop;saveInstanceState", activity.getSignature());
    }

    @Test
    public void testFinish() {
        launchActivity();

        activityRule.finishActivity();
        waitALittle();

        Assert.assertEquals("blur;pause;persistUserData;stop", activity.getSignature());
    }

    private void launchActivity() {
        launchActivity(true);
    }

    private void launchActivity(boolean shouldResetSignature) {
        activity = activityRule.launchActivity(null);

        if (shouldResetSignature) {
            activity.resetSignature();
        }
    }

    private void changeActivityOrientation() {
        int orientation = context.getResources().getConfiguration().orientation;

        activity.setRequestedOrientation((orientation == Configuration.ORIENTATION_PORTRAIT)
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        instrumentation.waitForIdleSync();
        waitALittle();
    }

    private void waitALittle() {
        // TODO: find a better way to ensure that we are on the right stage

        try {
            //noinspection MagicNumber
            Thread.sleep(250L);
        } catch (InterruptedException e) {
            // ignored
        }
    }
}
