package com.eightsines.holycycle;

import android.support.test.rule.ActivityTestRule;
import com.eightsines.holycycle.app.ViewControllerFragmentActivityStub;
import com.eightsines.holycycle.util.BaseViewControllerActivityTest;
import org.junit.Rule;

public class ViewControllerFragmentActivityInstrumentedTest
        extends BaseViewControllerActivityTest<ViewControllerFragmentActivityStub> {

    @Rule
    public ActivityTestRule<ViewControllerFragmentActivityStub> activityRule = new ActivityTestRule<>(
            ViewControllerFragmentActivityStub.class,
            false,
            false);

    @Override
    protected ActivityTestRule<ViewControllerFragmentActivityStub> getActivityRule() {
        return activityRule;
    }
}
