package com.eightsines.holycycle;

/**
 * Denotes that the specified class tracks the view controller lifecycle state.
 */
public interface ViewControllerLifecycleTracker {
    /**
     * Compares if current lifecycle state of the view controller is greater or equal to the given {@code state}.
     * <p><em>Note: There is no something like {@code int getControllerState()} - this is intentionally.</em></p>
     *
     * @param state State to compare with.
     * @return true if current lifecycle state of the view controller is greater or equal to the given {@code state}.
     */
    boolean isControllerStateAtLeast(int state);
}
