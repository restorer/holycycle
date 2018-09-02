package com.eightsines.holycycle.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.eightsines.holycycle.util.CallRecorder;
import com.eightsines.holycycle.util.FragmentStubDelegate;

public class ViewControllerFragmentStub extends ViewControllerFragment implements CallRecorder {
    private FragmentStubDelegate delegate = new FragmentStubDelegate(this);

    @Override
    public String getCalls() {
        return delegate.getCalls();
    }

    @Override
    public void resetCalls() {
        delegate.resetCalls();
    }

    @Override
    public void onControllerCreate(@Nullable Bundle extras) {
        delegate.onControllerCreate(extras);
    }

    @Override
    public void onControllerRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        delegate.onControllerRestoreInstanceState(savedInstanceState);
    }

    @Override
    public int onControllerGetContentLayoutId() {
        return delegate.onControllerGetContentLayoutId();
    }

    @Override
    public void onControllerContentViewCreated() {
        delegate.onControllerContentViewCreated();
    }

    @Override
    public void onControllerStart() {
        delegate.onControllerStart();
    }

    @Override
    public void onControllerResume() {
        delegate.onControllerResume();
    }

    @Override
    public void onControllerFocus() {
        delegate.onControllerFocus();
    }

    @Override
    public void onControllerBlur() {
        delegate.onControllerBlur();
    }

    @Override
    public void onControllerPause() {
        delegate.onControllerPause();
    }

    @Override
    public void onControllerPersistUserData() {
        delegate.onControllerPersistUserData();
    }

    @Override
    public void onControllerStop() {
        delegate.onControllerStop();
    }

    @Override
    public void onControllerSaveInstanceState(@NonNull Bundle outState) {
        delegate.onControllerSaveInstanceState(outState);
    }
}
