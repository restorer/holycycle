package com.eightsines.holycycle;

import android.support.annotation.NonNull;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Default implementation of the {@link ViewControllerLifecycle}. In almost all cases should not be used
 * in user code, but has public access for possible special cases.
 */
public class ViewControllerLifecycleRegistry implements ViewControllerLifecycle {
    private ViewControllerLifecycleTracker tracker;
    private List<ViewControllerLifecycleObserver> observers = new ArrayList<>();
    private Deque<ViewControllerLifecycleObserver> pendingAdd = new ArrayDeque<>();
    private Deque<ViewControllerLifecycleObserver> pendingRemove = new ArrayDeque<>();
    private boolean isHandlingEvent;

    /**
     * View controller lifecycle registry constructor. In almost all cases should not be used in user code.
     *
     * @param tracker The view controller lifecycle tracker (probably {@link ViewControllerActivityDelegate} or {@link ViewControllerFragmentDelegate}).
     */
    @SuppressWarnings("WeakerAccess")
    public ViewControllerLifecycleRegistry(@NonNull ViewControllerLifecycleTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public boolean isControllerStateAtLeast(int state) {
        return tracker.isControllerStateAtLeast(state);
    }

    @Override
    public void addControllerLifecycleObserver(@NonNull ViewControllerLifecycleObserver observer) {
        if (pendingRemove.contains(observer)) {
            pendingRemove.remove(observer);
            return;
        }

        if (observers.contains(observer) || pendingAdd.contains(observer)) {
            return;
        }

        if (isHandlingEvent) {
            pendingAdd.add(observer);
        } else {
            performAdd(observer);
        }
    }

    @Override
    public void removeControllerLifecycleObserver(@NonNull ViewControllerLifecycleObserver observer) {
        if (pendingAdd.contains(observer)) {
            pendingAdd.remove(observer);
            return;
        }

        if (!observers.contains(observer) && !pendingRemove.contains(observer)) {
            return;
        }

        if (isHandlingEvent) {
            pendingRemove.add(observer);
        } else {
            performRemove(observer);
        }
    }

    /**
     * Should be called right after {@link ViewControllerLifecycleObserver#onControllerStart()} in owner observer.
     */
    public void onControllerStart() {
        isHandlingEvent = true;

        for (ViewControllerLifecycleObserver observer : observers) {
            observer.onControllerStart();
        }

        executePendingActions();
    }

    /**
     * Should be called right after {@link ViewControllerLifecycleObserver#onControllerResume()} in owner observer.
     */
    public void onControllerResume() {
        isHandlingEvent = true;

        for (ViewControllerLifecycleObserver observer : observers) {
            observer.onControllerResume();
        }

        executePendingActions();
    }

    /**
     * Should be called right after {@link ViewControllerLifecycleObserver#onControllerFocus()} in owner observer.
     */
    public void onControllerFocus() {
        isHandlingEvent = true;

        for (ViewControllerLifecycleObserver observer : observers) {
            observer.onControllerFocus();
        }

        executePendingActions();
    }

    /**
     * Should be called immediately before {@link ViewControllerLifecycleObserver#onControllerFocus()}
     * in owner observer.
     */
    public void onControllerBlur() {
        isHandlingEvent = true;

        for (ViewControllerLifecycleObserver observer : observers) {
            observer.onControllerBlur();
        }

        executePendingActions();
    }

    /**
     * Should be called immediately before {@link ViewControllerLifecycleObserver#onControllerPause()}
     * in owner observer.
     */
    public void onControllerPause() {
        isHandlingEvent = true;

        for (ViewControllerLifecycleObserver observer : observers) {
            observer.onControllerPause();
        }

        executePendingActions();
    }

    /**
     * Should be called immediately before {@link ViewControllerLifecycleObserver#onControllerPersistUserData()}
     * in owner observer.
     */
    public void onControllerPersistUserData() {
        isHandlingEvent = true;

        for (ViewControllerLifecycleObserver observer : observers) {
            observer.onControllerPersistUserData();
        }

        executePendingActions();
    }

    /**
     * Should be called immediately before {@link ViewControllerLifecycleObserver#onControllerStop()}
     * in owner observer.
     */
    public void onControllerStop() {
        isHandlingEvent = true;

        for (ViewControllerLifecycleObserver observer : observers) {
            observer.onControllerStop();
        }

        executePendingActions();
    }

    private void executePendingActions() {
        for (; ; ) {
            // pollLast() is used, because it should be faster than poll() in ArrayDeque
            ViewControllerLifecycleObserver observer = pendingRemove.pollLast();

            if (observer != null) {
                performRemove(observer);
                continue;
            }

            observer = pendingAdd.pollLast();

            if (observer != null) {
                performAdd(observer);
                continue;
            }

            break;
        }

        isHandlingEvent = false;
    }

    private void performAdd(@NonNull ViewControllerLifecycleObserver observer) {
        observers.add(observer);

        if (tracker.isControllerStateAtLeast(ViewControllerLifecycle.STATE_STARTED)) {
            observer.onControllerStart();
        }

        if (tracker.isControllerStateAtLeast(ViewControllerLifecycle.STATE_RESUMED)) {
            observer.onControllerResume();
        }

        if (tracker.isControllerStateAtLeast(ViewControllerLifecycle.STATE_FOCUSED)) {
            observer.onControllerFocus();
        }
    }

    private void performRemove(@NonNull ViewControllerLifecycleObserver observer) {
        observers.remove(observer);

        if (tracker.isControllerStateAtLeast(ViewControllerLifecycle.STATE_FOCUSED)) {
            observer.onControllerBlur();
        }

        if (tracker.isControllerStateAtLeast(ViewControllerLifecycle.STATE_RESUMED)) {
            observer.onControllerPause();
        }

        if (tracker.isControllerStateAtLeast(ViewControllerLifecycle.STATE_STARTED)) {
            observer.onControllerStop();
        }
    }
}
