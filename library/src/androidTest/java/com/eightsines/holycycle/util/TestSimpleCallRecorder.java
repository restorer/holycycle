package com.eightsines.holycycle.util;

import android.text.TextUtils;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("AbstractClassWithoutAbstractMethods")
public abstract class TestSimpleCallRecorder implements TestCallRecorder {
    private List<String> calls = new CopyOnWriteArrayList<>();

    @Override
    public String getCalls() {
        return TextUtils.join(";", calls);
    }

    @Override
    public void resetCalls() {
        calls.clear();
    }

    void recordCall(String... args) {
        calls.add(TextUtils.join(":", args));
    }
}
