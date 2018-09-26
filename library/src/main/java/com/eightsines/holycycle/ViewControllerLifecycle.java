package com.eightsines.holycycle;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * A view controller lifecycle manager is an object that knows the current view controller lifecycle state
 * and allows you to add or remove child view controller lifecycle observers.
 * <p><em>Note: this interface called ViewControllerLifecycle instead of ViewControllerLifecycleManager
 * to be more in-sync with android architecture lifecycle. This can be confusing, since
 * "view controller lifecycle" is just a concept, but the "view controller lifecycle manager" means
 * a specific interface.</em></p>
 */
public interface ViewControllerLifecycle extends ViewControllerLifecycleTracker {
    /**
     * The view controller goes into the started state immediately before
     * {@link ViewControllerLifecycleObserver#onControllerStart()} and exits right before
     * {@link ViewControllerLifecycleObserver#onControllerStop()}.
     * <p><em>Note: In contrast, android.arch.lifecycle.Lifecycle goes into the
     * {@link android.arch.lifecycle.Lifecycle.State#STARTED} state <strong>after</strong>
     * {@link Activity#onStart()}</em></p>
     */
    int STATE_STARTED = 1;

    /**
     * The view controller goes into the resumed state immediately before
     * {@link ViewControllerLifecycleObserver#onControllerResume()} and exits right before
     * {@link ViewControllerLifecycleObserver#onControllerPause()}.
     * <p><em>Note: In contrast, android.arch.lifecycle.Lifecycle goes into the
     * {@link android.arch.lifecycle.Lifecycle.State#RESUMED} state <strong>after</strong>
     * {@link Activity#onResume()}</em></p>
     */
    int STATE_RESUMED = 2;

    /**
     * The view controller goes into the focused state immediately before
     * {@link ViewControllerLifecycleObserver#onControllerFocus()} and exits right before
     * {@link ViewControllerLifecycleObserver#onControllerBlur()}.
     */
    int STATE_FOCUSED = 3;

    /**
     * Adds a child {@link ViewControllerLifecycleObserver} that will be notified when the owner
     * {@link ViewControllerLifecycleObserver} changes state. If owner is already started, resumed or focused,
     * appropriate callbacks will be called in child.
     * <p>There is no guarantee that observers will be added in the same order as the method was called.</p>
     *
     * @param observer The observer to notify.
     */
    void addControllerLifecycleObserver(@NonNull ViewControllerLifecycleObserver observer);

    /**
     * Removes the given observer from the observers list. If owner is not yet blurred, paused or focused,
     * appropriate callbacks will be called in child.
     * <p>There is no guarantee that observers will be removed in the same order as the method was called.</p>
     * <p><em>Note: In contrast, android.arch.lifecycle.Lifecycle will <strong>not</strong> call child
     * callbacks when removing it. They do this for a strong reason, however this library works in
     * a different way.</em></p>
     *
     * @param observer The observer to be removed.
     */
    void removeControllerLifecycleObserver(@NonNull ViewControllerLifecycleObserver observer);
}
