package com.eightsines.holycycle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ActivityViewControllerStub extends ActivityViewController {
    private List<String> callsList = new CopyOnWriteArrayList<>();
    private Button showProgressButton;
    private Button startActivityButton;

    public String getSignature() {
        return TextUtils.join(";", callsList);
    }

    public void resetSignature() {
        callsList.clear();
    }

    @Override
    public void onControllerCreate(@Nullable Bundle extras) {
        callsList.add("create:" + (extras == null ? "N" : "Y"));
    }

    @Override
    public void onControllerRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        //noinspection ConstantConditions
        callsList.add("restoreInstanceState:" + (savedInstanceState == null ? "N" : "Y"));
    }

    @Override
    public int onControllerGetContentLayoutId() {
        callsList.add("getContentLayoutId");
        return R.layout.activity_stub;
    }

    @Override
    public void onControllerContentViewCreated() {
        callsList.add("contentViewCreated");

        showProgressButton = findViewById(R.id.show_progress);
        startActivityButton = findViewById(R.id.start_activity);
    }

    @Override
    public void onControllerStart() {
        callsList.add("start");

        showProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog.show(ActivityViewControllerStub.this, "TITLE", "MESSAGE", true, true);
            }
        });

        startActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityViewControllerStub.this, AnotherActivityStub.class));
            }
        });
    }

    @Override
    public void onControllerResume() {
        callsList.add("resume");
    }

    @Override
    public void onControllerFocus() {
        callsList.add("focus");
    }

    @Override
    public void onControllerBlur() {
        callsList.add("blur");
    }

    @Override
    public void onControllerPause() {
        callsList.add("pause");
    }

    @Override
    public void onControllerPersistUserData() {
        callsList.add("persistUserData");
    }

    @Override
    public void onControllerStop() {
        callsList.add("stop");

        showProgressButton.setOnClickListener(null);
        startActivityButton.setOnClickListener(null);
    }

    @Override
    public void onControllerSaveInstanceState(@NonNull Bundle outState) {
        //noinspection ConstantConditions
        callsList.add("saveInstanceState:" + (outState == null ? "N" : "Y"));
    }
}
