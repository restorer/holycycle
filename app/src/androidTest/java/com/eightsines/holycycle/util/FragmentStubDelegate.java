package com.eightsines.holycycle.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import com.eightsines.holycycle.R;
import com.eightsines.holycycle.ViewController;
import com.eightsines.holycycle.app.TestCoverActivity;

public class FragmentStubDelegate extends TestSimpleCallRecorder implements ViewController {
    private ViewController owner;
    private android.support.v4.app.Fragment ownerSupportFragment;
    private android.app.Fragment ownerPlatformFragment;
    private Button showProgressButton;
    private Button startActivityButton;

    public FragmentStubDelegate(ViewController owner) {
        super();
        this.owner = owner;

        if (owner instanceof android.support.v4.app.Fragment) {
            ownerSupportFragment = (android.support.v4.app.Fragment)owner;
        } else if (owner instanceof android.app.Fragment) {
            ownerPlatformFragment = (android.app.Fragment)owner;
        } else {
            throw new RuntimeException("owner must be an instance of Fragment (support or platform)");
        }
    }

    @Override
    public void onControllerCreate(@Nullable Bundle extras) {
        recordCall("create", extras == null ? "N" : "Y");
    }

    @Override
    public void onControllerRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        recordCall("restoreInstanceState");
    }

    @Override
    public int onControllerGetContentLayoutId() {
        recordCall("getContentLayoutId");
        return R.layout.test_ui;
    }

    @Override
    public void onControllerContentViewCreated() {
        recordCall("contentViewCreated");

        showProgressButton = findViewById(R.id.show_progress);
        startActivityButton = findViewById(R.id.start_activity);
    }

    @Override
    public void onControllerStart() {
        recordCall("start");

        showProgressButton.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                android.app.ProgressDialog.show(getContext(), "PROGRESS", "PROGRESS", true, true);
            }
        });

        startActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), TestCoverActivity.class));
            }
        });
    }

    @Override
    public void onControllerResume() {
        recordCall("resume");
    }

    @Override
    public void onControllerFocus() {
        recordCall("focus");
    }

    @Override
    public void onControllerBlur() {
        recordCall("blur");
    }

    @Override
    public void onControllerPause() {
        recordCall("pause");
    }

    @Override
    public void onControllerPersistUserData() {
        recordCall("persistUserData");
    }

    @Override
    public void onControllerStop() {
        recordCall("stop");

        showProgressButton.setOnClickListener(null);
        startActivityButton.setOnClickListener(null);
    }

    @Override
    public void onControllerSaveInstanceState(@NonNull Bundle outState) {
        recordCall("saveInstanceState");
    }

    @Nullable
    @Override
    public View getView() {
        return owner.getView();
    }

    @Nullable
    @Override
    public <T extends View> T findViewById(int id) {
        return owner.findViewById(id);
    }

    private Context getContext() {
        if (ownerSupportFragment != null) {
            return ownerSupportFragment.getContext();
        } else {
            // Use getActivity() instead of getContext() to support older APIs.
            return ownerPlatformFragment.getActivity();
        }
    }

    private void startActivity(Intent intent) {
        if (ownerSupportFragment != null) {
            ownerSupportFragment.startActivity(intent);
        } else {
            ownerPlatformFragment.startActivity(intent);
        }
    }
}
