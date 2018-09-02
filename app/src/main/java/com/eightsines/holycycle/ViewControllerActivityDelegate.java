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
public class ViewControllerActivityDelegate {
    private static final int STATE_DESTROYED = -3;
    private static final int STATE_FINISHED = -2;
    private static final int STATE_INSTANCE_STATE_SAVED = -1;
    private static final int STATE_INITIALIZED = 0;
    private static final int STATE_CREATED = 1;
    private static final int STATE_STARTED = 2;
    private static final int STATE_RESUMED = 3;

    private Activity owner;
    private ViewController controller;
    private int state = STATE_INITIALIZED;
    private int contentLayoutResId;
    private boolean hasWindowFocus;
    private boolean isPerformingPause;
    private boolean isPerformingSaveInstanceState;
    private boolean isFinishPending;

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
    public ViewControllerActivityDelegate(Activity owner, ViewController controller) {
        this.owner = owner;
        this.controller = controller;
    }

    /**
     * Call this method from {@link Activity#onCreate(Bundle savedInstanceState)}
     * after {@code super.onCreate(savedInstanceState)}.
     *
     * @param savedInstanceState Pass {@code savedInstanceState} parameter here.
     */
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (state == STATE_DESTROYED || state == STATE_FINISHED) {
            // Should not happen.
            return;
        }

        if (state != STATE_INITIALIZED) {
            throw new IllegalStateException("This should not happen, but onCreate() was called with an invalid state ("
                    + state
                    + ").");
        }

        state = STATE_CREATED;
        Intent intent = owner.getIntent();

        // Checking intent for null is redundant, but leaved here for the great justice.
        controller.onControllerCreate(intent == null ? null : intent.getExtras());

        if (state == STATE_FINISHED) {
            return;
        }

        if (savedInstanceState != null) {
            controller.onControllerRestoreInstanceState(savedInstanceState);

            if (state == STATE_FINISHED) {
                return;
            }
        }

        contentLayoutResId = controller.onControllerGetContentLayoutId();

        if (state != STATE_FINISHED && contentLayoutResId != 0) {
            owner.setContentView(contentLayoutResId);
            controller.onControllerContentViewCreated();
        }
    }

    /**
     * Call this method from {@link Activity#onRestart()} after {@code super.onRestart()}.
     */
    public void onRestart() {
        if (state == STATE_DESTROYED || state == STATE_FINISHED) {
            // Should not happen.
            return;
        }

        if (state == STATE_INSTANCE_STATE_SAVED) {
            state = STATE_CREATED;
        } else if (state != STATE_CREATED) {
            throw new IllegalStateException(
                    "onRestart() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onCreate()?");
        }
    }

    /**
     * Call this method from {@link Activity#onStart()} after {@code super.onStart()}.
     */
    public void onStart() {
        if (state == STATE_DESTROYED || state == STATE_FINISHED) {
            return;
        }

        if (state != STATE_CREATED) {
            throw new IllegalStateException(
                    "onStart() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onCreate() or onRestart()?");
        }

        state = STATE_STARTED;
        controller.onControllerStart();
    }

    /**
     * Call this method from {@link Activity#onResume()} after {@code super.onResume()}.
     */
    public void onResume() {
        if (state == STATE_DESTROYED || state == STATE_FINISHED) {
            return;
        }

        if (state != STATE_STARTED) {
            throw new IllegalStateException(
                    "onResume() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onStart()?");
        }

        state = STATE_RESUMED;
        controller.onControllerResume();

        if (state != STATE_FINISHED && hasWindowFocus) {
            controller.onControllerFocus();
        }
    }

    /**
     * Call this method from {@link Activity#onPause()} before {@code super.onPause()}.
     */
    public void onPause() {
        if (state == STATE_DESTROYED || state == STATE_INSTANCE_STATE_SAVED || state == STATE_FINISHED) {
            return;
        }

        if (state != STATE_RESUMED) {
            throw new IllegalStateException(
                    "onPause() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onResume()?");
        }

        isPerformingPause = true;

        if (hasWindowFocus) {
            controller.onControllerBlur();
        }

        controller.onControllerPause();
        controller.onControllerPersistUserData();

        state = STATE_STARTED;
        isPerformingPause = false;

        if (isFinishPending) {
            isFinishPending = false;
            finish();
        }
    }

    /**
     * Call this method from {@link Activity#onStop()} before {@code super.onStop()}.
     */
    public void onStop() {
        if (state == STATE_DESTROYED || state == STATE_INSTANCE_STATE_SAVED || state == STATE_FINISHED) {
            return;
        }

        if (state != STATE_STARTED) {
            throw new IllegalStateException(
                    "onStop() was called with an invalid state (" + state + "), perhaps you forgot to call onPause()?");
        }

        state = STATE_CREATED;
        controller.onControllerStop();
    }

    /**
     * Call this method from {@link Activity#onDestroy()} before {@code super.onDestroy()}.
     */
    public void onDestroy() {
        if (state == STATE_DESTROYED) {
            return;
        }

        if (state != STATE_CREATED && state != STATE_INSTANCE_STATE_SAVED && state != STATE_FINISHED) {
            throw new IllegalStateException(
                    "onDestroy() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onStop()?");
        }

        state = STATE_DESTROYED;
    }

    /**
     * Call this method from {@link Activity#onSaveInstanceState(Bundle outState)}
     * after {@code super.onSaveInstanceState(Bundle outState)}.
     */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Allow to continue with STATE_INSTANCE_STATE_SAVED and STATE_FINISHED.
        if (state == STATE_DESTROYED) {
            return;
        }

        isPerformingSaveInstanceState = true;

        if (state == STATE_RESUMED) {
            onPause();
        }

        if (state == STATE_STARTED) {
            onStop();
        }

        controller.onControllerSaveInstanceState(outState);

        if (state != STATE_FINISHED) {
            state = STATE_INSTANCE_STATE_SAVED;
        }

        isPerformingSaveInstanceState = false;

        if (isFinishPending) {
            isFinishPending = false;
            finish();
        }
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

            if (state == STATE_RESUMED) {
                controller.onControllerFocus();
            }
        } else if (!hasFocus && hasWindowFocus) {
            hasWindowFocus = false;

            if (state == STATE_RESUMED) {
                controller.onControllerBlur();
            }
        }
    }

    /**
     * Call this method from {@link Activity#finish()} before {@code super.finish()}.
     */
    public void finish() {
        // Allow to continue with STATE_INSTANCE_STATE_SAVED.
        if (state == STATE_DESTROYED || state == STATE_FINISHED) {
            return;
        }

        if (isPerformingPause || isPerformingSaveInstanceState) {
            isFinishPending = true;
            return;
        }

        if (state == STATE_RESUMED) {
            onPause();
        }

        if (state == STATE_STARTED) {
            onStop();
        }

        state = STATE_FINISHED;
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
}
