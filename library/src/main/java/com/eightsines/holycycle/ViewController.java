package com.eightsines.holycycle;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.eightsines.holycycle.app.ViewControllerActivity;
import com.eightsines.holycycle.app.ViewControllerAppCompatActivity;
import com.eightsines.holycycle.app.ViewControllerFragment;
import com.eightsines.holycycle.app.ViewControllerFragmentActivity;
import com.eightsines.holycycle.app.ViewControllerPlatformFragment;

/**
 * A view controller is an object, which manages piece of an application's user interface.
 * This is an abstraction over Activity or Fragment lifecycle, which makes it more straightforward and consistent.
 * <p>Most of time you should use {@link ViewControllerActivity}, {@link ViewControllerFragmentActivity},
 * {@link ViewControllerAppCompatActivity}, {@link ViewControllerFragment}, or {@link ViewControllerPlatformFragment}
 * instead of using this interface directly.</p>
 * <p>The new lifecycle is:</p>
 * <ol>
 * <li>{@link #onControllerCreate(Bundle extras)}</li>
 * <li>{@link #onControllerRestoreInstanceState(Bundle savedInstanceState)} (optional)</li>
 * <li>{@link #onControllerGetContentLayoutId()}</li>
 * <li>{@link #onControllerContentViewCreated()}</li>
 * <li>{@link #onControllerStart()}</li>
 * <li>{@link #onControllerResume()}</li>
 * <li>{@link #onControllerFocus()}</li>
 * <li>{@link #onControllerBlur()}</li>
 * <li>{@link #onControllerPause()}</li>
 * <li>{@link #onControllerPersistUserData()}</li>
 * <li>{@link #onControllerStop()}</li>
 * <li>{@link #onControllerSaveInstanceState(Bundle outState)} (optional)</li>
 * </ol>
 * <p>Every lifecycle callback is called exactly in this order.</p>
 */
public interface ViewController extends ViewControllerLifecycleObserver, ViewControllerLifecycleOwner {
    /**
     * Called when the view controller is creating. This is where most initialization should go
     * (you can think of this method as constructor replacement). Good place to inject dependencies.
     *
     * @param extras Intent extras (in case of Activity) or construction arguments (in case of Fragment).
     */
    void onControllerCreate(@Nullable Bundle extras);

    /**
     * This method mey be called after {@link #onControllerCreate(Bundle extras)}
     * (but before {@link #onControllerStart()}), when the view controller is being re-initialized
     * from a previously saved state, given here in <var>savedInstanceState</var>.
     * <p>Controller UI is not yet created, so you must not use {@link #findViewById(int id)} here or
     * modify UI in any other way (eg. showing dialogs or working with fragments).
     * It is better to save the necessary data in the class attributes.</p>
     * <p>If the view controller is started without previously saved state, this method will not be called.</p>
     *
     * @param savedInstanceState The data most recently supplied in {@link #onControllerSaveInstanceState(Bundle outState)}.
     */
    void onControllerRestoreInstanceState(@NonNull Bundle savedInstanceState);

    /**
     * Called when the view controller wants to create UI.
     * This is optional, and non-graphical view controllers can return zero (which is the default implementation).
     * This will be called between {@link #onControllerCreate(Bundle extras)} and {@link #onControllerStart()}.
     * <p>If this view controller based on Fragment, than in some cases this method can be called
     * after {@link #onControllerStop()}.</p>
     *
     * @return Return the layout id for the view controller UI, or zero for non-graphical view controller.
     */
    int onControllerGetContentLayoutId();

    /**
     * Called immediately after {@link #onControllerGetContentLayoutId()} has returned a non-zero layout id
     * (if {@link #onControllerGetContentLayoutId()} has returned zero, this method will not be called).
     * This gives subclasses a chance to initialize themselves once they know their view hierarchy
     * has been completely created.
     * <p>Good place to use {@link #findViewById(int id)} (if you don't already use kotlin extensions
     * or data binding) and create adapters for ListView or RecyclerView.</p>
     * <p>But you shouldn't register any {@link android.view.View} listeners (eg. click listeners)
     * here - use {@link #onControllerStart()} instead.</p>
     */
    void onControllerContentViewCreated();

    /**
     * This method may be called after {@link #onControllerStop()} to retrieve per-instance state
     * from this view controller. For more details see documentation
     * for {@link android.app.Activity#onSaveInstanceState(Bundle outState)}.
     * <p><em>onSaveInstanceState() in Activity or Fragment is not related to lifecycle, and can be called
     * at any time before onStop(), even before onPause() (or after onStop() in Android P). In contrast,
     * this method is always called after {@link #onControllerPause()} and {@link #onControllerStop()}.</em></p>
     *
     * @param outState Bundle in which to place your saved state.
     */
    void onControllerSaveInstanceState(@NonNull Bundle outState);

    /**
     * Returns the content view of this view controller, if non-zero layout id was returned from
     * {@link #onControllerGetContentLayoutId()}, otherwise returns {@code null}.
     *
     * @return The content view or {@code null}.
     */
    @SuppressWarnings("unused")
    @Nullable
    View getView();

    /**
     * Finds a view that was identified by the {@code android:id} XML attribute.
     *
     * @param id The ID to search for.
     * @return A view with given ID if found, or {@code null} otherwise.
     */
    @SuppressWarnings("unused")
    @Nullable
    <T extends View> T findViewById(@IdRes int id);
}
