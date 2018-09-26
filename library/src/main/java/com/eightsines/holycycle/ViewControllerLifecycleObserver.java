package com.eightsines.holycycle;

import android.os.Bundle;

/**
 * This interface represents the basic callbacks of the view controller lifecycle, which can be used not only
 * by the view controller itself, but also by third-party classes.
 * <p>Why this should be used instead of android architecture lifecycle observers? There are two main reasons:</p>
 * <ol>
 *     <li>To be in-sync with workaround for {@code onSaveInstanceState()} (main reason).</li>
 *     <li>To simplify view controller lifecycle observers (in our view android architecture lifecycle is
 *     slightly overcomplicated for regular applications).</li>
 * </ol>
 * <p>Basic lifecycle callbacks include:</p>
 * <ol>
 * <li>{@link #onControllerStart()}</li>
 * <li>{@link #onControllerResume()}</li>
 * <li>{@link #onControllerFocus()}</li>
 * <li>{@link #onControllerBlur()}</li>
 * <li>{@link #onControllerPause()}</li>
 * <li>{@link #onControllerPersistUserData()}</li>
 * <li>{@link #onControllerStop()}</li>
 * </ol>
 * <p>Every lifecycle callback is called exactly in this order.</p>
 */
public interface ViewControllerLifecycleObserver {
    /**
     * Called after {@link ViewController#onControllerGetContentLayoutId()}
     * (and after {@link ViewController#onControllerContentViewCreated()},
     * if {@link ViewController#onControllerGetContentLayoutId()} has returned a non-zero layout id).
     * When this method is called, it means that the view controller is ready for operation
     * (but not necessary active or even visible to user).
     * <p>Also can be called after {@link #onControllerStop()}, if the view controller is restarted.</p>
     * <p>Good place to register {@link android.view.View} listeners (eg. click listeners), subscribe
     * to observables or event bus, register broadcast receivers, and reflect last application state into UI.</p>
     * <p><em>NB. Internally this method is called directly from onStart() in Activity or Fragment,
     * so due to nature of Android, calling this method does NOT mean that this view controller is shown
     * to a user at first time (for example, Activity of Fragment can be destroyed and created
     * again after screen rotation).</em></p>
     */
    void onControllerStart();

    /**
     * Called after {@link #onControllerStart()} and mean that the view controller is ready to interact
     * with user.
     * <p>Keep in mind that this method doesn't mean that this view controller is actually visible
     * to user (a system window such as the keyguard may be in front).
     * Use {@link android.app.Activity#onWindowFocusChanged(boolean hasFocus)} to know for certain that your
     * view controller is visible to the user (for example, to resume a game).</p>
     * <p>Also can be called after {@link #onControllerPause()}, if the view controller is resuming after pause.</p>
     * <p>Good place to begin animations and open exclusive-access devices (such as the camera).</p>
     */
    void onControllerResume();

    /**
     * Called after {@link #onControllerResume()} when the view controller gains window focus.
     * This means that this view controller is active, visible to the user and not covered by any other window.
     * <p>In most cases, you do not need to override this method.</p>
     */
    void onControllerFocus();

    /**
     * Called after {@link #onControllerFocus()} when the view controller lost window focus.
     * This means that this view controller is still active, but not visible (or partially visible) to the user.
     * <p>In most cases, you do not need to override this method.</p>
     */
    void onControllerBlur();

    /**
     * Called when the view controller is going into the background (but has not (yet) been stopped and killed).
     * The counterpart to {@link #onControllerResume()}.
     * <p>Good place to stop animations and releasing exclusive-access devices (such as the camera).</p>
     */
    void onControllerPause();

    /**
     * Called immediately after {@link #onControllerPause()}. This is last change to persist important user
     * data, because after this method an application may be killed by Android without calling onStop
     * and onDestroy in Activities and Fragments (in terms of view controller - without calling
     * {@link #onControllerStop()} and {@link ViewController#onControllerSaveInstanceState(android.os.Bundle outState)}).
     */
    void onControllerPersistUserData();

    /**
     * Called when the view controller are no longer visible to the user. You will next receive either
     * {@link #onControllerStart()} (or {@link ViewController#onControllerGetContentLayoutId()} in case of Fragment),
     * {@link ViewController#onControllerSaveInstanceState(Bundle outState)} or nothing, depending on later user activity.
     * <p>Good place to unregister {@link android.view.View} listeners (eg. click listeners),
     * unsubscribe from observables or event bus, and unregister broadcast receivers.</p>
     * <p><em>Why you need to unregister listeners? See
     * <a href="https://stackoverflow.com/q/38368391">https://stackoverflow.com/q/38368391</a>.
     * Question answer suggests to disable button, but than you should re-enable it in
     * {@link #onControllerStart()}, because the view controller can be restarted. Unregistering listeners
     * seems more straightforward and consistent.</em></p>
     */
    void onControllerStop();
}
