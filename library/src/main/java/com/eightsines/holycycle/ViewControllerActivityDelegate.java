package com.eightsines.holycycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.eightsines.holycycle.app.ViewControllerActivity;
import com.eightsines.holycycle.app.ViewControllerAppCompatActivity;
import com.eightsines.holycycle.app.ViewControllerFragmentActivity;

/**
 * Helper class which transform Android-lifecycle to ViewController-lifecycle.
 * <p>If you can't use {@link ViewControllerActivity}, {@link ViewControllerFragmentActivity},
 * or {@link ViewControllerAppCompatActivity} (eg. you can't change base activity for some reason),
 * use this class. See the {@link ViewControllerActivity} for example of use.</p>
 */
public class ViewControllerActivityDelegate implements ViewControllerLifecycleOwner, ViewControllerLifecycleTracker {
    private static final int HOST_DESTROYED = -2;
    private static final int HOST_INSTANCE_STATE_SAVED = -1;
    private static final int HOST_INITIALIZED = 0;
    private static final int HOST_CREATED = 1;
    private static final int HOST_STARTED = 2;
    private static final int HOST_RESUMED = 3;

    private Activity owner;
    private ViewController controller;
    private ViewControllerLifecycleRegistry registry;
    private int hostState = HOST_INITIALIZED;
    private int contentLayoutResId;
    private boolean hasWindowFocus;
    private boolean isFinished;

    /**
     * View controller delegate constructor. Mostly you want use it like
     * {@code new ViewControllerActivityDelegate(this, this)}, however it is possible to separate
     * host activity from the view controller.
     * <p>See the {@link ViewControllerActivity} for an example of use.</p>
     *
     * @param owner Activity that owns the view controller.
     * @param controller Managed view controller.
     */
    @SuppressWarnings("WeakerAccess")
    public ViewControllerActivityDelegate(@NonNull Activity owner, @NonNull ViewController controller) {
        this.owner = owner;
        this.controller = controller;

        registry = new ViewControllerLifecycleRegistry(this);
    }

    /**
     * Call this method from {@link Activity#onCreate(Bundle savedInstanceState)}
     * after {@code super.onCreate(savedInstanceState)}.
     *
     * @param savedInstanceState Pass {@code savedInstanceState} parameter here.
     */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (hostState == HOST_DESTROYED || isFinished) {
            return;
        }

        if (hostState != HOST_INITIALIZED) {
            throw new IllegalStateException(
                    "This should not happen, but onCreate() was called with an invalid hostState ("
                            + hostState
                            + ").");
        }

        hostState = HOST_CREATED;
        Intent intent = owner.getIntent();

        // Checking intent for null is redundant, but leaved here for the great justice.
        controller.onControllerCreate(intent == null ? null : intent.getExtras());

        if (isFinished) {
            return;
        }

        if (savedInstanceState != null) {
            controller.onControllerRestoreInstanceState(savedInstanceState);

            if (isFinished) {
                return;
            }
        }

        contentLayoutResId = controller.onControllerGetContentLayoutId();

