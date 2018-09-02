package com.eightsines.holycycle.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import com.eightsines.holycycle.R;
import com.eightsines.holycycle.ViewController;
import com.eightsines.holycycle.app.TestCoverActivity;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestActivityStubDelegate<VC extends Activity & ViewController> implements ViewController, TestActivityStub {
    private VC owner;
    private List<String> callsList = new CopyOnWriteArrayList<>();
    private Button showProgressButton;
    private Button startActivityButton;

    public TestActivityStubDelegate(VC owner) {
        this.owner = owner;
    }

    @Override
    public String getCalls() {
        return TextUtils.join(";", callsList);
    }

    @Override
    public void resetCalls() {
        callsList.clear();
    }

    @Override
    public void onControllerCreate(@Nullable Bundle extras) {
        callsList.add("create:" + (extras == null ? "N" : "Y"));
    }

    @Override
    public void onControllerRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        callsList.add("restoreInstanceState");
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
                ProgressDialog.show(owner, "PROGRESS", "PROGRESS", true, true);
            }
        });

        startActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner.startActivity(new Intent(owner, TestCoverActivity.class));
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
        callsList.add("saveInstanceState");
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
}
