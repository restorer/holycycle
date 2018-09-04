package com.eightsines.holycycle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import com.eightsines.holycycle.app.ViewControllerFragment;
import com.eightsines.holycycle.app.ViewControllerPlatformFragment;

/**
 * Helper class which transform Android-lifecycle to ViewController-lifecycle.
 * <p>If you can't use {@link ViewControllerFragment} or {@link ViewControllerPlatformFragment},
 * (eg. you can't change base fragment for some reason), use this class.
 * See {@link ViewControllerFragment} for example of usage - it is pretty simple.</p>
 */
@SuppressLint("NewApi")
public class ViewControllerFragmentDelegate {
    private static final int STATE_DESTROYED = -3;
    private static final int STATE_INSTANCE_STATE_SAVED = -2;
    private static final int STATE_STOPPED = -1;
    private static final int STATE_INITIALIZED = 0;
    private static final int STATE_ATTACHED = 1;
    private static final int STATE_CREATED = 2;
    private static final int STATE_VIEW_CREATED = 3;
    private static final int STATE_STARTED = 4;
    private static final int STATE_RESUMED = 5;

    private ViewController controller;
    private int state = STATE_INITIALIZED;
    private View contentView;
    private boolean hasWindowFocus;

    private ViewTreeObserver.OnWindowFocusChangeListener windowFocusChangeListener = new ViewTreeObserver.OnWindowFocusChangeListener() {
        @Override
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
    };

    /**
     * View controller delegate constructor. Mostly you want use it like
     * {@code new ViewControllerFragmentDelegate(this)}, however it is possible to separate
     * host fragment from the view controller.
     * <p>See the {@link ViewControllerFragment} for an example of use.</p>
     *
     * @param controller Managed view controller.
     */
    @SuppressWarnings("WeakerAccess")
    public ViewControllerFragmentDelegate(ViewController controller) {
        this.controller = controller;
    }

    /**
     * Call this method from {@link Fragment#onAttach(Context context)}
     * after {@code super.onAttach(context)}.
     */
    public void onAttach() {
        if (state == STATE_DESTROYED) {
            return;
        }

        if (state != STATE_INITIALIZED && state != STATE_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException("This should not happen, but onAttach() was called with an invalid state ("
                    + state
                    + ").");
        }

        state = STATE_ATTACHED;
    }

