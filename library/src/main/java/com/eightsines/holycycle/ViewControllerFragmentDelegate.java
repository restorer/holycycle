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
public class ViewControllerFragmentDelegate implements ViewControllerLifecycleOwner, ViewControllerLifecycleTracker {
    private static final int HOST_DESTROYED = -3;
    private static final int HOST_INSTANCE_STATE_SAVED = -2;
    private static final int HOST_STOPPED = -1;
    private static final int HOST_INITIALIZED = 0;
    private static final int HOST_ATTACHED = 1;
    private static final int HOST_CREATED = 2;
    private static final int HOST_VIEW_CREATED = 3;
    private static final int HOST_STARTED = 4;
    private static final int HOST_RESUMED = 5;

    private ViewController controller;
    private ViewControllerLifecycleRegistry registry;
    private int hostState = HOST_INITIALIZED;
    private View contentView;
    private boolean hasWindowFocus;

    private ViewTreeObserver.OnWindowFocusChangeListener windowFocusChangeListener = new ViewTreeObserver.OnWindowFocusChangeListener() {
        @Override
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
    public ViewControllerFragmentDelegate(@NonNull ViewController controller) {
        this.controller = controller;

        registry = new ViewControllerLifecycleRegistry(this);
    }

    /**
     * Call this method from {@link Fragment#onAttach(Context context)}
     * after {@code super.onAttach(context)}.
     */
    public void onAttach() {
        // HOST_ATTACHED - This method may be called twice for platform fragments on newer APIs.
        if (hostState == HOST_DESTROYED || hostState == HOST_ATTACHED) {
            return;
        }

        if (hostState != HOST_INITIALIZED && hostState != HOST_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException(
                    "This should not happen, but onAttach() was called with an invalid hostState ("
                            + hostState
                            + ").");
        }

        hostState = HOST_ATTACHED;
    }

    /**
     * Call this method from {@link Fragment#onCreate(Bundle savedInstanceState)}
     * after {@code super.onCreate(savedInstanceState)}.
     *
     * @param savedInstanceState Pass {@code savedInstanceState} parameter here.
     * @param arguments Pass {@code getArguments()} here.
     */
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable Bundle arguments) {
        if (hostState == HOST_DESTROYED) {
            return;
        }

        if (hostState != HOST_ATTACHED) {
            throw new IllegalStateException(
                    "onCreate() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onAttach()?");
        }

        hostState = HOST_CREATED;
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
    public View onCreateView(@NonNull LayoutInflater inflater,
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
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            boolean isPlatformFragment) {

        if (hostState == HOST_DESTROYED) {
            return null;
        }

        // HOST_ATTACHED - If fragment is retained, this method can be called directly after onAttach();
        // HOST_CREATED - Normal flow;
        // HOST_STOPPED - Just for case, actually this should never happen;
        // HOST_INSTANCE_STATE_SAVED - Also just for case.
        if (hostState != HOST_ATTACHED
                && hostState != HOST_CREATED
                && hostState != HOST_STOPPED
                && hostState != HOST_INSTANCE_STATE_SAVED) {

            throw new IllegalStateException(
                    "onCreateView() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onCreate()?");
        }

        hostState = HOST_VIEW_CREATED;

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
        if (hostState == HOST_DESTROYED) {
            return;
        }

        if (hostState != HOST_VIEW_CREATED) {
            throw new IllegalStateException(
                    "onViewCreated() was called with an invalid hostState ("
                            + hostState
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
        if (hostState == HOST_DESTROYED) {
            return;
        }

        // HOST_VIEW_CREATED - Normal flow;
        // HOST_STOPPED - After onStop();
        // HOST_INSTANCE_STATE_SAVED - Should not happen, but handled for the great justice.
        if (hostState != HOST_VIEW_CREATED && hostState != HOST_STOPPED && hostState != HOST_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException(
                    "onStart() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onCreateView()?");
        }

        hostState = HOST_STARTED;

        controller.onControllerStart();
        registry.onControllerStart();
    }

    /**
     * Call this method from {@link Fragment#onResume()}
     * after {@code super.onResume()}.
     */
    public void onResume() {
        if (hostState == HOST_DESTROYED) {
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

        if (hasWindowFocus) {
            controller.onControllerFocus();
            registry.onControllerFocus();
        }
    }

    /**
     * Call this method from {@link Fragment#onPause()}
     * before {@code super.onPause()}.
     */
    public void onPause() {
        if (hostState == HOST_DESTROYED || hostState == HOST_INSTANCE_STATE_SAVED) {
            return;
        }

        if (hostState != HOST_RESUMED) {
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
     * Call this method from {@link Fragment#onStop()}
     * before {@code super.onStop()}.
     */
    public void onStop() {
        if (hostState == HOST_DESTROYED || hostState == HOST_INSTANCE_STATE_SAVED) {
            return;
        }

        if (hostState != HOST_STARTED) {
            throw new IllegalStateException(
                    "onStop() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onPause()?");
        }

        hostState = HOST_STOPPED;

        registry.onControllerStop();
        controller.onControllerStop();
    }

    /**
     * Call this method from {@link Fragment#onDestroyView()}
     * before {@code super.onDestroyView()}.
     */
    public void onDestroyView() {
        if (hostState == HOST_DESTROYED) {
            return;
        }

        // According to https://github.com/xxv/android-lifecycle, hostState can't be HOST_VIEW_CREATED,
        // but we still handle it for the great justice.
        if (hostState != HOST_STOPPED && hostState != HOST_VIEW_CREATED && hostState != HOST_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException(
                    "onDestroyView() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onStop()?");
        }

        hostState = HOST_CREATED;

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
        if (hostState == HOST_DESTROYED) {
            return;
        }

        // HOST_INSTANCE_STATE_SAVED - Should not happen, but handled for the great justice.
        if (hostState != HOST_CREATED && hostState != HOST_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException(
                    "onDestroy() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onDestroyView()? Otherwise, there is very little chance that this may be due to problems with onDestroyView() in some versions of Android.");
        }

        hostState = HOST_DESTROYED;
    }

    /**
     * Call this method from {@link Fragment#onDetach()}
     * before {@code super.onDetach()}.
     */
    public void onDetach() {
        // HOST_CREATED - If fragment is retained, this method can be called directly after onDestroyView();
        // HOST_DESTROYED - Normal flow;
        // HOST_INSTANCE_STATE_SAVED - Should not happen, but handled for the great justice.
        if (hostState != HOST_CREATED && hostState != HOST_DESTROYED && hostState != HOST_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException(
                    "onDetach() was called with an invalid hostState ("
                            + hostState
                            + "), perhaps you forgot to call onDestroy()? Otherwise, there is very little chance that this may be due to problems with onDestroyView() in some versions of Android.");
        }

        hostState = HOST_INITIALIZED;
    }

    /**
     * Call this method from {@link Fragment#onSaveInstanceState(Bundle outState)}
     * after {@code super.onSaveInstanceState(Bundle outState)}.
     */
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // Allow to continue with HOST_INSTANCE_STATE_SAVED.
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
    @Nullable
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
