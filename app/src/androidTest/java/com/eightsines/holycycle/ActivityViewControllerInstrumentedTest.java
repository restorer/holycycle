package com.eightsines.holycycle;

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
import java.util.concurrent.CountDownLatch;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActivityViewControllerInstrumentedTest {
    @Rule
    public ActivityTestRule<ActivityViewControllerStub> activityRule = new ActivityTestRule<>(ActivityViewControllerStub.class);

    private UiDevice uiDevice;
    private ActivityViewControllerStub activity;

    @Before
    public void setUp() {
        Intents.init();
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        activity = activityRule.getActivity();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testLaunch() {
        Assert.assertEquals("create:N;getContentLayoutId;contentViewCreated;start;resume;focus",
                activity.getSignature());
    }

    @Test
    public void testBlur() {
        activity.resetSignature();
        Espresso.onView(ViewMatchers.withId(R.id.show_progress)).perform(ViewActions.click());
        Assert.assertEquals("blur", activity.getSignature());
    }

    @Test
    public void testFocus() {
        Espresso.onView(ViewMatchers.withId(R.id.show_progress)).perform(ViewActions.click());
        waitALittle();

        activity.resetSignature();
        Espresso.pressBack();
        Assert.assertEquals("focus", activity.getSignature());
    }

    @Test
    public void testStartAnotherActivity() {
        activity.resetSignature();
        Espresso.onView(ViewMatchers.withId(R.id.start_activity)).perform(ViewActions.click());
        Assert.assertEquals("blur;pause;persistUserData", activity.getSignature());

        Intents.intended(IntentMatchers.hasComponent(AnotherActivityStub.class.getName()));

        activity.resetSignature();
        Espresso.pressBack();
        waitALittle();

        if (!"resume;focus".equals(activity.getSignature())) {
            Assert.assertEquals("stop;saveInstanceState:Y;start;resume;focus", activity.getSignature());
        }
    }

    @Test
    public void testPressHome() {
        waitALittle();
        activity.resetSignature();

        uiDevice.pressHome();
        waitALittle();

        Assert.assertEquals("blur;pause;persistUserData;stop;saveInstanceState:Y", activity.getSignature());
    }

    @Test
    public void testRotateDevice() {
        waitALittle();
        activity.resetSignature();

        changeActivityOrientation();
        waitALittle();

        Assert.assertEquals("blur;pause;persistUserData;stop;saveInstanceState:Y", activity.getSignature());
    }

    @Test
    public void testFinish() {
        activity.resetSignature();
        activityRule.finishActivity();
        Assert.assertEquals("blur;pause;persistUserData;stop", activity.getSignature());
    }

    private void changeActivityOrientation() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        int orientation = InstrumentationRegistry.getTargetContext().getResources().getConfiguration().orientation;

        activity.setRequestedOrientation((orientation == Configuration.ORIENTATION_PORTRAIT)
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        InstrumentationRegistry.getInstrumentation().waitForIdle(new Runnable() {
            @Override
            public void run() {
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Screen rotation failed", e);
        }
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