    /**
     * Call this method from {@link Fragment#onCreate(Bundle savedInstanceState)}
     * after {@code super.onCreate(savedInstanceState)}.
     *
     * @param savedInstanceState Pass {@code savedInstanceState} parameter here.
     * @param arguments Pass {@code getArguments()} here.
     */
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable Bundle arguments) {
        if (state == STATE_DESTROYED) {
            return;
        }

        if (state != STATE_ATTACHED) {
            throw new IllegalStateException(
                    "onCreate() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onAttach()?");
        }

        state = STATE_CREATED;
        controller.onControllerCreate(arguments);

        if (savedInstanceState != null) {
            controller.onControllerRestoreInstanceState(savedInstanceState);
        }
    }

    /**
     * Call this method from
     * {@link Fragment#onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)}.
     * <p><em>This method is intended for support fragments. For platform fragments use
     * {@link #onCreateView(LayoutInflater inflater, ViewGroup container, boolean isPlatformFragment)}</em></p>
     *
     * @param inflater Pass {@code inflater} parameter here.
     * @param container Pass {@code container} parameter here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container) {

        return onCreateView(inflater, container, false);
    }

    /**
     * Call this method from
     * {@link Fragment#onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)}.
     * <p><em>This method is intended for platform fragments. For support fragments use
     * {@link #onCreateView(LayoutInflater inflater, ViewGroup container)}</em></p>
     *
     * @param inflater Pass {@code inflater} parameter here.
     * @param container Pass {@code container} parameter here.
     * @param isPlatformFragment Pass {@code true} is this delegate is used in platform fragment (android.app.Fragment)
     * @return Return the View for the fragment's UI, or null.
     */
    @SuppressLint("ObsoleteSdkInt")
    @Nullable
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container,
            boolean isPlatformFragment) {

        if (state == STATE_DESTROYED) {
            return null;
        }

        // STATE_ATTACHED - If fragment is retained, this method can be called directly after onAttach();
        // STATE_CREATED - Normal flow;
        // STATE_STOPPED - Just for case, actually this should never happen;
        // STATE_INSTANCE_STATE_SAVED - Also just for case.
        if (state != STATE_ATTACHED
                && state != STATE_CREATED
                && state != STATE_STOPPED
                && state != STATE_INSTANCE_STATE_SAVED) {

            throw new IllegalStateException(
                    "onCreateView() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onCreate()?");
        }

        state = STATE_VIEW_CREATED;

        if (contentView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            contentView.getViewTreeObserver().removeOnWindowFocusChangeListener(windowFocusChangeListener);
        }

        contentView = null;
        hasWindowFocus = false;

        int contentLayoutResId = controller.onControllerGetContentLayoutId();

        if (contentLayoutResId == 0) {
            // Assume that non-graphical view controller always has focus.
            hasWindowFocus = true;
            return null;
        }

        contentView = inflater.inflate(contentLayoutResId, container, false);

        if (isPlatformFragment && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            controller.onControllerContentViewCreated();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            contentView.getViewTreeObserver().addOnWindowFocusChangeListener(windowFocusChangeListener);
        } else {
            // TODO:
            //
            // There are problems with implementing this for fragments and API < 18. Solutions:
            // a) pass onWindowFocusChanged() from Activity;
            // b) wrap fragment layout into custom View, which will handle onWindowFocusChanged();
            // c) set new Window.Callback, which wraps existing (but beware that Activity can set new callbacks in some cases).

            hasWindowFocus = true;
        }

        return contentView;
    }

    /**
     * Call this method from {@link Fragment#onViewCreated(View view, Bundle savedInstanceState)}
     * after {@code super.onViewCreated(view, savedInstanceState)}.
     */
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR2)
    public void onViewCreated() {
        if (state == STATE_DESTROYED) {
            return;
        }

        if (state != STATE_VIEW_CREATED) {
            throw new IllegalStateException(
                    "onViewCreated() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onCreateView()?");
        }

        controller.onControllerContentViewCreated();
    }

    // onActivityCreated(Bundle savedInstanceState) and onViewStateRestored(Bundle savedInstanceState) are not handled intentionally.

    /**
     * Call this method from {@link Fragment#onStart()}
     * after {@code super.onStart()}.
     */
    public void onStart() {
        if (state == STATE_DESTROYED) {
            return;
        }

        // STATE_VIEW_CREATED - Normal flow;
        // STATE_STOPPED - After onStop();
        // STATE_INSTANCE_STATE_SAVED - Should not happen, but handled for the great justice.
        if (state != STATE_VIEW_CREATED && state != STATE_STOPPED && state != STATE_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException(
                    "onStart() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onCreateView()?");
        }

        state = STATE_STARTED;
        controller.onControllerStart();
    }

    /**
     * Call this method from {@link Fragment#onResume()}
     * after {@code super.onResume()}.
     */
    public void onResume() {
        if (state == STATE_DESTROYED) {
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

        if (hasWindowFocus) {
            controller.onControllerFocus();
        }
    }

    /**
     * Call this method from {@link Fragment#onPause()}
     * before {@code super.onPause()}.
     */
    public void onPause() {
        if (state == STATE_DESTROYED || state == STATE_INSTANCE_STATE_SAVED) {
            return;
        }

        if (state != STATE_RESUMED) {
            throw new IllegalStateException(
                    "onPause() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onResume()?");
        }

        state = STATE_STARTED;

        if (hasWindowFocus) {
            controller.onControllerBlur();
        }

        controller.onControllerPause();
        controller.onControllerPersistUserData();
    }

    /**
     * Call this method from {@link Fragment#onStop()}
     * before {@code super.onStop()}.
     */
    public void onStop() {
        if (state == STATE_DESTROYED || state == STATE_INSTANCE_STATE_SAVED) {
            return;
        }

        if (state != STATE_STARTED) {
            throw new IllegalStateException(
                    "onStop() was called with an invalid state (" + state + "), perhaps you forgot to call onPause()?");
        }

        state = STATE_STOPPED;
        controller.onControllerStop();
    }

    /**
     * Call this method from {@link Fragment#onDestroyView()}
     * before {@code super.onDestroyView()}.
     */
    public void onDestroyView() {
        if (state == STATE_DESTROYED) {
            return;
        }

        // According to https://github.com/xxv/android-lifecycle, state can't be STATE_VIEW_CREATED,
        // but we still handle it for the great justice.
        if (state != STATE_STOPPED && state != STATE_VIEW_CREATED && state != STATE_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException(
                    "onDestroyView() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onStop()?");
        }

        state = STATE_CREATED;

        if (contentView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            contentView.getViewTreeObserver().removeOnWindowFocusChangeListener(windowFocusChangeListener);
        }

        contentView = null;
    }

    /**
     * Call this method from {@link Fragment#onDestroy()}
     * before {@code super.onDestroy()}.
     */
    public void onDestroy() {
        if (state == STATE_DESTROYED) {
            return;
        }

        // STATE_INSTANCE_STATE_SAVED - Should not happen, but handled for the great justice.
        if (state != STATE_CREATED && state != STATE_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException(
                    "onDestroy() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onDestroyView()? Otherwise, there is very little chance that this may be due to problems with onDestroyView() in some versions of Android.");
        }

        state = STATE_DESTROYED;
    }

    /**
     * Call this method from {@link Fragment#onDetach()}
     * before {@code super.onDetach()}.
     */
    public void onDetach() {
        // STATE_CREATED - If fragment is retained, this method can be called directly after onDestroyView();
        // STATE_DESTROYED - Normal flow;
        // STATE_INSTANCE_STATE_SAVED - Should not happen, but handled for the great justice.
        if (state != STATE_CREATED && state != STATE_DESTROYED && state != STATE_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException(
                    "onDetach() was called with an invalid state ("
                            + state
                            + "), perhaps you forgot to call onDestroy()? Otherwise, there is very little chance that this may be due to problems with onDestroyView() in some versions of Android.");
        }

        state = STATE_INITIALIZED;
    }

    /**
     * Call this method from {@link Fragment#onSaveInstanceState(Bundle outState)}
     * after {@code super.onSaveInstanceState(Bundle outState)}.
     */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Allow to continue with STATE_INSTANCE_STATE_SAVED.
        if (state == STATE_DESTROYED) {
            return;
        }

        if (state == STATE_RESUMED) {
            onPause();
        }

        if (state == STATE_STARTED) {
            onStop();
        }

        state = STATE_INSTANCE_STATE_SAVED;
        controller.onControllerSaveInstanceState(outState);
    }

    /**
     * Pass return value from this method to {@link ViewController#getView()}.
     * <p><em>This method makes sense only for platform fragments (android.app.Fragment), because
     * there is no onViewCreated callback on API lower than 13, and this class emulate it
     * (see {@link #onCreateView(LayoutInflater inflater, ViewGroup container, boolean isPlatformFragment)}).
     * To ensure that {@link ViewController#onControllerContentViewCreated()} can use
     * {@link ViewController#getView()} and {@link ViewController#findViewById(int id)},
     * custom getView() method must be used.</em></p>
     *
     * @return The content view or {@code null}.
     */
    public View getView() {
        return contentView;
    }

    /**
     * Pass return value from this method to {@link ViewController#findViewById(int id)}.
     *
     * @param id Pass {@code id} parameter here.
     * @return A view with given ID if found, or {@code null} otherwise.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends View> T findViewById(int id) {
        return (contentView == null ? null : (T)contentView.findViewById(id));
    }
}
