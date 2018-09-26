package com.eightsines.holycycle;

import android.support.annotation.NonNull;

/**
 * Denotes that the specified class has a view controller lifecycle manager.
 */
public interface ViewControllerLifecycleOwner {
    /**
     * @return The view controller lifecycle manager.
     */
    @NonNull
    ViewControllerLifecycle getControllerLifecycle();
}
