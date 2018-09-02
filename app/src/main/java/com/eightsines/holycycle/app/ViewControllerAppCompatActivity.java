package com.eightsines.holycycle.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.eightsines.holycycle.ViewController;
import com.eightsines.holycycle.ViewControllerActivityDelegate;

public abstract class ViewControllerAppCompatActivity extends AppCompatActivity implements ViewController {
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
    protected void onSaveInstanceState(Bundle outState) {
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
}
