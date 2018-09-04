package com.eightsines.holycycle.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.eightsines.holycycle.R;
import com.eightsines.holycycle.util.TestCallRecorder;

public class TestPlatformFragmentActivity extends Activity {
    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

    public static final String EXTRA_MODE = "EXTRA_MODE";

    public static final String MODE_EMBEDDED = "MODE_EMBEDDED";
    public static final String MODE_ON_CREATE = "MODE_ON_CREATE";
    public static final String MODE_ON_START = "MODE_ON_START";

    private String mode;
    private ViewControllerPlatformFragment fragment;

    public TestCallRecorder getCallRecorder() {
        return (TestCallRecorder)fragment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mode = getIntent().getStringExtra(EXTRA_MODE);

        if (MODE_EMBEDDED.equals(mode)) {
            setContentView(R.layout.test_fragment_platform);
            fragment = (ViewControllerPlatformFragment)getFragmentManager().findFragmentById(R.id.fragment);
        } else if (MODE_ON_CREATE.equals(mode) || MODE_ON_START.equals(mode)) {
            setContentView(R.layout.test_fragment_container);
        } else {
            throw new RuntimeException("Invalid mode");
        }

        if (!MODE_ON_CREATE.equals(mode)) {
            return;
        }

        if (savedInstanceState == null) {
            fragment = ViewControllerPlatformFragmentStub.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.container, fragment, FRAGMENT_TAG).commit();
        } else {
            fragment = (ViewControllerPlatformFragment)getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!MODE_ON_START.equals(mode)) {
            return;
        }

        fragment = (ViewControllerPlatformFragment)getFragmentManager().findFragmentByTag(FRAGMENT_TAG);

        if (fragment == null) {
            fragment = ViewControllerPlatformFragmentStub.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.container, fragment, FRAGMENT_TAG).commit();
        }
    }
}
