package com.eightsines.holycycle.util;

import android.app.Activity;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.eightsines.holycycle.R;
import com.eightsines.holycycle.app.TestCoverActivity;
import com.eightsines.holycycle.app.TestLauncherActivity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public abstract class BaseViewControllerActivityTest<T extends Activity & CallRecorder> {
    @Rule
    public ActivityTestRule<TestLauncherActivity> launcherActivityRule = new ActivityTestRule<>(TestLauncherActivity.class);

    protected abstract ActivityTestRule<T> getActivityRule();

    private CallRecorder stub;

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
        launchActivity(false);

        Assert.assertEquals("create:N;getContentLayoutId;contentViewCreated;start;resume;focus",
                stub.getCalls());
    }

    @Test
    public void testBlur() {
        launchActivity();
        TestUtils.performClick(R.id.show_progress);
        Assert.assertEquals("blur", stub.getCalls());
    }

    @Test
    public void testFocus() {
        launchActivity();
        TestUtils.performClick(R.id.show_progress);
        stub.resetCalls();
        TestUtils.pressBack();
        Assert.assertEquals("focus", stub.getCalls());
    }

    @Test
    public void testStartCoverActivity() {
        launchActivity();
        TestUtils.performClick(R.id.start_activity);
        Intents.intended(IntentMatchers.hasComponent(TestCoverActivity.class.getName()));

        TestUtils.assertEqualsAny(new String[] {
                "blur;pause;persistUserData",
                "blur;pause;persistUserData;stop;saveInstanceState" }, stub.getCalls());

        stub.resetCalls();
        TestUtils.pressBack();

        TestUtils.assertEqualsAny(new String[] {
                "resume;focus",
                "start;resume;focus" }, stub.getCalls());
    }

    @Test
    public void testPressBack() {
        launchActivity();
        TestUtils.pressBack();
        Assert.assertEquals("blur;pause;persistUserData;stop", stub.getCalls());
    }

    @Test
    public void testPressHome() {
        launchActivity();
        TestUtils.pressHome();
        Assert.assertEquals("blur;pause;persistUserData;stop;saveInstanceState", stub.getCalls());
    }

    @Test
    public void testRotateDevice() {
        launchActivity();
        TestUtils.changeActivityOrientation(getActivityRule());
        Assert.assertEquals("blur;pause;persistUserData;stop;saveInstanceState", stub.getCalls());
    }

    @Test
    public void testFinish() {
        launchActivity();
        TestUtils.finishActivity(getActivityRule());
        Assert.assertEquals("blur;pause;persistUserData;stop", stub.getCalls());
    }

    private void launchActivity() {
        launchActivity(true);
    }

    private void launchActivity(boolean shouldResetSignature) {
        stub = TestUtils.launchActivity(getActivityRule());

        if (shouldResetSignature) {
            stub.resetCalls();
        }
    }
}
