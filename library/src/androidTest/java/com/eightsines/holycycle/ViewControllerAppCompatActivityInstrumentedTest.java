package com.eightsines.holycycle;

import android.support.test.rule.ActivityTestRule;
import com.eightsines.holycycle.app.ViewControllerAppCompatActivityStub;
import com.eightsines.holycycle.util.BaseViewControllerActivityTest;
import org.junit.Rule;

public class ViewControllerAppCompatActivityInstrumentedTest
        extends BaseViewControllerActivityTest<ViewControllerAppCompatActivityStub> {

    @Rule
    public ActivityTestRule<ViewControllerAppCompatActivityStub> activityRule = new ActivityTestRule<>(
            ViewControllerAppCompatActivityStub.class,
            false,
            false);

    @Override
    protected ActivityTestRule<ViewControllerAppCompatActivityStub> getActivityRule() {
        return activityRule;
    }
}
