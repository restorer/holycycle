package com.eightsines.holycycle.util;

import android.app.Activity;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import com.eightsines.holycycle.R;
import com.eightsines.holycycle.app.TestCoverActivity;
import com.eightsines.holycycle.app.TestLauncherActivity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@LargeTest
public abstract class BaseViewControllerTest<T extends Activity> {
    @Rule
    public ActivityTestRule<TestLauncherActivity> launcherActivityRule = new ActivityTestRule<>(TestLauncherActivity.class);

    private TestCallRecorder callRecorder;

    protected abstract ActivityTestRule<T> getActivityRule();

    protected abstract TestCallRecorder launchActivityAndGetCallRecorder();

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testLaunch() {
        launchActivity();
    }

    @Test
    public void testBlur() {
        launchActivity();
        TestUtils.performClick(R.id.show_progress);
        Assert.assertEquals("blur", callRecorder.getCalls());
    }

    @Test
    public void testFocus() {
        launchActivity();
        TestUtils.performClick(R.id.show_progress);
        callRecorder.resetCalls();
        TestUtils.pressBack();
        Assert.assertEquals("focus", callRecorder.getCalls());
    }

    @Test
    public void testStartCoverActivity() {
        launchActivity();
        TestUtils.performClick(R.id.start_activity);
        Intents.intended(IntentMatchers.hasComponent(TestCoverActivity.class.getName()));

        // Sometimes onSaveInstanceState doesn't triggered immediately, so wait for it.
        TestUtils.waitForIdleUiSync();

        TestUtils.assertEqualsAny(new String[] {
                "blur;pause;persistUserData",
                "blur;pause;persistUserData;stop;saveInstanceState" }, callRecorder.getCalls());

        callRecorder.resetCalls();
        TestUtils.pressBack();

        TestUtils.assertEqualsAny(new String[] {
                "resume;focus",
                "start;resume;focus" }, callRecorder.getCalls());
    }

    @Test
    public void testPressBack() {
        launchActivity();
        TestUtils.pressBack();
        Assert.assertEquals("blur;pause;persistUserData;stop", callRecorder.getCalls());
    }

    @Test
    public void testPressHome() {
        launchActivity();
        TestUtils.pressHome();
        Assert.assertEquals("blur;pause;persistUserData;stop;saveInstanceState", callRecorder.getCalls());
    }

    @Test
    public void testRotateDevice() {
        launchActivity();
        TestUtils.changeActivityOrientation(getActivityRule());
        Assert.assertEquals("blur;pause;persistUserData;stop;saveInstanceState", callRecorder.getCalls());
    }

    @Test
    public void testFinish() {
        launchActivity();
        TestUtils.finishActivity(getActivityRule());
        Assert.assertEquals("blur;pause;persistUserData;stop", callRecorder.getCalls());
    }

    private void launchActivity() {
        callRecorder = launchActivityAndGetCallRecorder();

        if (callRecorder == null) {
            throw new RuntimeException("callRecorder is null");
        }

        Assert.assertEquals("create:N;getContentLayoutId;contentViewCreated;start;resume;focus",
                callRecorder.getCalls());

        callRecorder.resetCalls();
    }
}
