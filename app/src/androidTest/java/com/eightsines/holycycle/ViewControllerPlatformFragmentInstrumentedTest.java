package com.eightsines.holycycle;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import com.eightsines.holycycle.app.TestPlatformFragmentActivity;
import com.eightsines.holycycle.util.BaseViewControllerTest;
import com.eightsines.holycycle.util.TestCallRecorder;
import com.eightsines.holycycle.util.TestUtils;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ViewControllerPlatformFragmentInstrumentedTest
        extends BaseViewControllerTest<TestPlatformFragmentActivity> {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { TestPlatformFragmentActivity.MODE_EMBEDDED },
                { TestPlatformFragmentActivity.MODE_ON_CREATE },
                { TestPlatformFragmentActivity.MODE_ON_START } });
    }

    @Rule
    public ActivityTestRule<TestPlatformFragmentActivity> activityRule = new ActivityTestRule<>(
            TestPlatformFragmentActivity.class,
            false,
            false);

    @Parameterized.Parameter
    public String startMode;

    @Override
    protected ActivityTestRule<TestPlatformFragmentActivity> getActivityRule() {
        return activityRule;
    }

    @Override
    protected TestCallRecorder launchActivityAndGetCallRecorder() {
        TestPlatformFragmentActivity activity = TestUtils.launchActivity(activityRule,
                new Intent().putExtra(TestPlatformFragmentActivity.EXTRA_MODE, startMode));

        return activity.getCallRecorder();
    }
}
