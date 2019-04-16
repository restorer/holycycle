package com.eightsines.holycycle;

import android.app.Dialog;
import android.support.annotation.NonNull;
import com.eightsines.holycycle.app.ViewControllerDialogFragment;

/**
 * Created by vain onnellinen on 4/16/19.
 */
public class ViewControllerDialogFragmentDelegate extends ViewControllerFragmentDelegate {
    private ViewControllerDialog controller;

    /**
     * View controller delegate constructor. Mostly you want use it like
     * {@code new ViewControllerDialogFragmentDelegate(this)}, however it is possible to separate
     * host fragment from the view controller.
     * <p>See the {@link ViewControllerDialogFragment} for an example of use.</p>
     *
     * @param controller Managed view controller.
     */
    public ViewControllerDialogFragmentDelegate(@NonNull ViewControllerDialog controller) {
        super(controller);
        this.controller = controller;
    }

    public Dialog onCreateDialog() {
        if (state == STATE_DESTROYED) {
            return null;
        }

        if (state != STATE_CREATED && state != STATE_VIEW_CREATED && state != STATE_STARTED && state != STATE_INSTANCE_STATE_SAVED) {
            throw new IllegalStateException("onCreateDialog() was called with an invalid state ("
                    + state
                    + "), perhaps you forgot to call onCreate()?");
        }

        return controller.onControllerCreateDialog();
    }
}