        if (!isFinished && contentLayoutResId != 0) {
            owner.setContentView(contentLayoutResId);
            controller.onControllerContentViewCreated();
        }
    }

    /**
     * Call this method from {@link Activity#onRestart()} after {@code super.onRestart()}.
     */
    public void onRestart() {
        if (hostState == HOST_DESTROYED || isFinished) {
            return;
        }

        if (hostState == HOST_INSTANCE_STATE_SAVED) {
            hostState = HOST_CREATED;
        } else if (hostState != HOST_CREATED) {
            throw new IllegalStateException(
                    "onRestart() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onCreate()?");
        }
    }

    /**
     * Call this method from {@link Activity#onStart()} after {@code super.onStart()}.
     */
    public void onStart() {
        if (hostState == HOST_DESTROYED || isFinished) {
            return;
        }

        if (hostState != HOST_CREATED) {
            throw new IllegalStateException(
                    "onStart() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onCreate() or onRestart()?");
        }

        hostState = HOST_STARTED;

        controller.onControllerStart();
        registry.onControllerStart();
    }

    /**
     * Call this method from {@link Activity#onResume()} after {@code super.onResume()}.
     */
    public void onResume() {
        if (hostState == HOST_DESTROYED || isFinished) {
            return;
        }

        // HOST_INSTANCE_STATE_SAVED - Should not happen, but handled for the great justice.
        if (hostState == HOST_INSTANCE_STATE_SAVED) {
            controller.onControllerStart();
            registry.onControllerStart();
        } else if (hostState != HOST_STARTED) {
            throw new IllegalStateException(
                    "onResume() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onStart()?");
        }

        hostState = HOST_RESUMED;

        controller.onControllerResume();
        registry.onControllerResume();

        if (!isFinished && hasWindowFocus) {
            controller.onControllerFocus();
            registry.onControllerFocus();
        }
    }

    /**
     * Call this method from {@link Activity#onPause()} before {@code super.onPause()}.
     */
    public void onPause() {
        if (hostState == HOST_DESTROYED || hostState == HOST_INSTANCE_STATE_SAVED) {
            return;
        }

        if (hostState != HOST_RESUMED) {
            if (isFinished) {
                return;
            }

            throw new IllegalStateException(
                    "onPause() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onResume()?");
        }

        hostState = HOST_STARTED;

        if (hasWindowFocus) {
            registry.onControllerBlur();
            controller.onControllerBlur();
        }

        registry.onControllerPause();
        controller.onControllerPause();

        registry.onControllerPersistUserData();
        controller.onControllerPersistUserData();
    }

    /**
     * Call this method from {@link Activity#onStop()} before {@code super.onStop()}.
     */
    public void onStop() {
        if (hostState == HOST_DESTROYED || hostState == HOST_INSTANCE_STATE_SAVED) {
            return;
        }

        if (hostState != HOST_STARTED) {
            if (isFinished) {
                return;
            }

            throw new IllegalStateException(
                    "onStop() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onPause()?");
        }

        hostState = HOST_CREATED;

        registry.onControllerStop();
        controller.onControllerStop();
    }

    /**
     * Call this method from {@link Activity#onDestroy()} before {@code super.onDestroy()}.
     */
    public void onDestroy() {
        if (hostState == HOST_DESTROYED) {
            return;
        }

        if (hostState != HOST_CREATED && hostState != HOST_INSTANCE_STATE_SAVED && !isFinished) {
            throw new IllegalStateException(
                    "onDestroy() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onStop()?");
        }

        hostState = HOST_DESTROYED;
    }

    /**
     * Call this method from {@link Activity#onSaveInstanceState(Bundle outState)}
     * after {@code super.onSaveInstanceState(Bundle outState)}.
     */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Allow to continue with HOST_INSTANCE_STATE_SAVED or isFinished.
        if (hostState == HOST_DESTROYED) {
            return;
        }

        if (hostState == HOST_RESUMED) {
            onPause();
        }

        if (hostState == HOST_STARTED) {
            onStop();
        }

        hostState = HOST_INSTANCE_STATE_SAVED;
        controller.onControllerSaveInstanceState(outState);
    }

    /**
     * Call this method from {@link Activity#onWindowFocusChanged(boolean hasFocus)}
     * after {@code super.onWindowFocusChanged(hasFocus)}.
     *
     * @param hasFocus Pass {@code hasFocus} parameter here.
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && !hasWindowFocus) {
            hasWindowFocus = true;

            if (hostState == HOST_RESUMED) {
                controller.onControllerFocus();
                registry.onControllerFocus();
            }
        } else if (!hasFocus && hasWindowFocus) {
            hasWindowFocus = false;

            if (hostState == HOST_RESUMED) {
                registry.onControllerBlur();
                controller.onControllerBlur();
            }
        }
    }

    /**
     * Call this method from {@link Activity#finish()} before {@code super.finish()}.
     */
    public void finish() {
        isFinished = true;
    }

    /**
     * Pass return value from this method to {@link ViewController#getView()}.
     *
     * @return The content view or {@code null}.
     */
    @Nullable
    public View getView() {
        return (contentLayoutResId == 0 ? null : owner.findViewById(android.R.id.content));
    }

    /**
     * Pass return value from this method to {@link ViewController#getControllerLifecycle()}.
     *
     * @return The controller lifecycle.
     */
    @NonNull
    @Override
    public ViewControllerLifecycle getControllerLifecycle() {
        return registry;
    }

    /**
     * Internal method for the {@link ViewControllerLifecycleRegistry}.
     *
     * @param state State to compare with.
     * @return true if current lifecycle state of the view controller is greater or equal to the given {@code state}.
     */
    @Override
    public boolean isControllerStateAtLeast(int state) {
        switch (state) {
            case ViewControllerLifecycle.STATE_FOCUSED:
                return (hostState == HOST_RESUMED && hasWindowFocus);

            case ViewControllerLifecycle.STATE_RESUMED:
                return (hostState == HOST_RESUMED);

            case ViewControllerLifecycle.STATE_STARTED:
                return (hostState == HOST_STARTED || hostState == HOST_RESUMED);

            default:
                return false;
        }
    }
}
