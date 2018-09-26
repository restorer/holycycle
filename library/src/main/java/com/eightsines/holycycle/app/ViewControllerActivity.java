package com.eightsines.holycycle.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.eightsines.holycycle.ViewController;
import com.eightsines.holycycle.ViewControllerActivityDelegate;
import com.eightsines.holycycle.ViewControllerLifecycle;

/**
 * This class should be used instead of {@link android.app.Activity} (android.app.Activity).
 */
public abstract class ViewControllerActivity extends Activity implements ViewController {
    private ViewControllerActivityDelegate controllerDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        controllerDelegate = new ViewControllerActivityDelegate(this, this);
        controllerDelegate.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        controllerDelegate.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        controllerDelegate.onStart();
    }

    @Override
    protected void onResume() {
        // TODO: should we use onPostResume() instead (like onResumeFragments() in FragmentActivity)?
        // https://developer.android.com/reference/android/support/v4/app/FragmentActivity#onResume()
        //
        // According to quick look into Activity and platform FragmentManager,
        // onResume() should be enough, but we should know for sure.

        super.onResume();
        controllerDelegate.onResume();
    }

    @Override
    protected void onPause() {
        controllerDelegate.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        controllerDelegate.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        controllerDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        controllerDelegate.onSaveInstanceState(outState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        controllerDelegate.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void finish() {
        controllerDelegate.finish();
        super.finish();
    }

    @Override
    public void onControllerCreate(@Nullable Bundle extras) {
    }

    @Override
    public void onControllerRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    }

    @Override
    public int onControllerGetContentLayoutId() {
        return 0;
    }

    @Override
    public void onControllerContentViewCreated() {
    }

    @Override
    public void onControllerStart() {
    }

    @Override
    public void onControllerResume() {
    }

    @Override
    public void onControllerFocus() {
    }

    @Override
    public void onControllerBlur() {
    }

    @Override
    public void onControllerPause() {
    }

    @Override
    public void onControllerPersistUserData() {
    }

    @Override
    public void onControllerStop() {
    }

    @Override
    public void onControllerSaveInstanceState(@NonNull Bundle outState) {
    }

    @Nullable
    @Override
    public View getView() {
        return controllerDelegate.getView();
    }

    @NonNull
    @Override
    public ViewControllerLifecycle getControllerLifecycle() {
        return controllerDelegate.getControllerLifecycle();
    }
}
