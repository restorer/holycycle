package com.eightsines.holycycle;

import android.support.test.rule.ActivityTestRule;
import com.eightsines.holycycle.app.ViewControllerActivityStub;
import com.eightsines.holycycle.util.BaseViewControllerActivityTest;
import org.junit.Rule;

public class ViewControllerActivityInstrumentedTest extends BaseViewControllerActivityTest<ViewControllerActivityStub> {
    @Rule
    public ActivityTestRule<ViewControllerActivityStub> activityRule = new ActivityTestRule<>(ViewControllerActivityStub.class,
            false,
            false);

    @Override
    protected ActivityTestRule<ViewControllerActivityStub> getActivityRule() {
        return activityRule;
    }
}
