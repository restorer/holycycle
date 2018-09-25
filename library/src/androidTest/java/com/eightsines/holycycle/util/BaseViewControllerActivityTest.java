package com.eightsines.holycycle.util;

import android.app.Activity;
import android.support.test.filters.LargeTest;

@LargeTest
public abstract class BaseViewControllerActivityTest<T extends Activity & TestCallRecorder>
        extends BaseViewControllerTest<T> {

    @Override
    protected TestCallRecorder launchActivityAndGetCallRecorder() {
        return TestUtils.launchActivity(getActivityRule());
    }
}
