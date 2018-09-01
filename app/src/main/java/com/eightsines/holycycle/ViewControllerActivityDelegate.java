package com.eightsines.holycycle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Helper class which transform Android-lifecycle to ViewController-lifecycle.
 * <p>If you can't use {@link ActivityViewController}, {@link FragmentActivityViewController},
 * or {@link AppCompatActivityViewController} (eg. you can't change base activity for some reason),
 * use this class. See the {@link ActivityViewController} for example of use.</p>
 */
public class ViewControllerActivityDelegate {
    private static final int STATE_PENDING_FINISH = -3;
    private static final int STATE_DESTROYED = -2;
    private static final int STATE_PERFORMING_PAUSE = -1;
    private static final int STATE_INITIALIZED = 0;
    private static final int STATE_CREATED = 1;
    private static final int STATE_STARTED = 2;
    private static final int STATE_RESUMED = 3;

    private Activity owner;
    private ViewController controller;
    private int state = STATE_INITIALIZED;
    private int contentLayoutResId;
    private boolean hasWindowFocus;

    /**
     * View controller delegate constructor. Mostly you want use it like
     * {@code new ViewControllerActivityDelegate(this, this)}, however it is possible to separate
     * host activity from the view controller.
     * <p>See the {@link ActivityViewController} for an example of use.</p>
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
        if (state <= STATE_DESTROYED) {
            return;
        }

        if (state != STATE_INITIALIZED) {
            throw new IllegalStateException("This should not happen, but onCreate() was called with an invalid state.");
        }

        state = STATE_CREATED;
        Intent intent = owner.getIntent();

        // Checking intent for null is redundant, but leaved here for the great justice.
        controller.onControllerCreate(intent == null ? null : intent.getExtras());

        if (state <= STATE_DESTROYED) {
            return;
        }

        if (savedInstanceState != null) {
            controller.onControllerRestoreInstanceState(savedInstanceState);

            if (state <= STATE_DESTROYED) {
                return;
            }
        }

        contentLayoutResId = controller.onControllerGetContentLayoutId();

        if (state > STATE_DESTROYED && contentLayoutResId != 0) {
            owner.setContentView(contentLayoutResId);
            controller.onControllerContentViewCreated();
        }
    }

    // onRestart() is not handled intentionally.

    /**
     * Call this method from {@link Activity#onStart()} after {@code super.onStart()}.
     */
    public void onStart() {
        if (state <= STATE_DESTROYED) {
            return;
        }

        if (state != STATE_CREATED) {
            throw new IllegalStateException(
                    "onStart() was called with an invalid state, perhaps you forgot to call onCreate()?");
        }

        state = STATE_STARTED;
        controller.onControllerStart();
    }

    /**
     * Call this method from {@link Activity#onResume()} after {@code super.onResume()}.
     */
    public void onResume() {
        if (state <= STATE_DESTROYED) {
            return;
        }

        if (state != STATE_STARTED) {
            throw new IllegalStateException(
                    "onResume() was called with an invalid state, perhaps you forgot to call onStart()? Otherwise, there is very little chance that this may be due to problems with onSaveInstanceState() in older versions of Android.");
        }

        state = STATE_RESUMED;
        controller.onControllerResume();

        if (state > STATE_DESTROYED && hasWindowFocus) {
            controller.onControllerFocus();
        }
    }

    /**
     * Call this method from {@link Activity#onPause()} before {@code super.onPause()}.
     */
    public void onPause() {
        if (state <= STATE_DESTROYED) {
            return;
        }

        if (state != STATE_RESUMED) {
            throw new IllegalStateException(
                    "onPause() was called with an invalid state, perhaps you forgot to call onResume()?");
        }

        state = STATE_PERFORMING_PAUSE;

        if (hasWindowFocus) {
            controller.onControllerBlur();
        }

        controller.onControllerPause();
        controller.onControllerPersistUserData();

        if (state == STATE_PENDING_FINISH) {
            state = STATE_STARTED;
            finish();
        } else {
            state = STATE_STARTED;
        }
    }

    /**
     * Call this method from {@link Activity#onStop()} before {@code super.onStop()}.
     */
    public void onStop() {
        if (state <= STATE_DESTROYED) {
            return;
        }

        if (state != STATE_STARTED) {
            throw new IllegalStateException(
                    "onStop() was called with an invalid state, perhaps you forgot to call onPause()?");
        }

        state = STATE_CREATED;
        controller.onControllerStop();
    }

    /**
     * Call this method from {@link Activity#onDestroy()} before {@code super.onDestroy()}.
     */
    public void onDestroy() {
        if (state <= STATE_DESTROYED) {
            return;
        }

        if (state != STATE_CREATED) {
            throw new IllegalStateException(
                    "onDestroy() was called with an invalid state, perhaps you forgot to call onStop()?");
        }

        state = STATE_DESTROYED;
    }

    /**
     * Call this method from {@link Activity#onSaveInstanceState(Bundle outState)}
     * after {@code super.onSaveInstanceState(Bundle outState)}.
     */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (state <= STATE_DESTROYED) {
            return;
        }

        if (state == STATE_RESUMED) {
            onPause();
        }

        if (state == STATE_STARTED) {
            onStop();
        }

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
        if (state <= STATE_DESTROYED) {
            return;
        }

        if (state < STATE_INITIALIZED) {
            state = STATE_PENDING_FINISH;
            return;
        }

        if (state == STATE_RESUMED) {
            onPause();
        }

        if (state == STATE_STARTED) {
            onStop();
        }

        if (state == STATE_CREATED) {
            onDestroy();
        } else {
            // There is a little chance that finish() can be called outside view controller in host activity.
            state = STATE_DESTROYED;
        }
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
