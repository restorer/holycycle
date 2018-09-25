package com.eightsines.holycycle;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import com.eightsines.holycycle.app.TestFragmentActivity;
import com.eightsines.holycycle.util.BaseViewControllerTest;
import com.eightsines.holycycle.util.TestCallRecorder;
import com.eightsines.holycycle.util.TestUtils;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ViewControllerFragmentInstrumentedTest extends BaseViewControllerTest<TestFragmentActivity> {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { TestFragmentActivity.MODE_EMBEDDED },
                { TestFragmentActivity.MODE_ON_CREATE },
                { TestFragmentActivity.MODE_ON_START } });
    }

    @Rule
    public ActivityTestRule<TestFragmentActivity> activityRule = new ActivityTestRule<>(TestFragmentActivity.class,
            false,
            false);

    @Parameterized.Parameter
    public String startMode;

    @Override
    protected ActivityTestRule<TestFragmentActivity> getActivityRule() {
        return activityRule;
    }

    @Override
    protected TestCallRecorder launchActivityAndGetCallRecorder() {
        TestFragmentActivity activity = TestUtils.launchActivity(activityRule,
                new Intent().putExtra(TestFragmentActivity.EXTRA_MODE, startMode));

        return activity.getCallRecorder();
    }
}
