package com.eightsines.holycycle.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.mockito.Mockito;

public final class TestUtils {
    public static final String BUNDLE_EXTRAS = "extras";
    public static final String BUNDLE_SAVED_INSTANCE_STATE = "savedInstanceState";
    public static final String BUNDLE_OUT_STATE = "outState";

    private TestUtils() {
    }

    public static Bundle createMockBundle(@NonNull String name) {
        Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.toString()).thenReturn("Bundle#" + name);
        return bundle;
    }

    public static Intent createMockIntent(@Nullable Bundle extras) {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.getExtras()).thenReturn(extras);
        return intent;
    }
}
