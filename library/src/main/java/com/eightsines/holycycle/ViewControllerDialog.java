package com.eightsines.holycycle;

import android.app.Dialog;
import android.support.annotation.Nullable;

public interface ViewControllerDialog extends ViewController {

    @Nullable
    Dialog onControllerCreateDialog();
}
